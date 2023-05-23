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

package org.eclipse.tractusx.traceability.assets.infrastructure.adapters.jpa.asset;

import org.eclipse.tractusx.traceability.assets.domain.model.Asset;
import org.eclipse.tractusx.traceability.assets.domain.model.Descriptions;
import org.eclipse.tractusx.traceability.assets.domain.model.Owner;
import org.eclipse.tractusx.traceability.assets.domain.model.QualityType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class PersistentAssetsRepositoryTest {

    @InjectMocks
    private PersistentAssetsRepository persistentAssetsRepository;

    @Mock
    private JpaAssetsRepository jpaAssetsRepository;

    @Test
    void testToAsset() {
        // Given
        AssetEntity entity = new AssetEntity();
        entity.setIdShort("AS01");
        entity.setNameAtManufacturer("Asset 1");
        entity.setManufacturerPartId("MP001");
        entity.setPartInstanceId("PI001");
        entity.setManufacturerId("M01");
        entity.setBatchId("B001");
        entity.setManufacturerName("Manufacturer 1");
        entity.setNameAtCustomer("Customer Asset 1");
        entity.setCustomerPartId("CP001");
        entity.setManufacturingDate(Instant.ofEpochSecond(11111111L));
        entity.setManufacturingCountry("USA");
        entity.setOwner(Owner.OWN);
        entity.setQualityType(QualityType.OK);
        entity.setVan("V001");

        // add child and parent descriptors
        AssetEntity.ChildDescription child1 = new AssetEntity.ChildDescription();
        child1.setId("C001");
        child1.setIdShort("CD01");
        entity.setChildDescriptors(List.of(child1));

        AssetEntity.ParentDescription parent1 = new AssetEntity.ParentDescription();
        parent1.setId("P001");
        parent1.setIdShort("PD01");
        entity.setParentDescriptors(List.of(parent1));

        // when
        Asset asset = persistentAssetsRepository.toAsset(entity);


        // then
        Asset expected = new Asset(
                null, "AS01", "Asset 1", "MP001", "PI001", "M01",
                "B001", "Manufacturer 1", "Customer Asset 1", "CP001",
                Instant.ofEpochSecond(11111111L), "USA", Owner.OWN,
                List.of(new Descriptions("C001", "CD01")),
                List.of(new Descriptions("P001", "PD01")),
                false, QualityType.OK, "V001");
        Assertions.assertEquals(asset.getId(), expected.getId());
        Assertions.assertEquals(asset.getIdShort(), expected.getIdShort());
        Assertions.assertEquals(asset.getNameAtManufacturer(), expected.getNameAtManufacturer());
        Assertions.assertEquals(asset.getManufacturerPartId(), expected.getManufacturerPartId());
        Assertions.assertEquals(asset.getPartInstanceId(), expected.getPartInstanceId());
        Assertions.assertEquals(asset.getManufacturerId(), expected.getManufacturerId());
        Assertions.assertEquals(asset.getBatchId(), expected.getBatchId());
        Assertions.assertEquals(asset.getManufacturerName(), expected.getManufacturerName());
        Assertions.assertEquals(asset.getNameAtCustomer(), expected.getNameAtCustomer());
        Assertions.assertEquals(asset.getCustomerPartId(), expected.getCustomerPartId());
        Assertions.assertEquals(asset.getManufacturingDate(), expected.getManufacturingDate());
        Assertions.assertEquals(asset.getManufacturingCountry(), expected.getManufacturingCountry());
        Assertions.assertEquals(asset.getOwner(), expected.getOwner());
        Assertions.assertEquals(asset.getChildDescriptions(), expected.getChildDescriptions());
        Assertions.assertEquals(asset.getParentDescriptions(), expected.getParentDescriptions());
        Assertions.assertEquals(asset.isUnderInvestigation(), expected.isUnderInvestigation());
        Assertions.assertEquals(asset.getQualityType(), expected.getQualityType());
        Assertions.assertEquals(asset.getVan(), expected.getVan());
    }

}
