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
package org.eclipse.tractusx.traceability.assets.application.base.mapper;

import assets.response.asbuilt.DetailAspectDataAsBuiltResponse;
import assets.response.asbuilt.DetailAspectDataTractionBatteryCodeResponse;
import assets.response.asbuilt.DetailAspectDataTractionBatteryCodeResponse.DetailAspectDataTractionBatteryCodeSubcomponentResponse;
import assets.response.asplanned.DetailAspectDataAsPlannedResponse;
import assets.response.asplanned.PartSiteInformationAsPlannedResponse;
import assets.response.base.DescriptionsResponse;
import assets.response.base.DetailAspectDataResponse;
import assets.response.base.DetailAspectModelResponse;
import assets.response.base.DetailAspectTypeResponse;
import assets.response.base.OwnerResponse;
import assets.response.base.QualityTypeResponse;
import assets.response.base.SemanticDataModelResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.model.aspect.DetailAspectDataAsBuilt;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.model.aspect.DetailAspectDataTractionBatteryCode;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.model.aspect.DetailAspectDataTractionBatteryCode.DetailAspectDataTractionBatteryCodeSubcomponent;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.model.aspect.DetailAspectDataAsPlanned;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.model.aspect.DetailAspectDataPartSiteInformationAsPlanned;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Descriptions;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.domain.base.model.QualityType;
import org.eclipse.tractusx.traceability.assets.domain.base.model.SemanticDataModel;
import org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectData;
import org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectModel;
import org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectType;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotification;

import java.util.List;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@AllArgsConstructor
@Slf4j
@Data
public class AssetBaseResponseMapper {

    public static List<DetailAspectModelResponse> fromList(List<DetailAspectModel> detailAspectModels) {
        List<DetailAspectModelResponse> list = emptyIfNull(detailAspectModels).stream()
                .map(AssetBaseResponseMapper::from)
                .toList();
        log.info(list.toString());
        return list;
    }

    public static DetailAspectModelResponse from(DetailAspectModel detailAspectModel) {
        return DetailAspectModelResponse.builder()
                .type(from(detailAspectModel.getType()))
                .data(from(detailAspectModel.getData()))
                .build();
    }

    public static DetailAspectTypeResponse from(DetailAspectType detailAspectType) {
        return DetailAspectTypeResponse.valueOf(detailAspectType.name());
    }

    public static DetailAspectDataResponse from(DetailAspectData detailAspectData) {

        if (detailAspectData instanceof DetailAspectDataTractionBatteryCode detailAspectDataTractionBatteryCode) {
            return DetailAspectDataTractionBatteryCodeResponse.builder()
                    .tractionBatteryCode(detailAspectDataTractionBatteryCode.getTractionBatteryCode())
                    .productType(detailAspectDataTractionBatteryCode.getProductType())
                    .subcomponents(from(detailAspectDataTractionBatteryCode.getSubcomponents()))
                    .build();
        }

        if (detailAspectData instanceof DetailAspectDataPartSiteInformationAsPlanned detailAspectDataPartSiteInformationAsPlanned) {
            return PartSiteInformationAsPlannedResponse.builder().catenaXSiteId(detailAspectDataPartSiteInformationAsPlanned.getCatenaXSiteId())
                    .function(detailAspectDataPartSiteInformationAsPlanned.getFunction())
                    .functionValidFrom(String.valueOf(detailAspectDataPartSiteInformationAsPlanned.getFunctionValidFrom()))
                    .functionValidUntil(String.valueOf(detailAspectDataPartSiteInformationAsPlanned.getFunctionValidUntil()))
                    .build();
        }

        if (detailAspectData instanceof DetailAspectDataAsBuilt detailAspectDataAsBuilt) {
            return DetailAspectDataAsBuiltResponse.builder()
                    .partId(detailAspectDataAsBuilt.getPartId())
                    .customerPartId(detailAspectDataAsBuilt.getCustomerPartId())
                    .nameAtCustomer(detailAspectDataAsBuilt.getNameAtCustomer())
                    .manufacturingCountry(detailAspectDataAsBuilt.getManufacturingCountry())
                    .manufacturingDate(String.valueOf(detailAspectDataAsBuilt.getManufacturingDate()))
                    .build();
        }

        if (detailAspectData instanceof DetailAspectDataAsPlanned detailAspectDataAsPlanned) {
            return DetailAspectDataAsPlannedResponse.builder()
                    .validityPeriodTo(String.valueOf(detailAspectDataAsPlanned.getValidityPeriodTo()))
                    .validityPeriodFrom(String.valueOf(detailAspectDataAsPlanned.getValidityPeriodFrom()))
                    .build();
        }
        return null;
    }

    public static OwnerResponse from(final Owner owner) {
        return OwnerResponse.valueOf(owner.name());
    }

    public static DescriptionsResponse from(final Descriptions descriptions) {
        return new DescriptionsResponse(
                descriptions.id(),
                descriptions.idShort()
        );
    }

    public static QualityTypeResponse from(final QualityType qualityType) {
        return QualityTypeResponse.valueOf(qualityType.name());
    }

    public static SemanticDataModelResponse from(final SemanticDataModel semanticDataModel) {
        if (semanticDataModel == null) {
            return SemanticDataModelResponse.UNKNOWN;
        }
        return SemanticDataModelResponse.valueOf(semanticDataModel.name());
    }

    protected static Integer countNotificationsInActiveState(List<QualityNotification> notifications) {
        return emptyIfNull(notifications).stream()
                .filter(QualityNotification::isActiveState)
                .toList().size();
    }

    private static List<DetailAspectDataTractionBatteryCodeSubcomponentResponse> from(List<DetailAspectDataTractionBatteryCodeSubcomponent> subcomponents) {
        return subcomponents.stream().map(entry -> DetailAspectDataTractionBatteryCodeSubcomponentResponse.builder().tractionBatteryCode(entry.getTractionBatteryCode())
                .productType(entry.getProductType()).build()).toList();
    }
}
