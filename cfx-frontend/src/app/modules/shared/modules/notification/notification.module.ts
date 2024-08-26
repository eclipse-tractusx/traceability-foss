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

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { NotificationCommonModalComponent } from '@shared/components/notification-common-modal/notification-common-modal.component';
import { ModalModule } from '@shared/modules/modal/modal.module';
import { NotificationActionModalComponent } from '@shared/modules/notification/modal/actions/notification-action-modal.component';
import { NotificationTabComponent } from '@shared/modules/notification/notification-tab/notification-tab.component';
import { SharedModule } from '@shared/shared.module';
import { TemplateModule } from '@shared/template.module';
import { NotificationComponent } from './presentation/notification.component';

@NgModule({
  declarations: [
    NotificationComponent,
    NotificationTabComponent,
    NotificationActionModalComponent,
    NotificationCommonModalComponent,
  ],
  imports: [ CommonModule, TemplateModule, SharedModule, ModalModule ],
  exports: [
    NotificationCommonModalComponent,
    NotificationComponent,
    NotificationTabComponent,
    NotificationActionModalComponent,
  ],
})
export class NotificationModule {
}
