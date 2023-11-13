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
package org.eclipse.tractusx.traceability.common.actuator;

import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.IRSApiClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IrsConnectionHealthIndicatorTest {

    @Mock
    private IRSApiClient apiClient;

    @InjectMocks
    private IrsConnectionHealthIndicator testee;


    @Test
    void healthShouldReturnStatusUp() {
        when(apiClient.getPolicies()).thenReturn(List.of());

        Health health = testee.health();

        assertEquals(Health.up().build(), health, "Health should be UP");
    }

    @Test
    void healthShouldReturnStatusOutOfService() {
        when(apiClient.getPolicies()).thenThrow(IllegalStateException.class);

        Health health = testee.health();

        assertEquals(Health.outOfService().build(), health, "Health should be OUT_OF_SERVICE");
    }
}
