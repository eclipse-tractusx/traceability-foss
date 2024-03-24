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

package org.eclipse.tractusx.traceability.integration.common.support;

import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.asplanned.model.AssetAsPlannedEntity;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.model.InvestigationEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.repository.JpaInvestigationRepository;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationSideBaseEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationStatusBaseEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationTypeEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.notification.model.NotificationEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.notification.repository.JpaNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Component
public class InvestigationsSupport {

    @Autowired
    JpaNotificationRepository jpaInvestigationRepository;

    public Long storeInvestigationWithStatusAndAssets(NotificationStatusBaseEntity status, List<AssetAsBuiltEntity> assetsAsBuilt) {
        return storeInvestigationWithStatusAndAssets(status, assetsAsBuilt, NotificationSideBaseEntity.RECEIVER);
    }

    public Long storeInvestigationWithStatusAndAssets(NotificationStatusBaseEntity status, List<AssetAsBuiltEntity> assetsAsBuilt, NotificationSideBaseEntity side) {
        NotificationEntity entity = NotificationEntity.builder()
                .assets(Collections.emptyList())
                .bpn("BPNL00000003AXS3")
                .status(status)
                .type(NotificationTypeEntity.INVESTIGATION)
                .side(side)
                .createdDate(Instant.now())
                .build();
        Long alertId = storedInvestigation(entity);
        NotificationEntity savedInvestigation = jpaInvestigationRepository.findById(alertId).get();
        savedInvestigation.setAssets(assetsAsBuilt);
        jpaInvestigationRepository.save(savedInvestigation);
        return alertId;
    }

    public void assertInvestigationsSize(int size) {
        List<NotificationEntity> investigations = jpaInvestigationRepository.findAll();

        assertThat(investigations).hasSize(size);
    }

    public void assertInvestigationStatus(QualityNotificationStatus investigationStatus) {
        jpaInvestigationRepository.findAll().forEach(
                investigation -> assertThat(investigation.getStatus().name()).isEqualTo(investigationStatus.name())
        );
    }

    public Long storedInvestigation(NotificationEntity investigation) {
        return jpaInvestigationRepository.save(investigation).getId();
    }

    public NotificationEntity storedInvestigationFullObject(NotificationEntity investigation) {
        return jpaInvestigationRepository.save(investigation);
    }

    public List<NotificationEntity> findAllInvestigations() {
        return jpaInvestigationRepository.findAll();
    }
}
