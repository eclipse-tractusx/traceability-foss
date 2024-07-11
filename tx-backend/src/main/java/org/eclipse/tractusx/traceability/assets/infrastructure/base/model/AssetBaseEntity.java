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
package org.eclipse.tractusx.traceability.assets.infrastructure.base.model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportState;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.domain.base.model.QualityType;

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@MappedSuperclass
public class AssetBaseEntity {

    @Id
    private String id;
    private String idShort;
    @Enumerated(EnumType.STRING)
    private Owner owner;
    private String classification;
    @Enumerated(EnumType.STRING)
    private QualityType qualityType;
    private String manufacturerPartId;
    private String manufacturerId;
    private String manufacturerName;
    private String nameAtManufacturer;
    @Enumerated(EnumType.STRING)
    private SemanticDataModelEntity semanticDataModel;
    private String semanticModelId;
    private String van;
    @Enumerated(EnumType.STRING)
    private ImportState importState;
    private String importNote;
    private String policyId;
    private String tombstone;
}
