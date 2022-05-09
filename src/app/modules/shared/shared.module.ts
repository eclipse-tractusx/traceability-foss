import { NgModule } from '@angular/core';
import { SvgIconsModule } from '@ngneat/svg-icon';
import { AvatarComponent } from './components/avatar/avatar.component';
import { BreadcrumbsComponent } from './components/breadcrumbs/breadcrumbs.component';
import { ButtonComponent } from './components/button/button.component';
import { ConfirmDialogComponent } from './components/confirm-dialog/confirm-dialog.component';
import { HeaderComponent } from './components/header/header.component';
import { MenuItemComponent } from './components/menu/menu-item/menu-item.component';
import { MenuComponent } from './components/menu/menu.component';
import { NotificationContainerComponent } from './components/notifications/notification-container/notification-container.component';
import { NotificationMessageComponent } from './components/notifications/notification-message/notification-message.component';
import { QualityAlertEmptyStateComponent } from './components/quality-alert-empty-state/quality-alert-empty-state.component';
import { ChildTableComponent } from './components/table/child-table/child-table.component';
import { RowDetailDirective } from './components/table/row.detail.directive';
import { TableComponent } from './components/table/table.component';
import { TableFacade } from './components/table/table.facade';
import { TableState } from './components/table/table.state';
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
import { icons } from './shared-icons.module';
import { TemplateModule } from './template.module';

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
  providers: [SharedService, MspidsResolver, OrganizationsResolver, TableFacade, TableState],
})
export class SharedModule {}
