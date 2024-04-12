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

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { getI18nPageProvider } from '@core/i18n';
import { NotificationDetailFacade } from '@page/notifications/core/notification-detail.facade';
import { NotificationDetailState } from '@page/notifications/core/notification-detail.state';
import { NotificationHelperService } from '@page/notifications/core/notification-helper.service';
import { NotificationsFacade } from '@page/notifications/core/notifications.facade';
import { NotificationsState } from '@page/notifications/core/notifications.state';
import { NotificationEditComponent } from '@page/notifications/detail/edit/notification-edit.component';
import { NotificationDetailComponent } from '@page/notifications/detail/notification-detail.component';
import { NotificationsRoutingModule } from '@page/notifications/notifications.routing';
import { PartsModule } from '@page/parts/parts.module';
import { NotificationModule } from '@shared/modules/notification/notification.module';
import { FormatPaginationSemanticDataModelToCamelCasePipe } from '@shared/pipes/format-pagination-semantic-data-model-to-camelcase.pipe';
import { FormatPartSemanticDataModelToCamelCasePipe } from '@shared/pipes/format-part-semantic-data-model-to-camelcase.pipe';
import { FormatPartlistSemanticDataModelToCamelCasePipe } from '@shared/pipes/format-partlist-semantic-data-model-to-camelcase.pipe';
import { SharedModule } from '@shared/shared.module';
import { TemplateModule } from '@shared/template.module';
import { NotificationsComponent } from './presentation/notifications.component';


@NgModule({
  declarations: [
    NotificationsComponent, NotificationDetailComponent, NotificationEditComponent,
  ],
  imports: [
    CommonModule,
    TemplateModule,
    SharedModule,
    NotificationsRoutingModule,
    NotificationModule,
    PartsModule,
  ],
  providers: [
    NotificationsFacade,
    NotificationsState,
    NotificationDetailFacade,
    NotificationDetailState,
    NotificationHelperService,
    FormatPartSemanticDataModelToCamelCasePipe,
    FormatPaginationSemanticDataModelToCamelCasePipe,
    FormatPartlistSemanticDataModelToCamelCasePipe,
    ...getI18nPageProvider('page.alert'),
  ],
})
export class NotificationsModule {
}
