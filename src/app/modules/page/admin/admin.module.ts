/********************************************************************************
 * Copyright (c) 2022,2023
 *        2022: Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *        2022: ZF Friedrichshafen AG
 * Copyright (c) 2022,2023 Contributors to the Eclipse Foundation
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

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { getI18nPageProvider } from '@core/i18n';
import { AdminFacade } from '@page/admin/core/admin.facade';
import { AdminService } from '@page/admin/core/admin.service';
import { AdminState } from '@page/admin/core/admin.state';
import { SharedModule } from '@shared/shared.module';
import { TemplateModule } from '@shared/template.module';
import { AdminRoutingModule } from './admin.routing';
import { AdminComponent } from './presentation/admin.component';
import { ScheduledRegistryProcessesComponent } from './presentation/scheduled-registry-processes/scheduled-registry-processes.component';

@NgModule({
  declarations: [AdminComponent, ScheduledRegistryProcessesComponent],
  imports: [CommonModule, TemplateModule, SharedModule, AdminRoutingModule],
  providers: [...getI18nPageProvider('page.admin'), AdminService, AdminFacade, AdminState],
})
export class AdminModule {}
