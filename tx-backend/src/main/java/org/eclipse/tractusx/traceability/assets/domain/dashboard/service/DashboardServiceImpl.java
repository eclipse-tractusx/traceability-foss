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

package org.eclipse.tractusx.traceability.assets.domain.dashboard.service;

import static org.eclipse.tractusx.traceability.common.model.SearchStrategy.EQUAL;

import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.traceability.assets.application.dashboard.service.DashboardService;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.repository.AssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.repository.AssetAsPlannedRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.domain.dashboard.model.Dashboard;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaFilter;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaOperator;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.AlertRepository;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.InvestigationRepository;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSide;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private static final SearchCriteria RECEIVED_ACTIVE_NOTIFICATIONS = SearchCriteria.builder()
            .searchCriteriaFilterList(
                    Stream.concat(
                            Stream.of(SearchCriteriaFilter.builder()
                                    .key("side").strategy(EQUAL).value(QualityNotificationSide.RECEIVER.name())
                                    .build()),
                            QualityNotificationStatus.ACTIVE_STATES.stream()
                                    .map(status -> SearchCriteriaFilter.builder()
                                            .key("status").strategy(EQUAL).value(status.name())
                                            .build())
                    ).toList()
            )
            .searchCriteriaOperator(SearchCriteriaOperator.AND)
            .build();

    private final AssetAsBuiltRepository assetAsBuiltRepository;
    private final AssetAsPlannedRepository assetAsPlannedRepository;
    private final InvestigationRepository investigationsRepository;
    private final AlertRepository alertRepository;

    @Override
    public Dashboard getDashboard() {

        final long myParts = assetAsBuiltRepository.countAssetsByOwner(Owner.OWN)
                + assetAsPlannedRepository.countAssetsByOwner(Owner.OWN);

        final long supplierParts = assetAsBuiltRepository.countAssetsByOwner(Owner.SUPPLIER) +
                assetAsPlannedRepository.countAssetsByOwner(Owner.SUPPLIER);

        final long customerParts = assetAsBuiltRepository.countAssetsByOwner(Owner.CUSTOMER)
                + assetAsPlannedRepository.countAssetsByOwner(Owner.CUSTOMER);

        final long investigationsReceived = investigationsRepository.countAll(RECEIVED_ACTIVE_NOTIFICATIONS);
        final long alertsReceived = alertRepository.countAll(RECEIVED_ACTIVE_NOTIFICATIONS);

        return Dashboard.builder()
                .myParts(myParts)
                .otherParts(supplierParts + customerParts)
                .investigationsReceived(investigationsReceived)
                .alertsReceived(alertsReceived)
                .build();
    }
}
