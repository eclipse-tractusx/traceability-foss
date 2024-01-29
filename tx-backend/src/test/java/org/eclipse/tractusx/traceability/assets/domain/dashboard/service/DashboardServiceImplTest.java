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

package org.eclipse.tractusx.traceability.assets.domain.dashboard.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.tractusx.traceability.assets.domain.asbuilt.repository.AssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.repository.AssetAsPlannedRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.domain.dashboard.model.Dashboard;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaFilter;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaOperator;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaStrategy;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.AlertRepository;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.InvestigationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private AssetAsBuiltRepository assetAsBuiltRepository;
    @Mock
    private AssetAsPlannedRepository assetAsPlannedRepository;
    @Mock
    private InvestigationRepository investigationsRepository;
    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @Captor
    ArgumentCaptor<SearchCriteria> investigationSearchCriteriaArgumentCaptor;
    @Captor
    ArgumentCaptor<SearchCriteria> alertSearchCriteriaArgumentCaptor;

//    @Test
//    void givenAllRepositories_whenGetDashboard_executing() {
//
//        // given
//        when(assetAsBuiltRepository.countAssetsByOwner(Owner.OWN)).thenReturn(1L);
//        when(assetAsPlannedRepository.countAssetsByOwner(Owner.OWN)).thenReturn(2L);
//
//        when(assetAsBuiltRepository.countAssetsByOwner(Owner.SUPPLIER)).thenReturn(3L);
//        when(assetAsPlannedRepository.countAssetsByOwner(Owner.SUPPLIER)).thenReturn(4L);
//
//        when(assetAsBuiltRepository.countAssetsByOwner(Owner.CUSTOMER)).thenReturn(5L);
//        when(assetAsPlannedRepository.countAssetsByOwner(Owner.CUSTOMER)).thenReturn(6L);
//
//        when(investigationsRepository.countAll(isA(SearchCriteria.class))).thenReturn(7L);
//        when(alertRepository.countAll(isA(SearchCriteria.class))).thenReturn(8L);
//
//        // when
//        final Dashboard dashboard = dashboardService.getDashboard();
//
//        // then
//        assertThat(dashboard.getMyParts(), is(3L));
//        assertThat(dashboard.getOtherParts(), is(18L));
//        assertThat(dashboard.getInvestigationsReceived(), is(7L));
//        assertThat(dashboard.getAlertsReceived(), is(8L));
//
//        verify(assetAsBuiltRepository).countAssetsByOwner(Owner.OWN);
//        verify(assetAsBuiltRepository).countAssetsByOwner(Owner.SUPPLIER);
//        verify(assetAsBuiltRepository).countAssetsByOwner(Owner.CUSTOMER);
//
//        verify(assetAsPlannedRepository).countAssetsByOwner(Owner.OWN);
//        verify(assetAsPlannedRepository).countAssetsByOwner(Owner.SUPPLIER);
//        verify(assetAsPlannedRepository).countAssetsByOwner(Owner.CUSTOMER);
//
//        verify(investigationsRepository).countAll(investigationSearchCriteriaArgumentCaptor.capture());
//        final SearchCriteria investigationSearchCriteria = investigationSearchCriteriaArgumentCaptor.getValue();
//        assertThat(investigationSearchCriteria.getSearchCriteriaOperator(), is(SearchCriteriaOperator.AND));
//        assertThat(investigationSearchCriteria.getSearchCriteriaFilterList()
//                        .stream()
//                        .filter(filter -> "status".equals(filter.getKey()))
//                        .filter(filter -> SearchStrategy.EQUAL.equals(filter.getStrategy()))
//                        .map(SearchCriteriaFilter::getValue)
//                        .toList(),
//                containsInAnyOrder("CREATED", "SENT", "RECEIVED", "ACKNOWLEDGED", "ACCEPTED", "DECLINED"));
//        assertThat(investigationSearchCriteria.getSearchCriteriaFilterList()
//                        .stream()
//                        .filter(filter -> "side".equals(filter.getKey()))
//                        .filter(filter -> SearchStrategy.EQUAL.equals(filter.getStrategy()))
//                        .map(SearchCriteriaFilter::getValue)
//                        .toList(),
//                containsInAnyOrder("RECEIVER"));
//
//        verify(alertRepository).countAll(alertSearchCriteriaArgumentCaptor.capture());
//        final SearchCriteria alertSearchCriteria = alertSearchCriteriaArgumentCaptor.getValue();
//        assertThat(alertSearchCriteria.getSearchCriteriaOperator(), is(SearchCriteriaOperator.AND));
//        assertThat(alertSearchCriteria.getSearchCriteriaFilterList()
//                        .stream()
//                        .filter(filter -> "status".equals(filter.getKey()))
//                        .filter(filter -> SearchStrategy.EQUAL.equals(filter.getStrategy()))
//                        .map(SearchCriteriaFilter::getValue)
//                        .toList(),
//                containsInAnyOrder("CREATED", "SENT", "RECEIVED", "ACKNOWLEDGED", "ACCEPTED", "DECLINED"));
//        assertThat(alertSearchCriteria.getSearchCriteriaFilterList()
//                        .stream()
//                        .filter(filter -> "side".equals(filter.getKey()))
//                        .filter(filter -> SearchStrategy.EQUAL.equals(filter.getStrategy()))
//                        .map(SearchCriteriaFilter::getValue)
//                        .toList(),
//                containsInAnyOrder("RECEIVER"));
//    }
}
