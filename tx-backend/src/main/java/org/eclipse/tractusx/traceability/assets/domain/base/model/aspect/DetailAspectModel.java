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

import assets.response.asbuilt.DetailAspectDataAsBuiltResponse;
import assets.response.asplanned.DetailAspectDataAsPlannedResponse;
import assets.response.asplanned.PartSiteInformationAsPlannedResponse;
import assets.response.base.DetailAspectDataResponse;
import assets.response.base.DetailAspectModelResponse;
import assets.response.base.DetailAspectTypeResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.model.aspect.DetailAspectDataAsBuilt;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.model.aspect.DetailAspectDataAsPlanned;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.model.aspect.DetailAspectDataPartSiteInformationAsPlanned;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.asplanned.model.AssetAsPlannedEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.semanticdatamodel.ManufacturingInformation;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.semanticdatamodel.PartTypeInformation;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.semanticdatamodel.Site;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.semanticdatamodel.ValidityPeriod;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Getter
@Setter
@Builder
public class DetailAspectModel {
    private DetailAspectType type;
    private DetailAspectData data;

    public static DetailAspectModel from(DetailAspectModelResponse detailAspectModelResponse) {
        DetailAspectTypeResponse detailAspectType = detailAspectModelResponse.getType();
        DetailAspectDataResponse detailAspectData = detailAspectModelResponse.getData();
        return DetailAspectModel.builder()
                .type(DetailAspectType.valueOf(detailAspectType.name()))
                .data(from(detailAspectData)).build();

    }

    public static DetailAspectData from(DetailAspectDataResponse detailAspectDataResponse) {
        if (detailAspectDataResponse instanceof DetailAspectDataAsBuiltResponse) {
            return DetailAspectDataAsBuilt.builder()
                    .partId(((DetailAspectDataAsBuiltResponse) detailAspectDataResponse).getPartId())
                    .customerPartId(((DetailAspectDataAsBuiltResponse) detailAspectDataResponse).getCustomerPartId())
                    .nameAtCustomer(((DetailAspectDataAsBuiltResponse) detailAspectDataResponse).getNameAtCustomer())
                    .manufacturingCountry(((DetailAspectDataAsBuiltResponse) detailAspectDataResponse).getManufacturingCountry())
                    .manufacturingDate(((DetailAspectDataAsBuiltResponse) detailAspectDataResponse).getManufacturingDate())
                    .build();
        }

        if (detailAspectDataResponse instanceof DetailAspectDataAsPlannedResponse) {
            return DetailAspectDataAsPlanned.builder()
                    .validityPeriodTo(((DetailAspectDataAsPlannedResponse) detailAspectDataResponse).getValidityPeriodTo())
                    .validityPeriodFrom(((DetailAspectDataAsPlannedResponse) detailAspectDataResponse).getValidityPeriodFrom())
                    .build();
        }

        if (detailAspectDataResponse instanceof PartSiteInformationAsPlannedResponse) {
            return DetailAspectDataPartSiteInformationAsPlanned.builder()
                    .catenaXSiteId(((PartSiteInformationAsPlannedResponse) detailAspectDataResponse).getCatenaXSiteId())
                    .functionValidFrom(((PartSiteInformationAsPlannedResponse) detailAspectDataResponse).getFunctionValidFrom())
                    .function(((PartSiteInformationAsPlannedResponse) detailAspectDataResponse).getFunction())
                    .functionValidUntil(((PartSiteInformationAsPlannedResponse) detailAspectDataResponse).getFunctionValidUntil())
                    .build();
        }
        return DetailAspectDataPartSiteInformationAsPlanned.builder().build();
    }

    public static Optional<DetailAspectModel> getDetailAspectDataByType(List<DetailAspectModel> detailAspectModels, DetailAspectType type) {
        return detailAspectModels.stream()
                .filter(detailAspectModel -> detailAspectModel.getType().equals(type))
                .findFirst();
    }

