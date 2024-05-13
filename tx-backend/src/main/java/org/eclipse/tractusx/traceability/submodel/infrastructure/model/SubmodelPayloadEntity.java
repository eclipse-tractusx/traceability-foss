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
package org.eclipse.tractusx.traceability.submodel.infrastructure.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.asplanned.model.AssetAsPlannedEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IrsSubmodel;

import java.util.List;

@Entity
@Table(name = "submodel_payload")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmodelPayloadEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String aspectType;

    private String json;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "asset_as_built_id")
    @ToString.Exclude
    public AssetAsBuiltEntity assetAsBuilt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "asset_as_planned_id")
    @ToString.Exclude
    private AssetAsPlannedEntity assetAsPlanned;

    public static List<SubmodelPayloadEntity> from(AssetAsBuiltEntity asset, List<IrsSubmodel> submodels) {
        return submodels.stream().map(submodel -> SubmodelPayloadEntity.builder()
                        .aspectType(submodel.getAspectType())
                        .json(submodel.getPayloadRaw())
                        .assetAsBuilt(asset)
                        .build())
                .toList();
    }

    public static List<SubmodelPayloadEntity> from(AssetAsPlannedEntity asset, List<IrsSubmodel> submodels) {
        return submodels.stream().map(submodel -> SubmodelPayloadEntity.builder()
                        .aspectType(submodel.getAspectType())
                        .json(submodel.getPayloadRaw())
                        .assetAsPlanned(asset)
                        .build())
                .toList();
    }
}
