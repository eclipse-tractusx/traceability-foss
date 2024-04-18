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

import java.util.List;
import java.util.Optional;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Getter
@Builder
public class TractionBatteryCode {
    private String productType;
    private String tractionBatteryCode;

    private List<AssetAsBuiltEntity.TractionBatteryCodeSubcomponents> subcomponents;


    public static TractionBatteryCode from(List<DetailAspectModel> detailAspectModels) {
        Optional<DetailAspectModel> tractionBatteryCodeAspectModelOptional = emptyIfNull(detailAspectModels)
                .stream()
                .filter(detailAspectModel -> DetailAspectType.TRACTION_BATTERY_CODE.equals(detailAspectModel.getType()))
                .findFirst();

        if (tractionBatteryCodeAspectModelOptional.isEmpty()) {
            return TractionBatteryCode.builder().build();
        }

        DetailAspectDataTractionBatteryCode tractionBatteryCodeAspectModel = (DetailAspectDataTractionBatteryCode) tractionBatteryCodeAspectModelOptional.get().getData();

        String tractionBatteryCode = tractionBatteryCodeAspectModel.getTractionBatteryCode();

        String productType = tractionBatteryCodeAspectModel.getProductType();

        List<AssetAsBuiltEntity.TractionBatteryCodeSubcomponents> subcomponents = TractionBatteryCode.convertSubcomponents(tractionBatteryCodeAspectModel);

        return TractionBatteryCode.builder().productType(productType).tractionBatteryCode(tractionBatteryCode).subcomponents(subcomponents).build();

    }

    private static List<AssetAsBuiltEntity.TractionBatteryCodeSubcomponents> convertSubcomponents(DetailAspectDataTractionBatteryCode tractionBatteryCodeAspectModel) {

        String tractionBatteryCode1 = tractionBatteryCodeAspectModel.getTractionBatteryCode();
        return tractionBatteryCodeAspectModel.getSubcomponents().stream().map(subComponent -> {
            return AssetAsBuiltEntity.TractionBatteryCodeSubcomponents.builder()
                    .tractionBatteryCode(tractionBatteryCode1)
                    .subcomponentTractionBatteryCode(subComponent.getTractionBatteryCode())
                    .productType(subComponent.getProductType())
                    .build();
        }).toList();

    }

    public static List<DetailAspectDataTractionBatteryCodeSubcomponent> toDomain(AssetAsBuiltEntity entity) {
        return entity.getSubcomponents().stream().map(entry -> DetailAspectDataTractionBatteryCodeSubcomponent
                .builder()
                .tractionBatteryCode(entry.getSubcomponentTractionBatteryCode())
                .productType(entry.getProductType())
                .build()).toList();
    }

    public static List<DetailAspectDataTractionBatteryCodeSubcomponent> toDomain(AssetAsBuiltViewEntity entity) {
        return entity.getSubcomponents().stream().map(entry -> DetailAspectDataTractionBatteryCodeSubcomponent
                .builder()
                .tractionBatteryCode(entry.getSubcomponentTractionBatteryCode())
                .productType(entry.getProductType())
                .build()).toList();
    }


}
