/********************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
package org.eclipse.tractusx.traceability.common.security.apikey;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ApiKeyFeatureServiceTest {


    @Test
    void test_getApiKeyRequestMapping_should_return_apiKey_secured_paths() {
        //GIVEN
        ApiKeyFeatureService service = new ApiKeyFeatureService();

        //WHEN
        List<String> apiKeyRequestMapping = service.getApiKeyRequestMapping();

        //THEN
        assertNotNull(apiKeyRequestMapping);
        assertFalse(apiKeyRequestMapping.isEmpty());
        assertThat(apiKeyRequestMapping).containsExactlyInAnyOrder(
                "/assets/as-built/sync", "/assets/as-planned/sync",
                "/registry/reload", "/aas/lookup",
                "/administration/digitalTwinPart",
                "/administration/digitalTwinPart/detail",
                "/orders/configuration/triggers",
                "/orders/configuration/batches/active",
                "/orders/configuration/batches",
                "/orders/configuration/triggers/active"
        );
    }


}
