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
package org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model;

import lombok.Builder;
import lombok.Getter;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.model.aspect.DetailAspectDataTractionBatteryCode;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.model.aspect.DetailAspectDataTractionBatteryCode.DetailAspectDataTractionBatteryCodeSubcomponent;
import org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectModel;
import org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectType;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
@Builder
public class TractionBatteryCode {
    private String productType;
    private String tractionBatteryCode;

    private List<AssetAsBuiltEntity.tbcsub> subcomponents;


    public static TractionBatteryCode from(List<DetailAspectModel> detailAspectModels) {
        Optional<DetailAspectModel> tractionBatteryCodeAspectModel = detailAspectModels
                .stream()
                .filter(detailAspectModel -> DetailAspectType.TRACTION_BATTERY_CODE.equals(detailAspectModel.getType()))
                .findFirst();

        String tractionBatteryCode = tractionBatteryCodeAspectModel.map(detailAspectModel -> (DetailAspectDataTractionBatteryCode) detailAspectModel.getData())
                .map(DetailAspectDataTractionBatteryCode::getTractionBatteryCode).orElse("");

        String productType = tractionBatteryCodeAspectModel.map(detailAspectModel -> (DetailAspectDataTractionBatteryCode) detailAspectModel.getData())
                .map(DetailAspectDataTractionBatteryCode::getProductType).orElse("");

        List<AssetAsBuiltEntity.tbcsub> subcomponents = tractionBatteryCodeAspectModel.map(detailAspectModel -> (DetailAspectDataTractionBatteryCode) detailAspectModel.getData())
                .map(DetailAspectDataTractionBatteryCode::getSubcomponents)
                .orElse(Collections.emptyList())
                .stream()
                .map(TractionBatteryCode::convertSubcomponents).toList();

        return TractionBatteryCode.builder().productType(productType).tractionBatteryCode(tractionBatteryCode).subcomponents(subcomponents).build();

    }

    private static AssetAsBuiltEntity.tbcsub convertSubcomponents(DetailAspectDataTractionBatteryCodeSubcomponent detailAspectDataTractionBatteryCode) {
        return AssetAsBuiltEntity.tbcsub.builder().subcomponentTractionBatteryCode(detailAspectDataTractionBatteryCode.getTractionBatteryCode())
                .productType(detailAspectDataTractionBatteryCode.getProductType()).build();
    }

    public static List<DetailAspectDataTractionBatteryCodeSubcomponent> toDomain(AssetAsBuiltEntity entity) {
        return entity.getSubcomponents().stream().map(entry -> DetailAspectDataTractionBatteryCodeSubcomponent
                .builder()
                .tractionBatteryCode(entry.getSubcomponentTractionBatteryCode())
                .productType(entry.getProductType())
                .build()).toList();
    }


}
