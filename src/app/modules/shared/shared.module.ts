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
import { I18NextModule } from 'angular-i18next';
import { CardIconComponent } from './components/card-icon/card-icon.component';
import { CardListComponent } from './components/card-list/card-list.component';
import { ToKeyValuePipe } from './components/card-list/card-list.pipe';
import { TableComponent } from './components/table/table.component';
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
import { FormatDatePipe } from './pipes/format-date.pipe';
import { FirstLetterUpperPipe } from './pipes/first-letter-upper.pipe';
import { ShortenPipe } from './pipes/shorten.pipe';
import { OrganizationsResolver } from './resolver/organizations.resolver';
import { TemplateModule } from './template.module';
import { LanguageSelectorComponent } from './components/language-selector/language-selector.component';
import { I18nPipe } from './pipes/i18n.pipe';

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
    I18nPipe,
    FirstLetterUpperPipe,
    FormatDatePipe,
    ViewContainerDirective,
    ClickOutsideDirective,
    AvatarComponent,
    WizardComponent,
    StepsComponent,
    StepBodyComponent,
    StepActionsComponent,
    QualityAlertEmptyStateComponent,
    LanguageSelectorComponent,
    CardIconComponent,
    CardListComponent,
    ToKeyValuePipe,
  ],
  imports: [TemplateModule, TabsModule, RouterModule, I18NextModule],
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
    I18nPipe,
    FormatDatePipe,
    FirstLetterUpperPipe,
    ViewContainerDirective,
    ClickOutsideDirective,
    AvatarComponent,
    WizardComponent,
    StepsComponent,
    StepBodyComponent,
    StepActionsComponent,
    QualityAlertEmptyStateComponent,
    I18NextModule,
    LanguageSelectorComponent,
    CardIconComponent,
    CardListComponent,
  ],
  providers: [SharedService, OrganizationsResolver],
})
export class SharedModule {}
