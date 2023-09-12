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

package org.eclipse.tractusx.traceability.integration.assets.infrastructure.repository;

import org.eclipse.tractusx.traceability.assets.domain.asbuilt.repository.AssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.repository.JpaAssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AssetsSupport;

import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class AssetAsBuiltRepositoryIT extends IntegrationTestSpecification {

    @Autowired
    JpaAssetAsBuiltRepository supportRepo;
    @Autowired
    AssetsSupport assetsSupport;
    @Autowired
    AssetAsBuiltRepository repository;

//    @Test
//    public void test() {
//        assetsSupport.defaultAssetsStored();
//        PageResult<AssetBase> result = repository.getAssetsFiltered(Pageable.ofSize(10), List.of(SearchCriteria.builder().key("manufacturingCountry").operation(SearchOperation.EQUAL).value("DEU").build()));
//        assertThat(result).isNotNull();
//    }
}
