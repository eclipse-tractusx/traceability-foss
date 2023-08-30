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

import assets.response.base.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.eclipse.tractusx.traceability.assets.application.asplanned.mapper.AssetAsPlannedResponseMapper;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.model.aspect.PartSiteInformationAsPlanned;
import org.eclipse.tractusx.traceability.assets.domain.base.model.*;
import org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectData;
import org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectModel;
import org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectType;

import java.util.List;

@AllArgsConstructor
@Data
@SuperBuilder
public class AssetBaseResponseMapper {
    public static SemanticModelResponse from(final SemanticModel semanticModel) {
        return SemanticModelResponse.builder()
                .customerPartId(semanticModel.getCustomerPartId())
                .manufacturerPartId(semanticModel.getManufacturerPartId())
                .manufacturingCountry(semanticModel.getManufacturingCountry())
                .manufacturingDate(semanticModel.getManufacturingDate())
                .nameAtCustomer(semanticModel.getNameAtCustomer())
                .nameAtManufacturer(semanticModel.getNameAtManufacturer())
                .build();
    }

    public static List<DetailAspectModelResponse> fromList(List<DetailAspectModel> detailAspectModels) {
        return detailAspectModels.stream()
                .map(AssetAsPlannedResponseMapper::from)
                .toList();
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
        if (detailAspectData instanceof PartSiteInformationAsPlanned partSiteInformationAsPlanned) {
            return PartSiteInformationAsPlannedResponse.builder().catenaXSiteId(partSiteInformationAsPlanned.getCatenaXSiteId())
                    .function(partSiteInformationAsPlanned.getFunction())
                    .functionValidFrom(partSiteInformationAsPlanned.getFunctionValidFrom())
                    .functionValidUntil(partSiteInformationAsPlanned.getFunctionValidUntil())
                    .build();
        } else {
            return null;
        }
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
}
