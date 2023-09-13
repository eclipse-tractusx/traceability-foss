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

import org.eclipse.tractusx.traceability.assets.domain.base.model.*;

import java.time.Instant;
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
        Instant manufacturingDate = Instant.now();

// TODO add detailAspectModels

        return AssetBase.builder()
                .id("1")
                .idShort("1234")
                .semanticModelId("456")
                .semanticDataModel(SemanticDataModel.SERIALPART)
                .activeAlert(false)
                .parentRelations(AssetTestDataFactory.provideParentRelations())
                .childRelations(AssetTestDataFactory.provideChildRelations())
                .van("van123")
                .qualityType(QualityType.CRITICAL)
                .underInvestigation(false)
                .owner(Owner.OWN)
                .manufacturerName("manuName")
                .manufacturerId("manuId")
                .build();
    }

    public static AssetBase createAssetParentTestData() {
        Instant manufacturingDate = Instant.now();

// TODO add detailAspectModels

        return AssetBase.builder()
                .id("2")
                .idShort("23456")
                .semanticModelId("456")
                .semanticDataModel(SemanticDataModel.SERIALPART)
                .activeAlert(false)
                .parentRelations(AssetTestDataFactory.provideParentRelations())
                .childRelations(AssetTestDataFactory.provideChildRelations())
                .van("van123")
                .qualityType(QualityType.CRITICAL)
                .underInvestigation(false)
                .owner(Owner.OWN)
                .manufacturerName("manuName")
                .manufacturerId("manuId")
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
