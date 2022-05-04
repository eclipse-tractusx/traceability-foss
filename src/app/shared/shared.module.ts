/*
 * Copyright 2021 The PartChain Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { NgModule } from '@angular/core';
import { TableComponent } from './components/table/table.component';
import { RowDetailDirective } from './components/table/row.detail.directive';
import { ChildTableComponent } from './components/table/child-table/child-table.component';
import { ConfirmDialogComponent } from './components/confirm-dialog/confirm-dialog.component';
import { NotificationContainerComponent } from './components/notifications/notification-container/notification-container.component';
import { NotificationMessageComponent } from './components/notifications/notification-message/notification-message.component';
import { TemplateModule } from './template.module';
import { SharedService } from './core/shared.service';
import { BreadcrumbsComponent } from './components/breadcrumbs/breadcrumbs.component';
import { HeaderComponent } from './components/header/header.component';
import { ButtonComponent } from './components/button/button.component';
import { MenuComponent } from './components/menu/menu.component';
import { TooltipDirective } from './directives/tooltip.directive';
import { SvgIconsModule } from '@ngneat/svg-icon';
import { icons } from './shared-icons.module';
import { RoleDirective } from './directives/role.directive';
import { TabsModule } from './components/tabs/tabs.module';
import { DestroyService } from './core/destroy.service';
import { MenuItemComponent } from './components/menu/menu-item/menu-item.component';
import { ShortenPipe } from './pipes/shorten.pipe';
import { ViewContainerDirective } from './directives/view-container.directive';
import { AssetFacade } from './abstraction/asset-facade';
import { AssetState } from './core/asset.state';
import { AssetService } from './core/asset.service';
import { AvatarComponent } from './components/avatar/avatar.component';
import { ClickOutsideDirective } from './directives/click-outside.directive';
import { StepBodyComponent } from './components/wizard/step-body.component';
import { StepsComponent } from './components/wizard/steps/steps.component';
import { WizardComponent } from './components/wizard/wizard.component';
import { StepActionsComponent } from './components/wizard/step-actions.component';
import { FirstLetterUpperPipe } from './pipes/first-letter-upper.pipe';
import { MspidsResolver } from './resolver/mspids.resolver';
import { OrganizationsResolver } from './resolver/organizations.resolver';
import { QualityAlertEmptyStateComponent } from './components/quality-alert-empty-state/quality-alert-empty-state.component';
import { DateSplitPipe } from './pipes/date-split.pipe';
import { AssetDatePipe } from './pipes/asset-date.pipe';
import { TableFacade } from './components/table/table.facade';
import { TableState } from './components/table/table.state';

/**
 *
 *
 * @export
 * @class SharedModule
 */
@NgModule({
  declarations: [
    ConfirmDialogComponent,
    NotificationContainerComponent,
    NotificationMessageComponent,
    TableComponent,
    RowDetailDirective,
    ChildTableComponent,
    BreadcrumbsComponent,
    HeaderComponent,
    ButtonComponent,
    MenuComponent,
    MenuItemComponent,
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
  imports: [TemplateModule, TabsModule, SvgIconsModule.forChild(icons)],
  exports: [
    ConfirmDialogComponent,
    NotificationContainerComponent,
    NotificationMessageComponent,
    TableComponent,
    RowDetailDirective,
    ChildTableComponent,
    BreadcrumbsComponent,
    HeaderComponent,
    ButtonComponent,
    MenuComponent,
    MenuItemComponent,
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
  providers: [
    SharedService,
    DestroyService,
    AssetFacade,
    AssetState,
    AssetService,
    MspidsResolver,
    OrganizationsResolver,
    TableFacade,
    TableState,
  ],
  entryComponents: [ConfirmDialogComponent],
})
export class SharedModule {}
