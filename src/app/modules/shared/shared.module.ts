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
import { RequestInvestigationComponent } from '@shared/components/request-investigation/requestInvestigation.component';
import { SidenavComponent } from '@shared/components/sidenav/sidenav.component';
import { TextareaComponent } from '@shared/components/textarea/textarea.component';
import { ErrorMessagePipe } from '@shared/pipes/error-message.pipe';
import { I18NextModule } from 'angular-i18next';
import { AvatarComponent } from './components/avatar/avatar.component';
import { BreadcrumbsComponent } from './components/breadcrumbs/breadcrumbs.component';
import { ButtonComponent } from './components/button/button.component';
import { CardIconComponent } from './components/card-icon/card-icon.component';
import { CardListComponent } from './components/card-list/card-list.component';
import { ToKeyValuePipe } from './components/card-list/card-list.pipe';
import { ConfirmDialogComponent } from './components/confirm-dialog/confirm-dialog.component';
import { DataLoadingErrorComponent } from './components/data-loading-error/data-loading-error.component';
import { HeaderComponent } from './components/header/header.component';
import { LanguageSelectorComponent } from './components/language-selector/language-selector.component';
import { NotificationContainerComponent } from './components/notifications/notification-container/notification-container.component';
import { NotificationMessageComponent } from './components/notifications/notification-message/notification-message.component';
import { QualityAlertEmptyStateComponent } from './components/quality-alert-empty-state/quality-alert-empty-state.component';
import { SelectComponent } from './components/select/select.component';
import { TableComponent } from './components/table/table.component';
import { TabAsPanelDirective } from './components/tabs/tab-as-panel.directive';
import { StepActionsComponent } from './components/wizard/step-actions.component';
import { StepBodyComponent } from './components/wizard/step-body.component';
import { StepsComponent } from './components/wizard/steps/steps.component';
import { WizardComponent } from './components/wizard/wizard.component';
import { RoleDirective } from './directives/role.directive';
import { TooltipDirective } from './directives/tooltip.directive';
import { ViewContainerDirective } from './directives/view-container.directive';
import { AutoFormatPipe } from './pipes/auto-format.pipe';
import { FirstLetterUpperPipe } from './pipes/first-letter-upper.pipe';
import { FormatDatePipe } from './pipes/format-date.pipe';
import { I18nPipe } from './pipes/i18n.pipe';
import { ShortenPipe } from './pipes/shorten.pipe';
import { PartsService } from './service/parts.service';
import { StaticIdService } from './service/staticId.service';
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
    I18nPipe,
    FirstLetterUpperPipe,
    AutoFormatPipe,
    FormatDatePipe,
    ViewContainerDirective,
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
    SelectComponent,
    DataLoadingErrorComponent,
    TabAsPanelDirective,
    SidenavComponent,
    TextareaComponent,
    ErrorMessagePipe,
    RequestInvestigationComponent,
  ],
  imports: [TemplateModule, RouterModule, I18NextModule],
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
    ShortenPipe,
    I18nPipe,
    FormatDatePipe,
    AutoFormatPipe,
    FirstLetterUpperPipe,
    ViewContainerDirective,
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
    ToKeyValuePipe,
    SelectComponent,
    DataLoadingErrorComponent,
    TabAsPanelDirective,
    SidenavComponent,
    TextareaComponent,
    ErrorMessagePipe,
    RequestInvestigationComponent,
  ],
  providers: [FormatDatePipe, StaticIdService, PartsService, ErrorMessagePipe],
})
export class SharedModule {}
