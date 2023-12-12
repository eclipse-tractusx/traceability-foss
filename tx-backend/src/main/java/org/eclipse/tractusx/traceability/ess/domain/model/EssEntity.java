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

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.*;

@AllArgsConstructor
@Builder
@Data
@Entity
@NoArgsConstructor
@Table(name = "ess_investigations")
@ToString
public class EssEntity implements Serializable {

    @Id
    @Column(name = "id")
    private String id;

    @Transient
    private String rowno;

    @Column(name = "part_id")
    private String partId;

    @Column(name = "part_name")
    private String partName;

    @Column(name = "bpns")
    private String bpns;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "job_id")
    private String jobId;

    @Column(name = "status")
    private String status;

    @Column(name = "impacted")
    private String impacted;

    @Column(name = "response")
    private String response;

    @Column(name = "created")
    private LocalDateTime created;

    @Column(name = "updated")
    private LocalDateTime updated;

    @PrePersist
    public void preCreate() {
        this.created = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updated = LocalDateTime.now();
    }

    public static EssEntity toDomain(EssEntity ess) {
        return EssEntity.builder()
            .id(ess.getId())
            .rowno(ess.getRowno())
            .partId(ess.getPartId())
            .partName(ess.getPartName())
            .bpns(ess.getBpns())
            .companyName(ess.getCompanyName())
            .jobId(ess.getJobId())
            .status(ess.getStatus())
            .impacted(ess.getImpacted())
            .response(ess.getResponse())
            .created(ess.getCreated())
            .updated(ess.getUpdated())
            .build();
    }
}