    public static List<DetailAspectModel> from(AssetAsBuiltEntity entity) {

        DetailAspectModel detailAspectModel = DetailAspectModel.builder()
                .type(DetailAspectType.AS_BUILT)
                .data(DetailAspectDataAsBuilt.builder()
                        .partId(entity.getManufacturerPartId())
                        .customerPartId(entity.getCustomerPartId())
                        .nameAtCustomer(entity.getNameAtCustomer())
                        .manufacturingCountry(entity.getManufacturingCountry())
                        .manufacturingDate(entity.getManufacturingDate() != null ? entity.getManufacturingDate().toString() : null)
                        .build())
                .build();

        return List.of(detailAspectModel);
    }

    public static List<DetailAspectModel> from(AssetAsPlannedEntity entity) {
        DetailAspectModel asPlannedInfo = DetailAspectModel.builder()
                .type(DetailAspectType.AS_PLANNED)
                .data(DetailAspectDataAsPlanned.builder()
                        .validityPeriodFrom(entity.getValidityPeriodFrom())
                        .validityPeriodTo(entity.getValidityPeriodTo())
                        .build())
                .build();

        DetailAspectModel partSiteInfo = DetailAspectModel.builder()
                .type(DetailAspectType.PART_SITE_INFORMATION_AS_PLANNED)
                .data(DetailAspectDataPartSiteInformationAsPlanned.builder()
                        .catenaXSiteId(entity.getId())
                        .functionValidFrom(entity.getFunctionValidFrom())
                        .function(entity.getFunction())
                        .functionValidUntil(entity.getFunctionValidUntil())
                        .build())
                .build();

        return List.of(asPlannedInfo, partSiteInfo);
    }


    public static List<DetailAspectModel> extractDetailAspectModelsPartSiteInformationAsPlanned(List<Site> sites) {
        List<DetailAspectModel> detailAspectModels = new ArrayList<>();
        emptyIfNull(sites).forEach(site -> {
            DetailAspectDataPartSiteInformationAsPlanned detailAspectDataPartSiteInformationAsPlanned = DetailAspectDataPartSiteInformationAsPlanned.builder()
                    .catenaXSiteId(site.catenaXSiteId())
                    .functionValidFrom(site.functionValidFrom().toString())
                    .functionValidUntil(site.functionValidUntil().toString())
                    .build();
            detailAspectModels.add(DetailAspectModel.builder().data(detailAspectDataPartSiteInformationAsPlanned).type(DetailAspectType.PART_SITE_INFORMATION_AS_PLANNED).build());
        });

        return detailAspectModels;
    }

    public static DetailAspectModel extractDetailAspectModelsAsPlanned(ValidityPeriod validityPeriod) {
        DetailAspectDataAsPlanned detailAspectDataAsPlanned = DetailAspectDataAsPlanned.builder()
                .validityPeriodFrom(validityPeriod.validFrom().toString())
                .validityPeriodTo(validityPeriod.validTo().toString())
                .build();
        return DetailAspectModel.builder().data(detailAspectDataAsPlanned).type(DetailAspectType.SINGLE_LEVEL_BOM_AS_PLANNED).build();
    }

    public static DetailAspectModel extractDetailAspectModelsAsBuilt(ManufacturingInformation manufacturingInformation, PartTypeInformation partTypeInformation) {
        DetailAspectDataAsBuilt detailAspectDataAsBuilt = DetailAspectDataAsBuilt.builder()
                .customerPartId(partTypeInformation.customerPartId())
                .manufacturingCountry(manufacturingInformation.country())
                .manufacturingDate(manufacturingInformation.date().toString())
                .nameAtCustomer(partTypeInformation.nameAtCustomer())
                .partId(partTypeInformation.manufacturerPartId())
                .build();
        return DetailAspectModel.builder().data(detailAspectDataAsBuilt).type(DetailAspectType.SINGLE_LEVEL_USAGE_AS_BUILT).build();
    }
}
