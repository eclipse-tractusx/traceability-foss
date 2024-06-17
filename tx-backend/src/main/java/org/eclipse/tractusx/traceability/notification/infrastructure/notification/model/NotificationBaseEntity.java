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
package org.eclipse.tractusx.traceability.notification.infrastructure.notification.model;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationSeverity;

import java.time.Instant;

@SuperBuilder
@NoArgsConstructor
@Data
@MappedSuperclass
public class NotificationBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String bpn;
    private String closeReason;
    private String acceptReason;
    private String declineReason;
    private String description;
    @Column(name = "created")
    private Instant createdDate;
    private Instant updated;
    @Enumerated(EnumType.STRING)
    private NotificationSideBaseEntity side;
    @Enumerated(EnumType.STRING)
    private NotificationStatusBaseEntity status;
    @Enumerated(EnumType.STRING)
    private NotificationSeverityBaseEntity severity;

}
