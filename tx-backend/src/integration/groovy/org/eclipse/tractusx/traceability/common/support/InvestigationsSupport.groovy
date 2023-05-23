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

package org.eclipse.tractusx.traceability.common.support

import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationStatus
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.model.InvestigationEntity
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.QualityNotificationSideBaseEntity
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.QualityNotificationStatusBaseEntity

import java.time.Instant

trait InvestigationsSupport implements InvestigationsRepositoryProvider {

    Long defaultReceivedInvestigationStored() {
        InvestigationEntity entity = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn("BPNL00000003AXS3")
                .status(QualityNotificationStatusBaseEntity.RECEIVED)
                .side(QualityNotificationSideBaseEntity.RECEIVER)
                .description("some description")
                .created(Instant.now())
                .build();

        return storedInvestigation(entity)
    }

    Long defaultAcknowledgedInvestigationStored() {
        InvestigationEntity entity = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn("BPNL00000003AXS3")
                .status(QualityNotificationStatusBaseEntity.ACKNOWLEDGED)
                .side(QualityNotificationSideBaseEntity.RECEIVER)
                .created(Instant.now())
                .build();

        return storedInvestigation(entity)
    }

    void assertInvestigationsSize(int size) {
        List<InvestigationEntity> investigations = jpaInvestigationRepository().findAll()

        assert investigations.size() == size
    }

    void assertInvestigationStatus(QualityNotificationStatus investigationStatus) {
        jpaInvestigationRepository().findAll().each {
            assert it.status.name() == investigationStatus.name()
        }
    }

    void storedInvestigations(InvestigationEntity... investigations) {
        investigations.each {
            jpaInvestigationRepository().save(it)
        }
    }

    Long storedInvestigation(InvestigationEntity investigation) {
        return jpaInvestigationRepository().save(investigation).id
    }

    InvestigationEntity storedInvestigationFullObject(InvestigationEntity investigation) {
        return jpaInvestigationRepository().save(investigation);
    }
}
