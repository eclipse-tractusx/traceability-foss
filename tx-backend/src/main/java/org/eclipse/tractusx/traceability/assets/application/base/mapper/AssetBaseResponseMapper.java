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

import assets.response.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.eclipse.tractusx.traceability.assets.domain.base.model.*;

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
