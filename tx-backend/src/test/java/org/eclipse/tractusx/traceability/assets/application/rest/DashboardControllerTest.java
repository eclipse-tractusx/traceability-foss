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

package org.eclipse.tractusx.traceability.assets.application.rest;

import org.eclipse.tractusx.traceability.assets.domain.dashboard.model.Dashboard;
import org.eclipse.tractusx.traceability.assets.domain.dashboard.service.DashboardServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    DashboardServiceImpl dashboardService;

    @Test
    void dashboard() {
        Dashboard dashboard = Dashboard.builder()
                .asBuiltCustomerParts(9L)
                .asPlannedCustomerParts(99L)
                .asBuiltSupplierParts(999L)
                .asPlannedSupplierParts(1L)
                .asBuiltOwnParts(11L)
                .asPlannedOwnParts(111L)
                .myPartsWithOpenAlerts(1111L)
                .myPartsWithOpenInvestigations(1111L)
                .supplierPartsWithOpenAlerts(1111L)
                .customerPartsWithOpenAlerts(1111L)
                .supplierPartsWithOpenInvestigations(1111L)
                .customerPartsWithOpenInvestigations(1111L)
                .sentActiveInvestigations(5000L)
                .sentActiveAlerts(2000L)
                .receivedActiveInvestigations(2500L)
                .receivedActiveAlerts(3000L)
                .build();
        Mockito.when(dashboardService.getDashboard()).thenReturn(dashboard);
        Dashboard testDashboard = dashboardService.getDashboard();

        assertEquals(9, testDashboard.getAsBuiltCustomerParts());
        assertEquals(99, testDashboard.getAsPlannedCustomerParts());
        assertEquals(999, testDashboard.getAsBuiltSupplierParts());
        assertEquals(11, testDashboard.getAsBuiltOwnParts());
        assertEquals(111, testDashboard.getAsPlannedOwnParts());
        assertEquals(1111, testDashboard.getMyPartsWithOpenAlerts());
        assertEquals(1111, testDashboard.getMyPartsWithOpenInvestigations());
        assertEquals(1111, testDashboard.getSupplierPartsWithOpenAlerts());
        assertEquals(1111, testDashboard.getCustomerPartsWithOpenAlerts());
        assertEquals(1111, testDashboard.getSupplierPartsWithOpenInvestigations());
        assertEquals(1111, testDashboard.getCustomerPartsWithOpenInvestigations());
        assertEquals(5000, testDashboard.getSentActiveInvestigations());
        assertEquals(2000, testDashboard.getSentActiveAlerts());
        assertEquals(3000, testDashboard.getReceivedActiveAlerts());
        assertEquals(2500, testDashboard.getReceivedActiveInvestigations());

    }

}
