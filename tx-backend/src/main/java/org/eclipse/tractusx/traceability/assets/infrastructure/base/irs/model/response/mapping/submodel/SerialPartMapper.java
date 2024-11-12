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

import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.model.aspect.DetailAspectDataAsBuilt;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportNote;
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportState;
import org.eclipse.tractusx.traceability.assets.domain.base.model.QualityType;
import org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectModel;
import org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectType;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IrsSubmodel;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.semanticdatamodel.LocalIdKey;
import org.eclipse.tractusx.traceability.generated.SerialPart300Schema;
import org.eclipse.tractusx.traceability.generated.UrnSammIoCatenaxSerialPart300KeyValueList;
import org.eclipse.tractusx.traceability.generated.UrnSammIoCatenaxSerialPart300ManufacturingCharacteristic;
import org.eclipse.tractusx.traceability.generated.UrnSammIoCatenaxSerialPart300PartTypeInformationCharacteristic;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import static org.eclipse.tractusx.traceability.assets.domain.base.model.SemanticDataModel.SERIALPART;

@Slf4j
@Component
public class SerialPartMapper implements SubmodelMapper {
    @Override
    public AssetBase extractSubmodel(IrsSubmodel irsSubmodel) {
        SerialPart300Schema serialPart = (SerialPart300Schema) irsSubmodel.getPayload();

        String serialPartId = getValue(serialPart.getLocalIdentifiers(), LocalIdKey.PART_INSTANCE_ID.getValue());
        String manufacturerId = getValue(serialPart.getLocalIdentifiers(), LocalIdKey.MANUFACTURER_ID.getValue());
        String van = getValue(serialPart.getLocalIdentifiers(), LocalIdKey.VAN.getValue());
        DetailAspectModel detailAspectModel = extractDetailAspectModelsAsBuilt(serialPart.getManufacturingInformation(), serialPart.getPartTypeInformation());

        return AssetBase.builder()
                .id(serialPart.getCatenaXId())
                .semanticModelId(serialPartId)
                .detailAspectModels(List.of(detailAspectModel))
                .manufacturerId(manufacturerId)
                .nameAtManufacturer(serialPart.getPartTypeInformation().getNameAtManufacturer())
                .manufacturerPartId(serialPart.getPartTypeInformation().getManufacturerPartId())
                // TODO change model to be able to save something here
                .classification(null)
                .qualityType(QualityType.OK)
                .semanticDataModel(SERIALPART)
                .van(van)
                .importState(ImportState.PERSISTENT)
                .importNote(ImportNote.PERSISTED)
                .build();
    }

    @Override
    public boolean validMapper(IrsSubmodel submodel) {
        return submodel.getPayload() instanceof SerialPart300Schema;
    }

    private static DetailAspectModel extractDetailAspectModelsAsBuilt(UrnSammIoCatenaxSerialPart300ManufacturingCharacteristic manufacturingInformation,
                                                                      UrnSammIoCatenaxSerialPart300PartTypeInformationCharacteristic partTypeInformation) {

        OffsetDateTime offsetDateTime = MapperHelper.getOffsetDateTime(manufacturingInformation.getDate());

        DetailAspectDataAsBuilt detailAspectDataAsBuilt = DetailAspectDataAsBuilt.builder()
                .customerPartId(partTypeInformation.getCustomerPartId())
                .manufacturingCountry(manufacturingInformation.getCountry())
                .manufacturingDate(offsetDateTime)
                .nameAtCustomer(partTypeInformation.getNameAtCustomer())
                .partId(partTypeInformation.getManufacturerPartId())
                .build();
        return DetailAspectModel.builder().data(detailAspectDataAsBuilt).type(DetailAspectType.AS_BUILT).build();
    }

    private String getValue(Set<UrnSammIoCatenaxSerialPart300KeyValueList> localIdentifiers, String key) {
        UrnSammIoCatenaxSerialPart300KeyValueList object = localIdentifiers.stream()
                .filter(localId -> localId.getKey().equalsIgnoreCase(key))
                .findFirst()
                .orElse(null);

        if (object != null) {
            return object.getValue();
        }
        return null;
    }
}
