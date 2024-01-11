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

import java.util.List;

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
    private List<Descriptions> childRelations;
    @Singular
    private List<Descriptions> parentRelations;
    private boolean activeAlert;
    private boolean inInvestigation;
    private QualityType qualityType;
    private String van;
    private SemanticDataModel semanticDataModel;
    private String classification;
    private List<DetailAspectModel> detailAspectModels;
    private List<QualityNotification> sentQualityAlerts;
    private List<QualityNotification> receivedQualityAlerts;
    private List<QualityNotification> sentQualityInvestigations;
    private List<QualityNotification> receivedQualityInvestigations;
    private ImportState importState;
    private String importNote;

    public BomLifecycle getBomLifecycle() {
        if(semanticDataModel.equals(SERIALPART) || semanticDataModel.equals(BATCH) || semanticDataModel.equals(JUSTINSEQUENCE)){
            return BomLifecycle.AS_BUILT;
        } else {
            return BomLifecycle.AS_PLANNED;
        }
    }
    public boolean isOwnAsset(final String bpn){
        return bpn.equals(manufacturerId);
    }
}
