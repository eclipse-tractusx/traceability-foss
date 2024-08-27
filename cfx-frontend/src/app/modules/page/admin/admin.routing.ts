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

import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { I18NEXT_NAMESPACE_RESOLVER } from 'angular-i18next';
import { ScheduledRegistryProcessesComponent } from '@page/admin/presentation/scheduled-registry-processes/scheduled-registry-processes.component';
import { KnownAdminRouts } from '@page/admin/core/admin.model';
import { BpnConfigurationComponent } from '@page/admin/presentation/bpn-configuration/bpn-configuration.component';
import { RoleGuard } from '@core/user/role.guard';

export /** @type {*} */
const ADMIN_ROUTING: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: KnownAdminRouts.BPN,
  },
  {
    path: KnownAdminRouts.BPN,
    pathMatch: 'full',
    component: BpnConfigurationComponent,
    data: { i18nextNamespaces: ['page.admin'] },
    resolve: { i18next: I18NEXT_NAMESPACE_RESOLVER },
    canActivate: [RoleGuard],
  },
];

@NgModule({
  imports: [RouterModule.forChild(ADMIN_ROUTING)],
  exports: [RouterModule],
})
export class AdminRoutingModule {}
