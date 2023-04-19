/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
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

package org.eclipse.tractusx.traceability.investigations.domain.service;

import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.investigations.adapters.rest.model.InvestigationData;
import org.eclipse.tractusx.traceability.investigations.domain.model.Investigation;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationId;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationSide;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus;
import org.eclipse.tractusx.traceability.investigations.domain.model.exception.InvestigationNotFoundException;
import org.eclipse.tractusx.traceability.investigations.domain.ports.InvestigationsRepository;
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
class InvestigationsReadServiceTest {

    @Mock
    private InvestigationsRepository repository;
    @InjectMocks
    private InvestigationsReadService investigationsReadService;


    @Test
    void testFindNotPresentInvestigationThrowsException() {
        // given
        when(repository.findById(any(InvestigationId.class))).thenReturn(Optional.empty());

        // expect
        assertThrows(InvestigationNotFoundException.class, () -> investigationsReadService.findInvestigation(0L));
    }

    @Test
    void testFindExistingInvestigation() {
        // given
        when(repository.findById(any(InvestigationId.class))).thenReturn(Optional.of(
                InvestigationTestDataFactory.createInvestigationTestData(InvestigationStatus.ACKNOWLEDGED, InvestigationStatus.ACKNOWLEDGED)
        ));

        // expect
        InvestigationData investigationData = investigationsReadService.findInvestigation(0L);

        // then
        assertThat(investigationData).isNotNull();
    }

    @Test
    void testFindCreatedInvestigations() {
        // given
        when(repository.getInvestigations(any(InvestigationSide.class), any(Pageable.class))).thenReturn(new PageResult<>(
                List.of(
                        InvestigationTestDataFactory.createInvestigationTestData(InvestigationSide.SENDER),
                        InvestigationTestDataFactory.createInvestigationTestData(InvestigationSide.SENDER)
                )));

        // expect
        PageResult<InvestigationData> result = investigationsReadService.getCreatedInvestigations(PageRequest.of(0, 5));

        // then
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(2);
    }

    @Test
    void testFindReceivedInvestigations() {
        // given
        when(repository.getInvestigations(any(InvestigationSide.class), any(Pageable.class))).thenReturn(new PageResult<>(
                List.of(
                        InvestigationTestDataFactory.createInvestigationTestData(InvestigationSide.RECEIVER)
                )));

        // expect
        PageResult<InvestigationData> result = investigationsReadService.getReceivedInvestigations(PageRequest.of(0, 5));

        // then
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(1);
    }

    @Test
    void testLoadNotPresentInvestigationThrowsException() {
        // given
        when(repository.findById(any(InvestigationId.class))).thenReturn(Optional.empty());

        // expect
        InvestigationId investigationId = new InvestigationId(0L);
        assertThrows(InvestigationNotFoundException.class, () -> investigationsReadService.loadInvestigation(investigationId));
    }

    @Test
    void testLoadExistingInvestigation() {
        // given
        when(repository.findById(any(InvestigationId.class))).thenReturn(Optional.of(
                InvestigationTestDataFactory.createInvestigationTestData(InvestigationStatus.ACKNOWLEDGED, InvestigationStatus.ACKNOWLEDGED)
        ));

        // expect
        Investigation investigation = investigationsReadService.loadInvestigation(new InvestigationId(0L));

        // then
        assertThat(investigation).isNotNull();
    }

    @Test
    void testLoadNotPresentInvestigationByEdcNotificationIdThrowsException() {
        // given
        when(repository.findByEdcNotificationId(any())).thenReturn(Optional.empty());

        // expect
        assertThrows(InvestigationNotFoundException.class, () -> investigationsReadService.loadInvestigationByEdcNotificationId("0"));
    }

    @Test
    void testLoadPresentInvestigationByEdcNotificationId() {
        // given
        when(repository.findByEdcNotificationId(any())).thenReturn(Optional.of(
                        InvestigationTestDataFactory.createInvestigationTestData(InvestigationStatus.ACKNOWLEDGED, InvestigationStatus.ACKNOWLEDGED)
                )
        );

        // when
        Investigation investigation = investigationsReadService.loadInvestigationByEdcNotificationId("0");

        // then
        assertThat(investigation).isNotNull();
    }
}
