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

import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.traceability.assets.application.dashboard.service.DashboardService;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.repository.AssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.repository.AssetAsPlannedRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.domain.dashboard.model.Dashboard;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationSide;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationType;
import org.eclipse.tractusx.traceability.notification.domain.notification.repository.NotificationRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    //TODO: rework this to use Service classes instead of using the Repository directly
    private final AssetAsBuiltRepository assetAsBuiltRepository;
    private final AssetAsPlannedRepository assetAsPlannedRepository;
    private final NotificationRepository notificationRepository;

    @Override
    public Dashboard getDashboard() {
        long asBuiltCustomerParts = assetAsBuiltRepository.countAssetsByOwner(Owner.CUSTOMER);
        long asPlannedCustomerParts = assetAsPlannedRepository.countAssetsByOwner(Owner.CUSTOMER);
        long asBuiltSupplierParts = assetAsBuiltRepository.countAssetsByOwner(Owner.SUPPLIER);
        long asPlannedSupplierParts = assetAsPlannedRepository.countAssetsByOwner(Owner.SUPPLIER);
        long asBuiltOwnParts = assetAsBuiltRepository.countAssetsByOwner(Owner.OWN);
        long asPlannedOwnParts = assetAsPlannedRepository.countAssetsByOwner(Owner.OWN);


        long myPartsWithSentAlerts = notificationRepository.countOpenNotificationsByOwnershipAndNotificationType(List.of(Owner.OWN), NotificationType.ALERT);
        long myPartsWithReceivedInvestigations = notificationRepository.countOpenNotificationsByOwnershipAndNotificationType(List.of(Owner.OWN), NotificationType.INVESTIGATION);

        long supplierPartsWithOpenReceivedAlerts = notificationRepository.countOpenNotificationsByOwnershipAndNotificationType(List.of(Owner.SUPPLIER), NotificationType.ALERT);
        long supplierPartsWithOpenSentInvestigations = notificationRepository.countOpenNotificationsByOwnershipAndNotificationType(List.of(Owner.SUPPLIER), NotificationType.INVESTIGATION);

        long customerPartsWithOpenReceivedAlerts = notificationRepository.countOpenNotificationsByOwnershipAndNotificationType(List.of(Owner.CUSTOMER), NotificationType.ALERT);
        long customerPartsWithOpenSentInvestigations = notificationRepository.countOpenNotificationsByOwnershipAndNotificationType(List.of(Owner.CUSTOMER), NotificationType.INVESTIGATION);

        long receivedActiveInvestigations = notificationRepository.countNotificationsBySideAndType(NotificationSide.RECEIVER, NotificationType.INVESTIGATION);
        long sentActiveInvestigations = notificationRepository.countNotificationsBySideAndType(NotificationSide.SENDER, NotificationType.INVESTIGATION);

        long receivedActiveAlerts = notificationRepository.countNotificationsBySideAndType(NotificationSide.RECEIVER, NotificationType.ALERT);
        long sentActiveAlerts = notificationRepository.countNotificationsBySideAndType(NotificationSide.SENDER, NotificationType.ALERT);

        return Dashboard.builder()
                .asBuiltCustomerParts(asBuiltCustomerParts)
                .asPlannedCustomerParts(asPlannedCustomerParts)
                .asBuiltSupplierParts(asBuiltSupplierParts)
                .asPlannedSupplierParts(asPlannedSupplierParts)
                .asBuiltOwnParts(asBuiltOwnParts)
                .asPlannedOwnParts(asPlannedOwnParts)
                .myPartsWithOpenAlerts(myPartsWithSentAlerts)
                .myPartsWithOpenInvestigations(myPartsWithReceivedInvestigations)
                .supplierPartsWithOpenAlerts(supplierPartsWithOpenReceivedAlerts)
                .customerPartsWithOpenAlerts(customerPartsWithOpenReceivedAlerts)
                .supplierPartsWithOpenInvestigations(supplierPartsWithOpenSentInvestigations)
                .customerPartsWithOpenInvestigations(customerPartsWithOpenSentInvestigations)
                .receivedActiveAlerts(receivedActiveAlerts)
                .receivedActiveInvestigations(receivedActiveInvestigations)
                .sentActiveAlerts(sentActiveAlerts)
                .sentActiveInvestigations(sentActiveInvestigations)
                .build();
    }
}
