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

package org.eclipse.tractusx.traceability.testdata;

import org.eclipse.tractusx.traceability.assets.domain.asbuilt.model.aspect.DetailAspectDataAsBuilt;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.model.aspect.DetailAspectDataPartSiteInformationAsPlanned;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Descriptions;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.domain.base.model.QualityType;
import org.eclipse.tractusx.traceability.assets.domain.base.model.SemanticDataModel;
import org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectModel;
import org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectType;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AssetTestDataFactory {


    public static AssetBase createAssetTestDataWithRelations(List<Descriptions> parents, List<Descriptions> childs) {
        AssetBase assetTestData = createAssetAsBuiltTestdata();
        assetTestData.setParentRelations(parents);
        assetTestData.setChildRelations(childs);
        return assetTestData;
    }

    public static AssetBase createAssetAsBuiltTestdata() {


        DetailAspectDataAsBuilt detailAspectDataAsBuilt = DetailAspectDataAsBuilt.builder()
                .partId("1")
                .customerPartId("2")
                .nameAtCustomer("Moritz")
                .manufacturingCountry("Germany")
                .manufacturingDate(OffsetDateTime.parse("2022-10-20T00:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .build();

        DetailAspectModel detailAspectModel = DetailAspectModel.builder()
                .data(detailAspectDataAsBuilt)
                .type(DetailAspectType.AS_BUILT)
                .build();

        DetailAspectDataAsBuilt detailAspectDataAsBuilt2 = DetailAspectDataAsBuilt.builder()
                .partId("2")
                .customerPartId("3")
                .nameAtCustomer("Max")
                .manufacturingCountry("Germany")
                .manufacturingDate(OffsetDateTime.parse("2022-10-20T00:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .build();

        DetailAspectModel detailAspectModel2 = DetailAspectModel.builder()
                .data(detailAspectDataAsBuilt2)
                .type(DetailAspectType.AS_BUILT)
                .build();


        ArrayList<DetailAspectModel> detailAspectModels = new ArrayList<>();
        detailAspectModels.add(detailAspectModel);
        detailAspectModels.add(detailAspectModel2);

        return AssetBase.builder()
                .id("urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4eb01")
                .idShort("a/devNTierAPlastics")
                .semanticModelId("456")
                .semanticDataModel(SemanticDataModel.SERIALPART)
                .parentRelations(AssetTestDataFactory.provideParentRelations())
                .childRelations(AssetTestDataFactory.provideChildRelations())
                .van("OMAOYGBDTSRCMYSCX")
                .qualityType(QualityType.CRITICAL)
                .owner(Owner.OWN)
                .manufacturerName("manuName")
                .manufacturerId("BPNL000000000UKM")
                .detailAspectModels(detailAspectModels)
                .build();
    }

    public static AssetBase createAssetAsPlannedTestdata() {


        DetailAspectDataPartSiteInformationAsPlanned detailAspectDataPartSiteInfoAsPlanned = DetailAspectDataPartSiteInformationAsPlanned.builder()
                .catenaXSiteId("abc")
                .function("function")
                .functionValidFrom(OffsetDateTime.now())
                .functionValidUntil(OffsetDateTime.now().plusDays(1L))
                .build();

        DetailAspectModel detailAspectModel = DetailAspectModel.builder()
                .data(detailAspectDataPartSiteInfoAsPlanned)
                .type(DetailAspectType.AS_PLANNED)
                .build();

        ArrayList<DetailAspectModel> detailAspectModels = new ArrayList<>();
        detailAspectModels.add(detailAspectModel);

        return AssetBase.builder()
                .id("urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4eb01")
                .idShort("a/devNTierAPlastics")
                .semanticModelId("456")
                .semanticDataModel(SemanticDataModel.PARTASPLANNED)
                .parentRelations(AssetTestDataFactory.provideParentRelations())
                .childRelations(AssetTestDataFactory.provideChildRelations())
                .van("OMAOYGBDTSRCMYSCX")
                .qualityType(QualityType.CRITICAL)
                .owner(Owner.OWN)
                .manufacturerName("manuName")
                .manufacturerId("BPNL000000000UKM")
                .detailAspectModels(detailAspectModels)
                .build();
    }

    public static List<Descriptions> provideParentRelations() {
        List<Descriptions> parentDescriptions = new ArrayList<>();
        parentDescriptions.add(new Descriptions("parent1", "desc1", null, null));
        parentDescriptions.add(new Descriptions("parent2", "desc2", null, null));
        return parentDescriptions;
    }

    public static List<Descriptions> provideChildRelations() {
        List<Descriptions> childDescriptions = new ArrayList<>();
        childDescriptions.add(new Descriptions("child1", "desc1", null, null));
        childDescriptions.add(new Descriptions("child2", "desc2", null, null));
        return childDescriptions;
    }
}
