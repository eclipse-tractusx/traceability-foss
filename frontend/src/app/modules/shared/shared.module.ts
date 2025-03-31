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

import { DatePipe, TitleCasePipe } from '@angular/common';
import { NgModule } from '@angular/core';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorIntl } from '@angular/material/paginator';
import { RouterModule } from '@angular/router';
import { AdvancedTableFilterComponent } from '@shared/components/advanced-table-filter/advanced-table-filter.component';
import { AssetPublisherComponent } from '@shared/components/asset-publisher/asset-publisher.component';
import { AutocompleteChipInputComponent } from '@shared/components/autocomplete-chip-input/autocomplete-chip-input.component';
import { AutocompleteInputComponent } from '@shared/components/autocomplete-input/autocomplete-input.component';
import { CardMetricComponent } from '@shared/components/card-metric/card-metric.component';
import { ChipComponent } from '@shared/components/chip/chip.component';
import { ContractsQuickFilterComponent } from '@shared/components/contracts-quick-filter/contracts-quick-filter.component';
import { CountryFlagGeneratorComponent } from '@shared/components/country-flag-generator/country-flag-generator.component';
import { CsvUploadComponent } from '@shared/components/csv-upload/csv-upload.component';
import { DatepickerInputComponent } from '@shared/components/datepicker-input/datepicker-input.component';
import { DateTimeComponent } from '@shared/components/dateTime/dateTime.component';
import { FormErrorMessageComponent } from '@shared/components/formErrorMessage/formErrorMessage.component';
import { InputComponent } from '@shared/components/input/input.component';
import { MultiSelectAutocompleteComponent } from '@shared/components/multi-select-autocomplete/multi-select-autocomplete.component';
import { NotificationOverviewComponent } from '@shared/components/notification-overview/notification-overview.component';
import { NotificationReasonComponent } from '@shared/components/notification-reason/notification-reason.component';
import { NotificationTypeComponent } from '@shared/components/notification-type/notification-type.component';
import { PartsTableComponent } from '@shared/components/parts-table/parts-table.component';
import { RequestNotificationNewComponent } from '@shared/components/request-notification-new';
import { SeveritySelectComponent } from '@shared/components/severity-select/severity-select.component';
import { SeverityComponent } from '@shared/components/severity/severity.component';
import { TableSettingsComponent } from '@shared/components/table-settings/table-settings.component';
import { TextWithIconComponent } from '@shared/components/text-with-icon/text-with-icon.component';
import { TypeSelectComponent } from '@shared/components/type-select/type-select.component';
import { ViewSelectorComponent } from '@shared/components/view-selector/view-selector.component';
import { NotificationModalContentComponent } from '@shared/modules/notification/modal/content/notification-modal-content.component';
import { AbbreviateNumberPipe } from '@shared/pipes/abbreviate-number.pipe';
import { FlattenObjectPipe } from '@shared/pipes/flatten-object.pipe';
import { FormatPaginationSemanticDataModelToCamelCasePipe } from '@shared/pipes/format-pagination-semantic-data-model-to-camelcase.pipe';
import { FormatPartSemanticDataModelToCamelCasePipe } from '@shared/pipes/format-part-semantic-data-model-to-camelcase.pipe';
import { FormatPartlistSemanticDataModelToCamelCasePipe } from '@shared/pipes/format-partlist-semantic-data-model-to-camelcase.pipe';
import { I18NextModule } from 'angular-i18next';
import { NgxFileDropModule } from 'ngx-file-drop';
import { BaseInputComponent } from './abstraction/baseInput/baseInput.component';
import { AvatarComponent } from './components/avatar/avatar.component';
import { ButtonComponent } from './components/button/button.component';
import { CardIconComponent } from './components/card-icon/card-icon.component';
import { CardListComponent } from './components/card-list/card-list.component';
import { ToKeyValuePipe } from './components/card-list/card-list.pipe';
import { DataLoadingErrorComponent } from './components/data-loading-error/data-loading-error.component';
import { LanguageSelectorComponent } from './components/language-selector/language-selector.component';
import { PaginatorIntlService } from './components/pagination/paginator-intl.service';
import { QualityTypeComponent } from './components/quality-type/quality-type.component';
import { SelectComponent } from './components/select/select.component';
import { ValueToLablePipe } from './components/select/valueToLable.pipe';
import { SidenavWrapperComponent } from './components/sidenav/sidenav-wrapper.component';
import { TableComponent } from './components/table/table.component';
import { TextareaComponent } from './components/textarea/textarea.component';
import { ToastContainerComponent } from './components/toasts/toast-container/toast-container.component';
import { ToastMessageComponent } from './components/toasts/toast-message/toast-message.component';
import { RoleDirective } from './directives/role.directive';
import { TabAsPanelDirective } from './directives/tabs/tab-as-panel.directive';
import { TooltipDirective } from './directives/tooltip.directive';
import { ViewContainerDirective } from './directives/view-container.directive';
import { AutoFormatPipe } from './pipes/auto-format.pipe';
import { ErrorMessagePipe } from './pipes/error-message.pipe';
import { FormatDatePipe } from './pipes/format-date.pipe';
import { I18nPipe } from './pipes/i18n.pipe';
import { PartsService } from './service/parts.service';
import { StaticIdService } from './service/staticId.service';
import { TemplateModule } from './template.module';
import { BreadcrumbComponent } from './components/breadcrumb/breadcrumb.component';
@NgModule({
  declarations: [
    ToastContainerComponent,
    PartsTableComponent,
    ToastMessageComponent,
    ButtonComponent,
    TextWithIconComponent,
    TableComponent,
    TooltipDirective,
    NotificationTypeComponent,
    RoleDirective,
    I18nPipe,
    AutoFormatPipe,
    FormatDatePipe,
    FormatPaginationSemanticDataModelToCamelCasePipe,
    FormatPartSemanticDataModelToCamelCasePipe,
    FormatPartlistSemanticDataModelToCamelCasePipe,
    FlattenObjectPipe,
    ViewContainerDirective,
    AvatarComponent,
    LanguageSelectorComponent,
    CardIconComponent,
    CardListComponent,
    ToKeyValuePipe,
    SelectComponent,
    DataLoadingErrorComponent,
    TabAsPanelDirective,
    SidenavWrapperComponent,
    TextareaComponent,
    ErrorMessagePipe,
    RequestNotificationNewComponent,
    QualityTypeComponent,
    ValueToLablePipe,
    NotificationOverviewComponent,
    NotificationReasonComponent,
    NotificationModalContentComponent,
    DateTimeComponent,
    BaseInputComponent,
    FormErrorMessageComponent,
    SeverityComponent,
    SeveritySelectComponent,
    TypeSelectComponent,
    InputComponent,
    ContractsQuickFilterComponent,
    ViewSelectorComponent,
    MultiSelectAutocompleteComponent,
    CountryFlagGeneratorComponent,
    TableSettingsComponent,
    AbbreviateNumberPipe,
    CardMetricComponent,
    AssetPublisherComponent,
    ChipComponent,
    AdvancedTableFilterComponent,
    AutocompleteChipInputComponent,
    DatepickerInputComponent,
    AutocompleteInputComponent,
    CsvUploadComponent,
    BreadcrumbComponent,
  ],
  imports: [ TemplateModule, RouterModule, I18NextModule, MatAutocompleteModule, NgxFileDropModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule ],
  exports: [
    ToastContainerComponent,
    ToastMessageComponent,
    ButtonComponent,
    TextWithIconComponent,
    TableComponent,
    TooltipDirective,
    RoleDirective,
    I18nPipe,
    FormatDatePipe,
    AutoFormatPipe,
    ViewContainerDirective,
    AvatarComponent,
    NotificationTypeComponent,
    I18NextModule,
    LanguageSelectorComponent,
    CardIconComponent,
    CardListComponent,
    ToKeyValuePipe,
    SelectComponent,
    DataLoadingErrorComponent,
    TabAsPanelDirective,
    SidenavWrapperComponent,
    TextareaComponent,
    ErrorMessagePipe,
    RequestNotificationNewComponent,
    QualityTypeComponent,
    NotificationOverviewComponent,
    NotificationReasonComponent,
    NotificationModalContentComponent,
    DateTimeComponent,
    BaseInputComponent,
    SeverityComponent,
    SeveritySelectComponent,
    TypeSelectComponent,
    InputComponent,
    FormatPaginationSemanticDataModelToCamelCasePipe,
    FlattenObjectPipe,
    FormatPartSemanticDataModelToCamelCasePipe,
    FormatPartlistSemanticDataModelToCamelCasePipe,
    ContractsQuickFilterComponent,
    ViewSelectorComponent,
    PartsTableComponent,
    MultiSelectAutocompleteComponent,
    CountryFlagGeneratorComponent,
    AbbreviateNumberPipe,
    CardMetricComponent,
    AssetPublisherComponent,
    ChipComponent,
    AdvancedTableFilterComponent,
    AutocompleteChipInputComponent,
    DatepickerInputComponent,
    AutocompleteInputComponent,
    CsvUploadComponent,
    BreadcrumbComponent,
  ],
  providers: [
    FormatDatePipe,
    StaticIdService,
    PartsService,
    ErrorMessagePipe,
    TitleCasePipe,
    DatePipe,
    {
      provide: MatPaginatorIntl,
      useClass: PaginatorIntlService,
    },
  ],
})
export class SharedModule {
}
