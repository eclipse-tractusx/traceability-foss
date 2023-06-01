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

package org.eclipse.tractusx.traceability.assets.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Data
@Builder
public class Asset {
    private final String id;
    private final String idShort;
    private final String nameAtManufacturer;
    private final String manufacturerPartId;
    private final String partInstanceId;
    private final String manufacturerId;
    private final String batchId;
    private String manufacturerName;
    private final String nameAtCustomer;
    private final String customerPartId;
    private final Instant manufacturingDate;
    private final String manufacturingCountry;
    private Owner owner;

    @Singular
    private List<Descriptions> childDescriptions;
    @Singular
    private List<Descriptions> parentDescriptions;
    private boolean underInvestigation;
    private QualityType qualityType;
    private String van;
}
