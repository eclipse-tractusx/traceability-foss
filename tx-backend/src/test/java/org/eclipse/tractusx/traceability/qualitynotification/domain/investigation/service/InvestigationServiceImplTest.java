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
package org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.service;

import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.InvestigationRepository;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSide;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.exception.InvestigationNotFoundException;
import org.eclipse.tractusx.traceability.testdata.InvestigationTestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvestigationServiceImplTest {

    @Mock
    private InvestigationRepository investigationsRepositoryMock;
    @InjectMocks
    private InvestigationServiceImpl investigationService;

    @Test
    void testFindNotPresentInvestigationThrowsException() {
        // given
        when(investigationsRepositoryMock.findOptionalQualityNotificationById(any(QualityNotificationId.class))).thenReturn(Optional.empty());

        // expect
        assertThrows(InvestigationNotFoundException.class, () -> investigationService.find(0L));
    }

    @Test
    void testFindExistingInvestigation() {
        // given
        when(investigationsRepositoryMock.findOptionalQualityNotificationById(any(QualityNotificationId.class))).thenReturn(Optional.of(
                InvestigationTestDataFactory.createInvestigationTestData(QualityNotificationStatus.ACKNOWLEDGED, QualityNotificationStatus.ACKNOWLEDGED)
        ));

        // expect
        QualityNotification investigation = investigationService.find(0L);

        // then
        assertThat(investigation).isNotNull();
    }

    @Test
    void testFindCreatedInvestigations() {
        // given
        when(investigationsRepositoryMock.findQualityNotificationsBySide(any(QualityNotificationSide.class), any(Pageable.class))).thenReturn(new PageResult<>(
                List.of(
                        InvestigationTestDataFactory.createInvestigationTestData(QualityNotificationSide.SENDER),
                        InvestigationTestDataFactory.createInvestigationTestData(QualityNotificationSide.SENDER)
                )));

        // expect
        PageResult<QualityNotification> result = investigationService.getCreated(PageRequest.of(0, 5));

        // then
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(2);
    }

    @Test
    void testFindCreatedInvestigationsWithSearchCriteria() {
        // given
        when(investigationsRepositoryMock.findAll(any(Pageable.class), any(SearchCriteria.class))).thenReturn(new PageResult<>(
                List.of(
                        InvestigationTestDataFactory.createInvestigationTestData(QualityNotificationSide.SENDER),
                        InvestigationTestDataFactory.createInvestigationTestData(QualityNotificationSide.SENDER)
                )));
        SearchCriteria searchCriteria = InvestigationTestDataFactory.createSearchCriteria();

        // expect
        PageResult<QualityNotification> result = investigationService.getCreated(PageRequest.of(0, 5), searchCriteria);

        // then
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(2);
    }

    @Test
    void testFindReceivedInvestigations() {
        // given
        when(investigationsRepositoryMock.findQualityNotificationsBySide(any(QualityNotificationSide.class), any(Pageable.class))).thenReturn(new PageResult<>(
                List.of(
                        InvestigationTestDataFactory.createInvestigationTestData(QualityNotificationSide.RECEIVER)
                )));

        // expect
        PageResult<QualityNotification> result = investigationService.getReceived(PageRequest.of(0, 5));

        // then
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(1);
    }

    @Test
    void testFindReceivedInvestigationsWithSearchCriteria() {
        // given
        when(investigationsRepositoryMock.findAll(any(Pageable.class), any(SearchCriteria.class))).thenReturn(new PageResult<>(
                List.of(
                        InvestigationTestDataFactory.createInvestigationTestData(QualityNotificationSide.RECEIVER)
                )));
        SearchCriteria searchCriteria = InvestigationTestDataFactory.createSearchCriteria();

        // expect
        PageResult<QualityNotification> result = investigationService.getReceived(PageRequest.of(0, 5), searchCriteria);

        // then
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(1);
    }



    @Test
    void testLoadNotPresentInvestigationThrowsException() {
        // given
        when(investigationsRepositoryMock.findOptionalQualityNotificationById(any(QualityNotificationId.class))).thenReturn(Optional.empty());

        // expect
        QualityNotificationId investigationId = new QualityNotificationId(0L);
        assertThrows(InvestigationNotFoundException.class, () -> investigationService.loadOrNotFoundException(investigationId));
    }

    @Test
    void testLoadExistingInvestigation() {
        // given
        when(investigationsRepositoryMock.findOptionalQualityNotificationById(any(QualityNotificationId.class))).thenReturn(Optional.of(
                InvestigationTestDataFactory.createInvestigationTestData(QualityNotificationStatus.ACKNOWLEDGED, QualityNotificationStatus.ACKNOWLEDGED)
        ));

        // expect
        QualityNotification investigation = investigationService.loadOrNotFoundException(new QualityNotificationId(0L));

        // then
        assertThat(investigation).isNotNull();
    }

    @Test
    void testLoadNotPresentInvestigationByEdcNotificationIdThrowsException() {
        // given
        when(investigationsRepositoryMock.findByEdcNotificationId(any())).thenReturn(Optional.empty());

        // expect
        assertThrows(InvestigationNotFoundException.class, () -> investigationService.loadByEdcNotificationIdOrNotFoundException("0"));
    }

    @Test
    void testLoadPresentInvestigationByEdcNotificationId() {
        // given
        when(investigationsRepositoryMock.findByEdcNotificationId(any())).thenReturn(Optional.of(
                        InvestigationTestDataFactory.createInvestigationTestData(QualityNotificationStatus.ACKNOWLEDGED, QualityNotificationStatus.ACKNOWLEDGED)
                )
        );

        // when
        QualityNotification investigation = investigationService.loadByEdcNotificationIdOrNotFoundException("0");

        // then
        assertThat(investigation).isNotNull();
    }
}
