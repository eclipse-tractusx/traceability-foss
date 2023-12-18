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
import org.eclipse.tractusx.traceability.assets.domain.base.model.*;
import org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectModel;
import org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectType;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AssetTestDataFactory {


    public static AssetBase createAssetTestDataWithRelations(List<Descriptions> parents, List<Descriptions> childs) {
        AssetBase assetTestData = createAssetTestData();
        assetTestData.setParentRelations(parents);
        assetTestData.setChildRelations(childs);
        return assetTestData;
    }

    public static AssetBase createAssetTestData() {


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
                .activeAlert(false)
                .parentRelations(AssetTestDataFactory.provideParentRelations())
                .childRelations(AssetTestDataFactory.provideChildRelations())
                .van("OMAOYGBDTSRCMYSCX")
                .qualityType(QualityType.CRITICAL)
                .inInvestigation(false)
                .owner(Owner.OWN)
                .manufacturerName("manuName")
                .manufacturerId("BPNL00000003CML1")
                .detailAspectModels(detailAspectModels)
                .build();
    }

    public static List<Descriptions> provideParentRelations() {
        List<Descriptions> parentDescriptions = new ArrayList<>();
        parentDescriptions.add(new Descriptions("parent1", "desc1"));
        parentDescriptions.add(new Descriptions("parent2", "desc2"));
        return parentDescriptions;
    }

    public static List<Descriptions> provideChildRelations() {
        List<Descriptions> childDescriptions = new ArrayList<>();
        childDescriptions.add(new Descriptions("child1", "desc1"));
        childDescriptions.add(new Descriptions("child2", "desc2"));
        return childDescriptions;
    }
}
