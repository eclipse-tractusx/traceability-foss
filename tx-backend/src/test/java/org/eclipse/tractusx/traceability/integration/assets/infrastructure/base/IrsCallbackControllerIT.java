/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.integration.assets.infrastructure.base;

import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.OrderRepositoryImpl;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.ProcessingState;
import org.eclipse.tractusx.traceability.configuration.domain.model.Batch;
import org.eclipse.tractusx.traceability.configuration.domain.model.Order;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.tractusx.traceability.integration.common.support.IrsApiSupport.sendCallback;

class IrsCallbackControllerIT extends IntegrationTestSpecification {


    @Autowired
    OrderRepositoryImpl orderRepository;

    private final static int HTTP_STATUS_OKAY = 200;


    @Test
    void givenAssets_whenCallbackReceived_thenSaveThemAndStoreContractAgreementId() {
        // given
        oAuth2ApiSupport.oauth2ApiReturnsTechnicalUserToken();

        String orderId = "ebb79c45-7bba-4169-bf17-3e719989ab54";
        String batchId = "ebb79c45-7bba-4169-bf17-3e719989ab55";
        String orderState = "PROCESSING";
        String batchState = "COMPLETED";

        Batch batch = Batch.builder()
                .status(ProcessingState.PROCESSING).id(batchId).build();
        Order order = Order.builder().batchList(List.of(batch)).id(orderId).status(ProcessingState.INITIALIZED).build();
        orderRepository.save(order);

        // when
        sendCallback(orderId, batchId, orderState, batchState, HTTP_STATUS_OKAY);

        // then
        List<Order> ordersByStatus = orderRepository.findOrdersByStatus(List.of(ProcessingState.PROCESSING));
        assertThat(ordersByStatus).hasSize(1);
        assertThat(ordersByStatus.get(0).getStatus()).isEqualTo(ProcessingState.PROCESSING);
        assertThat(ordersByStatus.get(0).getId()).isEqualTo(orderId);
        assertThat(ordersByStatus.get(0).getBatchList().get(0).getId()).isEqualTo(batchId);
        assertThat(ordersByStatus.get(0).getBatchList().get(0).getStatus()).isEqualTo(ProcessingState.COMPLETED);
    }

}
