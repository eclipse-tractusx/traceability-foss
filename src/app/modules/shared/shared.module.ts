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

import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { TableComponent } from '@shared/components/table/table.component';
import { AvatarComponent } from './components/avatar/avatar.component';
import { BreadcrumbsComponent } from './components/breadcrumbs/breadcrumbs.component';
import { ButtonComponent } from './components/button/button.component';
import { ConfirmDialogComponent } from './components/confirm-dialog/confirm-dialog.component';
import { HeaderComponent } from './components/header/header.component';
import { NotificationContainerComponent } from './components/notifications/notification-container/notification-container.component';
import { NotificationMessageComponent } from './components/notifications/notification-message/notification-message.component';
import { QualityAlertEmptyStateComponent } from './components/quality-alert-empty-state/quality-alert-empty-state.component';
import { TabsModule } from './components/tabs/tabs.module';
import { StepActionsComponent } from './components/wizard/step-actions.component';
import { StepBodyComponent } from './components/wizard/step-body.component';
import { StepsComponent } from './components/wizard/steps/steps.component';
import { WizardComponent } from './components/wizard/wizard.component';
import { SharedService } from './service/shared.service';
import { ClickOutsideDirective } from './directives/click-outside.directive';
import { RoleDirective } from './directives/role.directive';
import { TooltipDirective } from './directives/tooltip.directive';
import { ViewContainerDirective } from './directives/view-container.directive';
import { AssetDatePipe } from './pipes/asset-date.pipe';
import { DateSplitPipe } from './pipes/date-split.pipe';
import { FirstLetterUpperPipe } from './pipes/first-letter-upper.pipe';
import { ShortenPipe } from './pipes/shorten.pipe';
import { MspidsResolver } from './resolver/mspids.resolver';
import { OrganizationsResolver } from './resolver/organizations.resolver';
import { TemplateModule } from './template.module';

@NgModule({
  declarations: [
    ConfirmDialogComponent,
    NotificationContainerComponent,
    NotificationMessageComponent,
    BreadcrumbsComponent,
    HeaderComponent,
    ButtonComponent,
    TableComponent,
    TooltipDirective,
    RoleDirective,
    ShortenPipe,
    DateSplitPipe,
    AssetDatePipe,
    FirstLetterUpperPipe,
    ViewContainerDirective,
    ClickOutsideDirective,
    AvatarComponent,
    WizardComponent,
    StepsComponent,
    StepBodyComponent,
    StepActionsComponent,
    QualityAlertEmptyStateComponent,
  ],
  imports: [TemplateModule, TabsModule, RouterModule],
  exports: [
    ConfirmDialogComponent,
    NotificationContainerComponent,
    NotificationMessageComponent,
    BreadcrumbsComponent,
    HeaderComponent,
    ButtonComponent,
    TableComponent,
    TooltipDirective,
    RoleDirective,
    TabsModule,
    ShortenPipe,
    DateSplitPipe,
    AssetDatePipe,
    FirstLetterUpperPipe,
    ViewContainerDirective,
    ClickOutsideDirective,
    AvatarComponent,
    WizardComponent,
    StepsComponent,
    StepBodyComponent,
    StepActionsComponent,
    QualityAlertEmptyStateComponent,
  ],
  providers: [SharedService, MspidsResolver, OrganizationsResolver],
})
export class SharedModule {}
