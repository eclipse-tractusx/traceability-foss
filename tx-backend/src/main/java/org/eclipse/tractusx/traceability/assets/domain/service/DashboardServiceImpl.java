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

package org.eclipse.tractusx.traceability.assets.domain.service;

import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.traceability.assets.application.rest.service.DashboardService;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.AssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.AssetAsPlannedRepository;
import org.eclipse.tractusx.traceability.assets.domain.model.Dashboard;
import org.eclipse.tractusx.traceability.assets.domain.model.Owner;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.repository.InvestigationRepository;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final AssetAsBuiltRepository assetAsBuiltRepository;
    private final AssetAsPlannedRepository assetAsPlannedRepository;
    private final InvestigationRepository investigationsRepository;

    public Dashboard getDashboard() {
        long customerParts = assetAsBuiltRepository.countAssetsByOwner(Owner.CUSTOMER) + assetAsPlannedRepository.countAssetsByOwner(Owner.CUSTOMER);
        long supplierParts = assetAsBuiltRepository.countAssetsByOwner(Owner.SUPPLIER) + assetAsPlannedRepository.countAssetsByOwner(Owner.SUPPLIER);
        ;
        long otherParts = customerParts + supplierParts;
        long ownParts = assetAsBuiltRepository.countAssetsByOwner(Owner.OWN) + assetAsPlannedRepository.countAssetsByOwner(Owner.OWN);
        long pendingInvestigations = investigationsRepository.countQualityNotificationEntitiesByStatus(QualityNotificationStatus.RECEIVED);
        return new Dashboard(ownParts, otherParts, pendingInvestigations);
    }
}
