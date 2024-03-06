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

package org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.semanticdatamodel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
@Slf4j
@NoArgsConstructor
public class SemanticDataModel {

    PartTypeInformation partTypeInformation;
    ManufacturingInformation manufacturingInformation;
    List<LocalId> localIdentifiers;
    ValidityPeriod validityPeriod;
    List<Site> sites;
    String aspectType;
    String catenaXId;
    String identification;

    public SemanticDataModel(
            String catenaXId,
            PartTypeInformation partTypeInformation,
            ManufacturingInformation manufacturingInformation,
            List<LocalId> localIdentifiers,
            ValidityPeriod validityPeriod,
            List<Site> sites,
            String aspectType) {
        this.catenaXId = catenaXId;
        this.partTypeInformation = partTypeInformation;
        this.manufacturingInformation = manufacturingInformation;
        this.localIdentifiers = Objects.requireNonNullElse(localIdentifiers, Collections.emptyList());
        this.validityPeriod = validityPeriod;
        this.sites = Objects.requireNonNullElse(sites, Collections.emptyList());
        this.aspectType = aspectType;
    }

    public String catenaXId() {
        return catenaXId;
    }

    public String identification() {
        return identification;
    }

    public ManufacturingInformation manufacturingInformation() {
        return manufacturingInformation;
    }

    public List<LocalId> localIdentifiers() {
        return localIdentifiers;
    }

    public List<Site> sites() {
        return sites;
    }

    public String aspectType() {
        return aspectType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SemanticDataModel) obj;
        return Objects.equals(this.catenaXId, that.catenaXId) &&
                Objects.equals(this.partTypeInformation, that.partTypeInformation) &&
                Objects.equals(this.manufacturingInformation, that.manufacturingInformation) &&
                Objects.equals(this.localIdentifiers, that.localIdentifiers) &&
                Objects.equals(this.validityPeriod, that.validityPeriod) &&
                Objects.equals(this.sites, that.sites) &&
                Objects.equals(this.aspectType, that.aspectType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(catenaXId, partTypeInformation, manufacturingInformation, localIdentifiers, validityPeriod, sites, aspectType);
    }

    @Override
    public String toString() {
        return "SemanticDataModel[" +
                "catenaXId=" + catenaXId + ", " +
                "partTypeInformation=" + partTypeInformation + ", " +
                "manufacturingInformation=" + manufacturingInformation + ", " +
                "localIdentifiers=" + localIdentifiers + ", " +
                "validityPeriod=" + validityPeriod + ", " +
                "sites=" + sites + ", " +
                "aspectType=" + aspectType + ']';
    }

    public boolean isAsPlanned() {
        return aspectType.contains("AsPlanned");
    }

    public boolean isAsBuilt() {
        return !aspectType.contains("AsPlanned");
    }

}
