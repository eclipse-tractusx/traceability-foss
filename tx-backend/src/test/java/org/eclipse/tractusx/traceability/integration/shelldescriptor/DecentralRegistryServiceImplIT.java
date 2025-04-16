/********************************************************************************
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package org.eclipse.tractusx.traceability.integration.shelldescriptor;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;

import java.util.List;
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportState;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.ProcessingState;
import org.eclipse.tractusx.traceability.configuration.domain.model.OrderConfiguration;
import org.eclipse.tractusx.traceability.configuration.infrastructure.model.OrderEntity;
import org.eclipse.tractusx.traceability.configuration.infrastructure.model.TriggerConfigurationEntity;
import org.eclipse.tractusx.traceability.configuration.infrastructure.repository.OrderJPARepository;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AssetsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.ConfigurationSupport;
import org.eclipse.tractusx.traceability.integration.common.support.IrsApiSupport;
import org.eclipse.tractusx.traceability.shelldescriptor.application.DecentralRegistryService;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DecentralRegistryServiceImplIT extends IntegrationTestSpecification {

    @Autowired
    ConfigurationSupport configurationSupport;

    @Autowired
    OrderJPARepository orderJPARepository;

    @Autowired
    AssetsSupport assetsSupport;

    @Autowired
    IrsApiSupport irsApiSupport;

    @Autowired
    DecentralRegistryService service;

    @Test
    void shouldUpdateRegisterOrderCron() throws JoseException {
        // given
        final int ttl = 604800000; // 1 week

        assetsSupport.defaultAssetsStored();
        irsApiSupport.irsApiTriggerOrder();
        irsApiSupport.irsApiReturnsOrderAndBatchDetails();
        irsApiSupport.irsApiReturnsJobDetails();
        configurationSupport.storeTriggerConfiguration(TriggerConfigurationEntity.builder()
                .cronExpressionRegisterOrderTTLReached("* * * * * ?")
                .aasTTL(ttl).build());
        OrderConfiguration orderConfiguration = OrderConfiguration.builder().build();
        service.registerOrdersForExpiredAssets(orderConfiguration);

        final String orderId = "ebb79c45-7bba-4169-bf17-3e719989ab54";

        // when
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .log()
                .all()
                .when()
                .get("/api/registry/reload")
                .then()
                .statusCode(200);

        // then
        List<OrderEntity> all = orderJPARepository.findAll();
        assertThat(all).hasSize(1);
        assertThat(all.get(0).getStatus()).isEqualTo(ProcessingState.INITIALIZED);

        assetsSupport.findAll().forEach(asset ->
                assertThat(asset.getImportState()).isEqualTo(ImportState.IN_SYNCHRONIZATION));

        // then
        IrsApiSupport.sendCallback(orderId, "ebb79c45-7bba-4169-bf17-3e719989ab55", "PROCESSING", "COMPLETED", 200);

        List<OrderEntity> allAfterCallback = orderJPARepository.findAll();
        assertThat(allAfterCallback).hasSize(1);
        assertThat(allAfterCallback.get(0).getStatus()).isEqualTo(ProcessingState.PROCESSING);

        AssetAsBuiltEntity assetFromCallbackMockError = assetsSupport.findById(
                "urn:uuid:b978ad2d-be06-47ea-a578-580d9b2eca77");
        assertThat(assetFromCallbackMockError.getTtl()).isNull();
        assertThat(assetFromCallbackMockError.getExpirationDate()).isNull();
    }
}
