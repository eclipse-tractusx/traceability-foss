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
package org.eclipse.tractusx.traceability.assets.application.base.service;

import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.QualityType;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaFilter;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotification;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface AssetBaseService {
    void synchronizeAssetsAsync(List<String> globalAssetIds);

    void synchronizeAssetsAsync(String globalAssetId);

    void setAssetsInvestigationStatus(QualityNotification investigation);

    void setAssetsAlertStatus(QualityNotification alert);

    Map<String, Long> getAssetsCountryMap();

    AssetBase getAssetById(String assetId);

    List<AssetBase> getAssetsById(List<String> assetIds);

    AssetBase getAssetByChildId(String assetId, String childId);

    PageResult<AssetBase> getAssets(Pageable pageable, SearchCriteria searchCriteria);

    AssetBase updateQualityType(String assetId, QualityType qualityType);

    List<String> getDistinctFilterValues(String fieldName, Long size);
}
