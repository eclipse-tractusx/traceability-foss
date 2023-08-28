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
package org.eclipse.tractusx.traceability.assets.domain.base.model;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.semanticdatamodel.ManufacturingInformation;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.semanticdatamodel.PartTypeInformation;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.AssetBaseEntity;

import java.time.Instant;

@Builder
@Data
public class SemanticModel {
    private Instant manufacturingDate;
    private String manufacturingCountry;
    private String manufacturerPartId;
    private String customerPartId;
    private String nameAtManufacturer;
    private String nameAtCustomer;

    public static SemanticModel from(AssetBaseEntity assetAsPlannedEntity) {
        return SemanticModel.builder()
                .customerPartId(assetAsPlannedEntity.getCustomerPartId())
                .manufacturerPartId(assetAsPlannedEntity.getManufacturerPartId())
                .nameAtCustomer(assetAsPlannedEntity.getNameAtCustomer())
                .nameAtManufacturer(assetAsPlannedEntity.getNameAtManufacturer())
                .build();
    }

    public static SemanticModel from(AssetAsBuiltEntity assetAsBuiltEntity) {
        return SemanticModel.builder()
                .customerPartId(assetAsBuiltEntity.getCustomerPartId())
                .manufacturerPartId(assetAsBuiltEntity.getManufacturerPartId())
                .manufacturingCountry(assetAsBuiltEntity.getManufacturingCountry())
                .manufacturingDate(assetAsBuiltEntity.getManufacturingDate())
                .nameAtCustomer(assetAsBuiltEntity.getNameAtCustomer())
                .nameAtManufacturer(assetAsBuiltEntity.getNameAtManufacturer())
                .build();
    }


    public static SemanticModel from(PartTypeInformation partTypeInformation, ManufacturingInformation manufacturingInformation) {
        return SemanticModel.builder()
                .manufacturerPartId(defaultValue(partTypeInformation.manufacturerPartId()))
                .nameAtManufacturer(defaultValue(partTypeInformation.nameAtManufacturer()))
                .customerPartId(defaultValue(partTypeInformation.customerPartId()))
                .nameAtCustomer(defaultValue(partTypeInformation.nameAtCustomer()))
                .manufacturingCountry(defaultValue(manufacturingInformation.country()))
                .manufacturingDate(manufacturingInformation.date().toInstant())
                .manufacturerPartId(defaultValue(partTypeInformation.manufacturerPartId()))
                .build();
    }

    public static SemanticModel from(PartTypeInformation partTypeInformation) {
        if (partTypeInformation != null){
            return SemanticModel.builder()
                    .manufacturerPartId(defaultValue(partTypeInformation.manufacturerPartId()))
                    .nameAtManufacturer(defaultValue(partTypeInformation.nameAtManufacturer()))
                    .customerPartId(defaultValue(partTypeInformation.customerPartId()))
                    .nameAtCustomer(defaultValue(partTypeInformation.nameAtCustomer()))
                    .manufacturerPartId(defaultValue(partTypeInformation.manufacturerPartId()))
                    .build();
        } else {
            return SemanticModel.builder().build();
        }

    }

    private static String defaultValue(String value) {
        final String EMPTY_TEXT = "--";
        if (StringUtils.isBlank(value)) {
            return EMPTY_TEXT;
        }
        return value;
    }

}
