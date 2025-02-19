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

import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Direction;

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
    public static RegisterOrderRequest buildOrderRequest(List<String> aspects, BomLifecycle bomLifecycle, String callbackUrl, Direction direction, List<PartChainIdentificationKey> keys) {
        return new RegisterOrderRequest(aspects, DEFAULT_BATCH_SIZE, DEFAULT_BATCH_STRATEGY, bomLifecycle, callbackUrl, DEFAULT_COLLECT_ASPECTS, DEFAULT_DEPTH, direction, DEFAULT_JOB_TIMEOUT, keys, DEFAULT_TIMEOUT);
    }

    public static final int DEFAULT_DEPTH = 2;
    public static final int DEFAULT_BATCH_SIZE = 10;
    public static final BatchStrategy DEFAULT_BATCH_STRATEGY = BatchStrategy.PRESERVE_BATCH_JOB_ORDER;
    public static final boolean DEFAULT_COLLECT_ASPECTS = true;
    public static final int DEFAULT_JOB_TIMEOUT = 3600;
    public static final int DEFAULT_TIMEOUT = 43200;

}


