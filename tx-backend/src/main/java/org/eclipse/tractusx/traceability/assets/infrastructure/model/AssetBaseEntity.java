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
package org.eclipse.tractusx.traceability.assets.infrastructure.model;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.eclipse.tractusx.traceability.assets.domain.model.Owner;
import org.eclipse.tractusx.traceability.assets.domain.model.QualityType;

import java.time.Instant;

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@MappedSuperclass
public class AssetBaseEntity {

    @Id
    private String id;
    private String idShort;
    private String manufacturerId;
    private Owner owner;
    private String manufacturerName;



    private boolean inInvestigation;
    private boolean activeAlert;

    private QualityType qualityType;
    private String van;

    private Instant manufacturingDate;
    private String manufacturingCountry;
    private String manufacturerPartId;
    private String customerPartId;
    private String nameAtManufacturer;
    private String nameAtCustomer;
    private String semanticModelId;
}
