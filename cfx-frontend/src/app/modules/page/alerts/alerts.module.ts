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
import { AlertsRoutingModule } from '@page/alerts/alerts.routing';
import { AlertDetailFacade } from '@page/alerts/core/alert-detail.facade';
import { AlertDetailState } from '@page/alerts/core/alert-detail.state';
import { AlertHelperService } from '@page/alerts/core/alert-helper.service';
import { AlertsFacade } from '@page/alerts/core/alerts.facade';
import { AlertsState } from '@page/alerts/core/alerts.state';
import { AlertDetailComponent } from '@page/alerts/detail/alert-detail.component';
import { PartsModule } from '@page/parts/parts.module';
import { NotificationModule } from '@shared/modules/notification/notification.module';
import { FormatPartSemanticDataModelToCamelCasePipe } from '@shared/pipes/format-part-semantic-data-model-to-camelcase.pipe';
import { FormatPaginationSemanticDataModelToCamelCasePipe } from '@shared/pipes/format-pagination-semantic-data-model-to-camelcase.pipe';
import { FormatPartlistSemanticDataModelToCamelCasePipe } from '@shared/pipes/format-partlist-semantic-data-model-to-camelcase.pipe';
import { SharedModule } from '@shared/shared.module';
import { TemplateModule } from '@shared/template.module';
import { AlertsComponent } from './presentation/alerts.component';


@NgModule({
  declarations: [
    AlertsComponent, AlertDetailComponent
  ],
  imports: [
    CommonModule,
    TemplateModule,
    SharedModule,
    AlertsRoutingModule,
    NotificationModule,
    PartsModule
  ],
  providers: [
    AlertsFacade,
    AlertsState,
    AlertDetailFacade,
    AlertDetailState,
    AlertHelperService,
    FormatPartSemanticDataModelToCamelCasePipe,
    FormatPaginationSemanticDataModelToCamelCasePipe,
    FormatPartlistSemanticDataModelToCamelCasePipe,
    ...getI18nPageProvider('page.alert'),
  ]
})
export class AlertsModule { }
