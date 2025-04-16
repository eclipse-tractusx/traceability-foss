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

package org.eclipse.tractusx.traceability.integration.orders;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;

import io.restassured.http.ContentType;
import java.util.List;
import orders.request.CreateOrderRequest;
import org.eclipse.tractusx.irs.component.PartChainIdentificationKey;
import org.eclipse.tractusx.traceability.aas.infrastructure.model.AASEntity;
import org.eclipse.tractusx.traceability.aas.infrastructure.repository.JpaAASRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportState;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.OrderRepositoryImpl;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.ProcessingState;
import org.eclipse.tractusx.traceability.configuration.infrastructure.model.OrderEntity;
import org.eclipse.tractusx.traceability.configuration.infrastructure.repository.OrderJPARepository;
import org.eclipse.tractusx.traceability.cron.domain.UpdateAssetsAfterOrderCompletionJobRegistration;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AssetsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.IrsApiSupport;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class OrderControllerIT extends IntegrationTestSpecification {

    @Autowired
    OrderJPARepository orderJPARepository;

    @Autowired
    IrsApiSupport irsApiSupport;

    @Autowired
    AssetsSupport assetsSupport;

    @Autowired
    UpdateAssetsAfterOrderCompletionJobRegistration updateAssetsAfterOrderCompletionJobRegistration;

    @Autowired
    JpaAASRepository aasRepository;

    @Autowired
    OrderRepositoryImpl orderRepository;

    @Test
    @Transactional
    void shouldRegisterManualOrder() throws JoseException {
        // given
        irsApiSupport.irsApiTriggerOrder();
        irsApiSupport.irsApiReturnsOrderAndBatchDetails();
        irsApiSupport.irsApiReturnsJobDetails_withAasIdentifier();

        final String bpn = "bpn";
        final String globalAssetId = "urn:uuid:12345678-1234-1234-1234-123456789012";
        final String aasIdentifier = "urn:uuid:12345678-1234-1234-1234-123456789000";

        CreateOrderRequest createOrderRequest = new CreateOrderRequest("PartType", List.of(
                PartChainIdentificationKey.builder().bpn(bpn).globalAssetId(globalAssetId).build(),
                PartChainIdentificationKey.builder().bpn(bpn).identifier(aasIdentifier).build()));

        final String orderId = "ebb79c45-7bba-4169-bf17-3e719989ab54";

        // when
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .log()
                .all()
                .when()
                .contentType(ContentType.JSON)
                .body(createOrderRequest)
                .post("/api/orders")
                .then()
                .statusCode(201);

        // then
        List<OrderEntity> all = orderJPARepository.findAll();
        assertThat(all).hasSize(1);
        assertThat(all.get(0).getStatus()).isEqualTo(ProcessingState.INITIALIZED);
        assertThat(all.get(0).getAssetsAsBuilt()).hasSize(1);
        assertThat(all.get(0).getAssetsAsBuilt().iterator().next().getImportState()).isEqualTo(
                ImportState.IN_SYNCHRONIZATION);

        assertThat(aasRepository.findAll()).isEmpty();

        orderRepository.handleOrderFinishedCallback(orderId, "ebb79c45-7bba-4169-bf17-3e719989ab55", ProcessingState.COMPLETED, ProcessingState.COMPLETED);

        // then
        updateAssetsAfterOrderCompletionJobRegistration.updateAssetsAfterJobCompletion();

        List<AASEntity> aasAfterCallback = aasRepository.findAll();
        assertThat(aasAfterCallback).hasSize(1);

        AssetAsBuiltEntity updatedAsset = assetsSupport.findById(globalAssetId);
        assertThat(updatedAsset.getTtl()).isNotNull();
        assertThat(updatedAsset.getExpirationDate()).isNotNull();
    }

    @Test
    void shouldNotRegisterManualOrder_missingDigitalTwinType() throws JoseException {
        // given
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(null, List.of(
                PartChainIdentificationKey.builder().bpn("bpn").globalAssetId("globalAssetId").build()));

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .log()
                .all()
                .when()
                .contentType(ContentType.JSON)
                .body(createOrderRequest)
                .post("/api/orders")
                .then()
                .statusCode(400);
    }

    @Test
    void shouldNotRegisterManualOrder_missingKeys() throws JoseException {
        // given
        CreateOrderRequest createOrderRequest = new CreateOrderRequest("PartType", List.of());

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .log()
                .all()
                .when()
                .contentType(ContentType.JSON)
                .body(createOrderRequest)
                .post("/api/orders")
                .then()
                .statusCode(400);
    }
}
