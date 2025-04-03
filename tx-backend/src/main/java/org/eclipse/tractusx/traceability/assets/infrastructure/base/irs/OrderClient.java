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
package org.eclipse.tractusx.traceability.assets.infrastructure.base.irs;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.RegisterOrderRequest;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IrsBatchResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.RegisterOrderResponse;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.eclipse.tractusx.traceability.common.config.RestTemplateConfiguration.IRS_REGULAR_TEMPLATE;

@Slf4j
@Component
public class OrderClient {

    private final RestTemplate irsRegularTemplate;

    public OrderClient(@Qualifier(IRS_REGULAR_TEMPLATE) RestTemplate irsRegularTemplate) {
        this.irsRegularTemplate = irsRegularTemplate;
    }

    public RegisterOrderResponse registerOrder(RegisterOrderRequest registerOrderRequest) {
        return irsRegularTemplate.exchange("/irs/orders", HttpMethod.POST,
                new HttpEntity<>(registerOrderRequest), RegisterOrderResponse.class).getBody();
    }

    @Nullable
    public IrsBatchResponse getBatchByOrder(String orderId, String batchId) {
        String encodedOrderId = URLEncoder.encode(orderId, StandardCharsets.UTF_8);
        String encodedBatchId = URLEncoder.encode(batchId, StandardCharsets.UTF_8);
        return irsRegularTemplate.exchange("/irs/orders/" + encodedOrderId + "/batches/" + encodedBatchId, HttpMethod.GET, null, new ParameterizedTypeReference<IrsBatchResponse>() {
        }).getBody();
    }
}
