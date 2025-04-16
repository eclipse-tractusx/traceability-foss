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
import { RoleGuard } from '@core/user/role.guard';
import { KnownAdminRoutes } from '@page/admin/core/admin.model';
import { BpnConfigurationComponent } from '@page/admin/presentation/bpn-configuration/bpn-configuration.component';
import { ContractDetailComponent } from '@page/admin/presentation/contracts/contract-detail/contract-detail.component';
import { ContractsComponent } from '@page/admin/presentation/contracts/contracts.component';
import { ImportJsonComponent } from '@page/admin/presentation/import-json/import-json.component';
import { PoliciesComponent } from '@page/admin/presentation/policy-management/policies/policies.component';
import { PolicyEditorComponent } from '@page/admin/presentation/policy-management/policy-editor/policy-editor.component';
import { I18NEXT_NAMESPACE_RESOLVER } from 'angular-i18next';
import { DigitalTwinPartComponent } from '@page/digital-twin-part/presentation/digital-twin-part.component';
import { DigitalTwinPartDetailComponent } from '@page/digital-twin-part/detail/digital-twin-part-detail.component';


export /** @type {*} */
const ADMIN_ROUTING: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: KnownAdminRoutes.BPN,
  },
  {
    path: KnownAdminRoutes.BPN,
    pathMatch: 'full',
    component: BpnConfigurationComponent,
    data: { i18nextNamespaces: [ 'page.admin' ] },
    resolve: { i18next: I18NEXT_NAMESPACE_RESOLVER },
    canActivate: [ RoleGuard ],
  },
  {
    path: KnownAdminRoutes.IMPORT,
    pathMatch: 'full',
    component: ImportJsonComponent,
    data: { i18nextNamespaces: [ 'page.admin' ] },
    resolve: { i18next: I18NEXT_NAMESPACE_RESOLVER },
  },
  {
    path: KnownAdminRoutes.CONTRACT,
    pathMatch: 'full',
    component: ContractsComponent,
    data: { i18nextNamespaces: [ 'page.admin' ] },
    resolve: { i18next: I18NEXT_NAMESPACE_RESOLVER },
    canActivate: [ RoleGuard ],
  },
  {
    path: KnownAdminRoutes.CONTRACT_DETAIL_VIEW,
    pathMatch: 'full',
    component: ContractDetailComponent,
    data: { i18nextNamespaces: [ 'page.admin' ] },
    resolve: { i18next: I18NEXT_NAMESPACE_RESOLVER },
    canActivate: [ RoleGuard ],
  },
  {
    path: KnownAdminRoutes.POLICY_MANAGEMENT,
    pathMatch: 'full',
    component: PoliciesComponent,
    data: { i18nextNamespaces: [ 'page.admin' ] },
    resolve: { i18next: I18NEXT_NAMESPACE_RESOLVER },
    canActivate: [ RoleGuard ],
  },
  {
    path: KnownAdminRoutes.POLICY_MANAGEMENT_EDIT,
    pathMatch: 'full',
    component: PolicyEditorComponent,
    data: { i18nextNamespaces: [ 'page.admin' ] },
    resolve: { i18next: I18NEXT_NAMESPACE_RESOLVER },
    canActivate: [ RoleGuard ],
  },
  {
    path: KnownAdminRoutes.POLICY_MANAGEMENT_CREATE,
    pathMatch: 'full',
    component: PolicyEditorComponent,
    data: { i18nextNamespaces: [ 'page.admin' ] },
    resolve: { i18next: I18NEXT_NAMESPACE_RESOLVER },
    canActivate: [ RoleGuard ],
  },
  {
    path: KnownAdminRoutes.POLICY_MANAGEMENT_DETAIL_VIEW,
    pathMatch: 'full',
    component: PolicyEditorComponent,
    data: { i18nextNamespaces: [ 'page.admin' ] },
    resolve: { i18next: I18NEXT_NAMESPACE_RESOLVER },
    canActivate: [ RoleGuard ],
  },
  {
    path: KnownAdminRoutes.DIGITAL_TWIN_PART,
    pathMatch: 'full',
    component: DigitalTwinPartComponent,
    data: { i18nextNamespaces: [ 'page.admin' ] },
    resolve: { i18next: I18NEXT_NAMESPACE_RESOLVER },
    canActivate: [ RoleGuard ],
  }, 
  {
    path: KnownAdminRoutes.DIGITAL_TWIN_PART_DETAIL,
    component: DigitalTwinPartDetailComponent,
    data: { i18nextNamespaces: [ 'page.admin' ] },
    resolve: { i18next: I18NEXT_NAMESPACE_RESOLVER },
    canActivate: [ RoleGuard ],
  }
  
];

@NgModule({
  imports: [ RouterModule.forChild(ADMIN_ROUTING) ],
  exports: [ RouterModule ],
})
export class AdminRoutingModule {
}
