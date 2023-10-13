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
package org.eclipse.tractusx.traceability.assets.domain.base.model.aspect;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.util.Strings;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.model.aspect.DetailAspectDataAsBuilt;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.model.aspect.DetailAspectDataTractionBatteryCode;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.model.aspect.DetailAspectDataAsPlanned;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.model.aspect.DetailAspectDataPartSiteInformationAsPlanned;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.TractionBatteryCode;
import org.eclipse.tractusx.traceability.assets.infrastructure.asplanned.model.AssetAsPlannedEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.semanticdatamodel.ManufacturingInformation;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.semanticdatamodel.PartTypeInformation;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.semanticdatamodel.Site;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.semanticdatamodel.ValidityPeriod;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;
import static org.eclipse.tractusx.traceability.common.date.DateUtil.toOffsetDateTime;

@Getter
@Setter
@Builder
public class DetailAspectModel {
    private DetailAspectType type;
    private DetailAspectData data;

    public static List<DetailAspectModel> from(AssetAsBuiltEntity entity) {

        DetailAspectModel detailAspectModelAsBuilt = DetailAspectModel.builder()
                .type(DetailAspectType.AS_BUILT)
                .data(DetailAspectDataAsBuilt.builder()
                        .partId(entity.getManufacturerPartId())
                        .customerPartId(entity.getCustomerPartId())
                        .nameAtCustomer(entity.getNameAtCustomer())
                        .manufacturingCountry(entity.getManufacturingCountry())
                        .manufacturingDate(toOffsetDateTime(entity.getManufacturingDate()))
                        .build())
                .build();
        List<DetailAspectModel> detailAspectModels = new ArrayList<>(List.of(detailAspectModelAsBuilt));

        if (!Strings.isEmpty(entity.getTractionBatteryCode())) {
            DetailAspectModel detailAspectModelTractionBatteryCode = DetailAspectModel.
                    builder()
                    .type(DetailAspectType.TRACTION_BATTERY_CODE)
                    .data(DetailAspectDataTractionBatteryCode.builder()
                            .tractionBatteryCode(entity.getTractionBatteryCode())
                            .productType(entity.getProductType())
                            .subcomponents(TractionBatteryCode.toDomain(entity))
                            .build()
                    ).build();

            detailAspectModels.add(detailAspectModelTractionBatteryCode);
        }

        return detailAspectModels;
    }

    public static List<DetailAspectModel> from(AssetAsPlannedEntity entity) {
        DetailAspectModel asPlannedInfo = DetailAspectModel.builder()
                .type(DetailAspectType.AS_PLANNED)
                .data(DetailAspectDataAsPlanned.builder()
                        .validityPeriodFrom(toOffsetDateTime(entity.getValidityPeriodFrom()))
                        .validityPeriodTo(toOffsetDateTime(entity.getValidityPeriodTo()))
                        .build())
                .build();

        DetailAspectModel partSiteInfo = DetailAspectModel.builder()
                .type(DetailAspectType.PART_SITE_INFORMATION_AS_PLANNED)
                .data(DetailAspectDataPartSiteInformationAsPlanned.builder()
                        .catenaXSiteId(entity.getCatenaxSiteId())
                        .functionValidFrom(toOffsetDateTime(entity.getFunctionValidFrom()))
                        .function(entity.getFunction())
                        .functionValidUntil(toOffsetDateTime(entity.getFunctionValidUntil()))
                        .build())
                .build();

        return List.of(asPlannedInfo, partSiteInfo);
    }

    public static List<DetailAspectModel> extractDetailAspectModelsPartSiteInformationAsPlanned(List<Site> sites) {
        List<DetailAspectModel> detailAspectModels = new ArrayList<>();
        emptyIfNull(sites).forEach(site -> {
            DetailAspectDataPartSiteInformationAsPlanned detailAspectDataPartSiteInformationAsPlanned = DetailAspectDataPartSiteInformationAsPlanned.builder()
                    .catenaXSiteId(site.catenaXSiteId())
                    .functionValidFrom(site.functionValidFrom())
                    .function(site.function())
                    .functionValidUntil(site.functionValidUntil())
                    .build();
            detailAspectModels.add(DetailAspectModel.builder().data(detailAspectDataPartSiteInformationAsPlanned).type(DetailAspectType.PART_SITE_INFORMATION_AS_PLANNED).build());
        });

        return detailAspectModels;
    }

    public static DetailAspectModel extractDetailAspectModelsAsPlanned(ValidityPeriod validityPeriod) {
        DetailAspectDataAsPlanned detailAspectDataAsPlanned = DetailAspectDataAsPlanned.builder()
                .validityPeriodFrom(validityPeriod.validFrom())
                .validityPeriodTo(validityPeriod.validTo())
                .build();
        return DetailAspectModel.builder().data(detailAspectDataAsPlanned).type(DetailAspectType.AS_PLANNED).build();
    }

    public static DetailAspectModel extractDetailAspectModelsAsBuilt(ManufacturingInformation manufacturingInformation,
                                                                     PartTypeInformation partTypeInformation) {

        DetailAspectDataAsBuilt detailAspectDataAsBuilt = DetailAspectDataAsBuilt.builder()
                .customerPartId(partTypeInformation.customerPartId())
                .manufacturingCountry(manufacturingInformation.country())
                .manufacturingDate(manufacturingInformation.date())
                .nameAtCustomer(partTypeInformation.nameAtCustomer())
                .partId(partTypeInformation.manufacturerPartId())
                .build();
        return DetailAspectModel.builder().data(detailAspectDataAsBuilt).type(DetailAspectType.AS_BUILT).build();
    }

    public static DetailAspectModel extractDetailAspectModelTractionBatteryCode(DetailAspectDataTractionBatteryCode detailAspectDataTractionBatteryCode) {
        return DetailAspectModel.builder().data(detailAspectDataTractionBatteryCode).type(DetailAspectType.TRACTION_BATTERY_CODE).build();
    }
}
