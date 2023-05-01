/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
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

package org.eclipse.tractusx.traceability.assets.infrastructure.adapters.jpa.asset;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import org.eclipse.tractusx.traceability.assets.domain.model.QualityType;
import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs.model.Owner;
import org.eclipse.tractusx.traceability.investigations.adapters.jpa.InvestigationEntity;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "asset")
public class AssetEntity {
    @Id
    private String id;
    private String idShort;
    private String nameAtManufacturer;
    private String manufacturerPartId;
    private String partInstanceId;
    private String manufacturerId;
    private String batchId;
    private String manufacturerName;
    private String nameAtCustomer;
    private String customerPartId;
    private Instant manufacturingDate;
    private String manufacturingCountry;
    private Owner owner;
    private QualityType qualityType;
    private String van;

    @ElementCollection
    @CollectionTable(name = "asset_child_descriptors")
    private List<ChildDescription> childDescriptors;

    @ElementCollection
    @CollectionTable(name = "asset_parent_descriptors")
    private List<ParentDescription> parentDescriptors;

    @ManyToMany(mappedBy = "assets")
    private List<InvestigationEntity> investigations = new ArrayList<>();

    public AssetEntity(String id, String idShort, String nameAtManufacturer,
                       String manufacturerPartId, String partInstanceId,
                       String manufacturerId, String batchId,
                       String manufacturerName, String nameAtCustomer,
                       String customerPartId, Instant manufacturingDate,
                       String manufacturingCountry, Owner owner,
                       List<ChildDescription> childDescriptors,
                       List<ParentDescription> parentDescriptors,
                       QualityType qualityType,
                       String van) {
        this.id = id;
        this.idShort = idShort;
        this.nameAtManufacturer = nameAtManufacturer;
        this.manufacturerPartId = manufacturerPartId;
        this.partInstanceId = partInstanceId;
        this.manufacturerId = manufacturerId;
        this.batchId = batchId;
        this.manufacturerName = manufacturerName;
        this.nameAtCustomer = nameAtCustomer;
        this.customerPartId = customerPartId;
        this.manufacturingDate = manufacturingDate;
        this.manufacturingCountry = manufacturingCountry;
        this.owner = owner;
        this.childDescriptors = childDescriptors;
        this.parentDescriptors = parentDescriptors;
        this.qualityType = qualityType;
        this.van = van;
    }

    public AssetEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String assetId) {
        this.id = assetId;
    }

    public List<ChildDescription> getChildDescriptors() {
        return childDescriptors;
    }

    public List<ParentDescription> getParentDescriptors() {
        return parentDescriptors;
    }

    public void setChildDescriptors(List<ChildDescription> specificAssetIds) {
        this.childDescriptors = specificAssetIds;
    }

    public void setParentDescriptors(List<ParentDescription> specificAssetIds) {
        this.parentDescriptors = specificAssetIds;
    }

    public String getIdShort() {
        return idShort;
    }

    public void setIdShort(String idShort) {
        this.idShort = idShort;
    }

    public String getNameAtManufacturer() {
        return nameAtManufacturer;
    }

    public void setNameAtManufacturer(String nameAtManufacturer) {
        this.nameAtManufacturer = nameAtManufacturer;
    }

    public String getManufacturerPartId() {
        return manufacturerPartId;
    }

    public void setManufacturerPartId(String manufacturerPartId) {
        this.manufacturerPartId = manufacturerPartId;
    }

    public String getPartInstanceId() {
        return partInstanceId;
    }

    public void setPartInstanceId(String partInstanceId) {
        this.partInstanceId = partInstanceId;
    }

    public String getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(String manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public String getNameAtCustomer() {
        return nameAtCustomer;
    }

    public void setNameAtCustomer(String nameAtCustomer) {
        this.nameAtCustomer = nameAtCustomer;
    }

    public String getCustomerPartId() {
        return customerPartId;
    }

    public void setCustomerPartId(String customerPartId) {
        this.customerPartId = customerPartId;
    }

    public Instant getManufacturingDate() {
        return manufacturingDate;
    }

    public void setManufacturingDate(Instant manufacturingDate) {
        this.manufacturingDate = manufacturingDate;
    }

    public String getManufacturingCountry() {
        return manufacturingCountry;
    }

    public void setManufacturingCountry(String manufacturingCountry) {
        this.manufacturingCountry = manufacturingCountry;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public QualityType getQualityType() {
        return qualityType;
    }

    public void setQualityType(QualityType qualityType) {
        this.qualityType = qualityType;
    }

    public List<InvestigationEntity> getInvestigations() {
        return investigations;
    }

    public void setInvestigations(List<InvestigationEntity> investigations) {
        this.investigations = investigations;
    }

    public boolean isOnInvestigation() {
        if (investigations == null || investigations.isEmpty()) {
            return false;
        }

        return investigations.stream()
                .allMatch(investigation -> investigation.getStatus() != InvestigationStatus.CLOSED);
    }

    public String getVan() {
        return van;
    }

    public void setVan(String van) {
        this.van = van;
    }

    @Embeddable
    public static class ChildDescription {
        private String id;
        private String idShort;

        public ChildDescription() {
        }

        public ChildDescription(String id, String idShort) {
            this.id = id;
            this.idShort = idShort;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setIdShort(String idShort) {
            this.idShort = idShort;
        }

        public String getId() {
            return id;
        }

        public String getIdShort() {
            return idShort;
        }

    }

    @Embeddable
    public static class ParentDescription {
        private String id;
        private String idShort;

        public ParentDescription() {
        }

        public ParentDescription(String id, String idShort) {
            this.id = id;
            this.idShort = idShort;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setIdShort(String idShort) {
            this.idShort = idShort;
        }

        public String getId() {
            return id;
        }

        public String getIdShort() {
            return idShort;
        }

    }

}
