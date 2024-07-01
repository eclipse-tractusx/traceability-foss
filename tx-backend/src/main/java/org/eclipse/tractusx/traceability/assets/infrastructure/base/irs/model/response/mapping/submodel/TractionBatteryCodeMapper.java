/********************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
package org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.submodel;

import org.eclipse.tractusx.traceability.assets.domain.asbuilt.model.aspect.DetailAspectDataTractionBatteryCode;
import org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectModel;
import org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectType;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IrsSubmodel;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.asbuilt.AsBuiltDetailMapper;
import org.eclipse.tractusx.traceability.generated.TractionBatteryCode200Schema;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class TractionBatteryCodeMapper implements AsBuiltDetailMapper {
    @Override
    public List<DetailAspectModel> extractDetailAspectModel(IrsSubmodel irsSubmodel, String globalAssetId) {
        TractionBatteryCode200Schema tractionBatteryCode = (TractionBatteryCode200Schema) irsSubmodel.getPayload();

        List<DetailAspectDataTractionBatteryCode.DetailAspectDataTractionBatteryCodeSubcomponent> subComponents = tractionBatteryCode.getSubcomponents().stream().map(tractionBatteryComponent -> DetailAspectDataTractionBatteryCode.DetailAspectDataTractionBatteryCodeSubcomponent
                .builder()
                .tractionBatteryCode(tractionBatteryComponent.getTractionBatteryCode())
                .productType(tractionBatteryComponent.getProductType().value())
                .build()).toList();

        DetailAspectDataTractionBatteryCode detailAspectDataTractionBatteryCode =
                DetailAspectDataTractionBatteryCode
                        .builder()
                        .tractionBatteryCode(tractionBatteryCode.getTractionBatteryCode())
                        .productType(tractionBatteryCode.getProductType().value())
                        .subcomponents(subComponents)
                        .build();

        return List.of(DetailAspectModel.builder().data(detailAspectDataTractionBatteryCode).type(DetailAspectType.TRACTION_BATTERY_CODE).globalAssetId(globalAssetId).build());
    }

    @Override
    public boolean validMapper(IrsSubmodel submodel) {
        return submodel.getPayload() instanceof TractionBatteryCode200Schema;
    }

}
