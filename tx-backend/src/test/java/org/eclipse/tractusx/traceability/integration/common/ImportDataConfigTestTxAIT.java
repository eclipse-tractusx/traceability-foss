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

package org.eclipse.tractusx.traceability.integration.common;

import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.config.TxABpnConfig;
import org.eclipse.tractusx.traceability.integration.common.support.AssetsSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {TxABpnConfig.Initializer.class})
@ActiveProfiles("integration-spring-boot")
class ImportDataConfigTestTxAIT extends IntegrationTestSpecification {

    @Autowired
    AssetsSupport assetsSupport;

    @Test
    void givenValidFile_whenImportData_thenShouldCreateAssetsWithCorrectOwnerCountsTraceXA() {
        int assetOwnCount = 5;
        int assetSupplierCount = 6;
        assetsSupport.validateAssetsCreatedByOwner(assetOwnCount, assetSupplierCount);
    }
}
