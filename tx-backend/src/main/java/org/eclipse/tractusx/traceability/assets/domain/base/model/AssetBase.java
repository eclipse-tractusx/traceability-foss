/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.assets.domain.base.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.irs.component.enums.BomLifecycle;
import org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectModel;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotification;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.eclipse.tractusx.traceability.assets.domain.base.model.SemanticDataModel.BATCH;
import static org.eclipse.tractusx.traceability.assets.domain.base.model.SemanticDataModel.JUSTINSEQUENCE;
import static org.eclipse.tractusx.traceability.assets.domain.base.model.SemanticDataModel.SERIALPART;

@Slf4j
@AllArgsConstructor
@Data
@Builder
public class AssetBase {
    private final String id;
    private final String idShort;
    private String semanticModelId;
    private final String manufacturerId;
    private String manufacturerName;
    private String nameAtManufacturer;
    private String manufacturerPartId;
    private Owner owner;
    @Singular
    private List<Descriptions> childRelations = Collections.emptyList();
    @Singular
    private List<Descriptions> parentRelations = Collections.emptyList();
    private QualityType qualityType;
    private String van;
    private SemanticDataModel semanticDataModel;
    private String classification;
    private List<DetailAspectModel> detailAspectModels = Collections.emptyList();
    private List<QualityNotification> sentQualityAlerts = Collections.emptyList();
    private List<QualityNotification> receivedQualityAlerts = Collections.emptyList();
    private List<QualityNotification> sentQualityInvestigations = Collections.emptyList();
    private List<QualityNotification> receivedQualityInvestigations = Collections.emptyList();
    private ImportState importState;
    private String importNote;
    private String policyId;
    private String tombstone;

    public BomLifecycle getBomLifecycle() {
        if (Objects.equals(semanticDataModel, SERIALPART) || Objects.equals(semanticDataModel, BATCH) || Objects.equals(semanticDataModel, JUSTINSEQUENCE)) {
            return BomLifecycle.AS_BUILT;
        } else {
            return BomLifecycle.AS_PLANNED;
        }
    }

    public boolean isOwnAsset(final String bpn) {
        return bpn.equals(manufacturerId);
    }
}
