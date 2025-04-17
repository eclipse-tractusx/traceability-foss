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

package org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request;

import  assets.request.PartChainIdentificationKey;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Direction;
import org.eclipse.tractusx.traceability.configuration.domain.model.OrderConfiguration;

import java.util.List;

public record RegisterOrderRequest(
        List<String> aspects,
        int batchSize,
        BatchStrategy batchStrategy,
        BomLifecycle bomLifecycle,
        String callbackUrl,
        boolean collectAspects,
        int depth,
        Direction direction,
        int jobTimeout,
        List<PartChainIdentificationKey> keys,
        int timeout
) {

    private static final int DEFAULT_DEPTH = 2;
    private static final BatchStrategy DEFAULT_BATCH_STRATEGY = BatchStrategy.PRESERVE_BATCH_JOB_ORDER;
    private static final boolean DEFAULT_COLLECT_ASPECTS = true;
    private static final String ORDER_CALLBACK = "/api/irs/order/callback";

    public static RegisterOrderRequest buildOrderRequest(List<String> aspects, BomLifecycle bomLifecycle, String callbackUrl, Direction direction, List<PartChainIdentificationKey> keys, OrderConfiguration orderConfiguration) {

        return new RegisterOrderRequest(
                aspects,
                orderConfiguration.getBatchSize(),
                DEFAULT_BATCH_STRATEGY,
                bomLifecycle,
                callbackUrl + ORDER_CALLBACK,
                DEFAULT_COLLECT_ASPECTS,
                DEFAULT_DEPTH,
                direction,
                orderConfiguration.getJobTimeoutMs(),
                keys,
                orderConfiguration.getTimeoutMs());
    }

}


