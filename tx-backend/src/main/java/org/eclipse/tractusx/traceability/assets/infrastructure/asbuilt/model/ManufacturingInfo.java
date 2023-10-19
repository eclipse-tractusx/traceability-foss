/********************************************************************************
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
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
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.model.aspect.DetailAspectDataAsBuilt;
import org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectModel;
import org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectType;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Getter
@Builder
public class ManufacturingInfo {
    private String manufacturerId;
    private String manufacturerName;
    private String nameAtManufacturer;
    private String manufacturerPartId;
    private String nameAtCustomer;
    private String customerPartId;
    private String manufacturingCountry;
    private OffsetDateTime manufacturingDate;

    public static ManufacturingInfo from(List<DetailAspectModel> detailAspectModels) {
        Optional<DetailAspectModel> detailAspectAsBuilt = detailAspectModels
                .stream()
                .filter(detailAspectModel -> detailAspectModel.getType().equals(DetailAspectType.AS_BUILT))
                .findFirst();

        String manufacturerPartId = detailAspectAsBuilt.map(detailAspectModel -> (DetailAspectDataAsBuilt) detailAspectModel.getData())
                .map(DetailAspectDataAsBuilt::getPartId)
                .orElse("");
        String customerPartId = detailAspectAsBuilt.map(detailAspectModel -> (DetailAspectDataAsBuilt) detailAspectModel.getData())
                .map(DetailAspectDataAsBuilt::getCustomerPartId)
                .orElse("");
        String manufacturingCountry = detailAspectAsBuilt.map(detailAspectModel -> (DetailAspectDataAsBuilt) detailAspectModel.getData())
                .map(DetailAspectDataAsBuilt::getManufacturingCountry)
                .orElse("");
        String nameAtCustomer = detailAspectAsBuilt.map(detailAspectModel -> (DetailAspectDataAsBuilt) detailAspectModel.getData())
                .map(DetailAspectDataAsBuilt::getNameAtCustomer)
                .orElse("");
        OffsetDateTime manufacturingDate = detailAspectAsBuilt.map(detailAspectModel -> (DetailAspectDataAsBuilt) detailAspectModel.getData())
                .map(DetailAspectDataAsBuilt::getManufacturingDate)
                .orElse(null);

        return ManufacturingInfo.builder()
                .manufacturerPartId(manufacturerPartId)
                .customerPartId(customerPartId)
                .manufacturingCountry(manufacturingCountry)
                .nameAtCustomer(nameAtCustomer)
                .manufacturingDate(manufacturingDate)
                .build();
    }
}
