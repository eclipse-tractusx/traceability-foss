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

package org.eclipse.tractusx.traceability.common.config;

import org.eclipse.tractusx.traceability.assets.domain.base.JobRepository;
import org.eclipse.tractusx.traceability.policies.domain.PolicyRepository;
import org.eclipse.tractusx.traceability.notification.domain.contract.EdcNotificationContractService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ApplicationStartupConfigTest {

    @InjectMocks
    private ApplicationStartupConfig applicationStartupConfig;

    @Mock
    private EdcNotificationContractService edcNotificationContractService;
    @Mock
    private JobRepository jobRepository;

    @Mock
    private PolicyRepository policyRepository;

    @Test
    void whenCallRegisterIrsPolicy_thenCallRepository() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        // when
        executor.execute(() -> {
            applicationStartupConfig.registerIrsPolicy();

            // then
            verify(policyRepository, times(1)).createPolicyBasedOnAppConfig();
        });

        executor.shutdown();
    }

    @Test
    void whenCallCreateNotificationContracts_thenCallContractService() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        // when
        executor.execute(() -> {
            applicationStartupConfig.registerIrsPolicy();

            // then
            verify(edcNotificationContractService, times(4)).createNotificationContract(any());
        });

        executor.shutdown();
    }
}
