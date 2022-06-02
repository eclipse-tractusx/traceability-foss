/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { getI18nPageProvider } from '@core/i18n';
import { SharedModule, TemplateModule } from '@shared';
import { DashboardFacade } from './abstraction/dashboard.facade';
import { DashboardService } from './core/dashboard.service';
import { DashboardState } from './core/dashboard.state';
import { DashboardRoutingModule } from './dashboard.routing';
import { AlertDonutChartComponent } from './presentation/alert-donut-chart/alert-donut-chart.component';
import { DashboardComponent } from './presentation/dashboard.component';
import { HistogramChartComponent } from './presentation/histogram-chart/histogram-chart.component';
import { ReceivedAlertEmptyStateComponent } from './presentation/received-alert-empty-state/received-alert-empty-state.component';

@NgModule({
  declarations: [
    DashboardComponent,
    ReceivedAlertEmptyStateComponent,
    AlertDonutChartComponent,
    HistogramChartComponent,
  ],
  imports: [CommonModule, TemplateModule, SharedModule, DashboardRoutingModule],
  providers: [DashboardService, DashboardFacade, DashboardState, ...getI18nPageProvider('page.dashboard')],
})
export class DashboardModule {}
