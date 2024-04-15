/********************************************************************************
 * Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
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

package org.eclipse.tractusx.traceability.ess.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

@AllArgsConstructor
@Builder
@Data
@Entity
@Immutable
@NoArgsConstructor
@Table(name = "v_ess_investigations")
public class VEssEntity {

    @Id
    private String id;
    private String jobId;
    private String rowNumber;
    private String manufacturerPartId;
    private String nameAtManufacturer;
    private String catenaxSiteId;
    private String bpns;
    private String companyName;
    private String status;
    private String impacted;
    private String response;
    private String created;
    private String updated;

    public static VEssEntity toDomain(VEssEntity ess) {
        return VEssEntity.builder()
            .id(ess.getId())
            .jobId(ess.getJobId())
            .rowNumber(ess.getRowNumber())
            .manufacturerPartId(ess.getManufacturerPartId())
            .nameAtManufacturer(ess.getNameAtManufacturer())
            .catenaxSiteId(ess.getCatenaxSiteId())
            .bpns(ess.getBpns())
            .companyName(ess.getCompanyName())
            .status(ess.getStatus())
            .impacted(ess.getImpacted())
            .response(ess.getResponse())
            .created(ess.getCreated())
            .updated(ess.getUpdated())
            .build();
    }

}
