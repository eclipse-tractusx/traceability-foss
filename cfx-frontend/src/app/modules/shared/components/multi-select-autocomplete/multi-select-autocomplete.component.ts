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

import { DatePipe, registerLocaleData } from '@angular/common';
import localeDe from '@angular/common/locales/de';
import localeDeExtra from '@angular/common/locales/extra/de';
import {
  Component,
  EventEmitter,
  Inject,
  Injector,
  Input,
  LOCALE_ID,
  OnChanges,
  Output,
  ViewChild,
} from '@angular/core';
import { FormControl } from '@angular/forms';
import { DateAdapter, MAT_DATE_LOCALE } from '@angular/material/core';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';
import {
  AutocompleteStrategy,
  AutocompleteStrategyMap,
} from '@shared/components/multi-select-autocomplete/autocomplete-strategy';
import { TableType } from '@shared/components/multi-select-autocomplete/table-type.model';
import { FormatPartSemanticDataModelToCamelCasePipe } from '@shared/pipes/format-part-semantic-data-model-to-camelcase.pipe';
import { PartsService } from '@shared/service/parts.service';
import { firstValueFrom } from 'rxjs';
import { ToastService } from '../toasts/toast.service';

@Component({
  selector: 'app-multiselect',
  templateUrl: 'multi-select-autocomplete.component.html',
  styleUrls: ['multi-select-autocomplete.component.scss'],
})

export class MultiSelectAutocompleteComponent implements OnChanges {

  @Input()
  placeholder: string;
  @Input()
  options: any;
  allOptions: any;
  searchedOptions: any;
  optionsSelected: any = [];
  @Input()
  disabled = false;
  @Input()
  display = 'display';
  @Input()
  value = 'value';
  @Input()
  formControl = new FormControl();
  @Input()
  placeholderMultiple;
  @Input()
  singleSearch = false;

  @Input()
  selectedOptions;

  @Input()
  isDate = false;

  // New Options
  @Input()
  labelCount = 1;
  @Input()
  appearance = 'standard';

  @Input()
  filterColumn = null;

  @Input()
  tableType: TableType = TableType.AS_BUILT_OWN;

  @Output() onFilterValueChanged = new EventEmitter<any>();

  strategy: AutocompleteStrategy;

  public readonly minDate = new Date();
  public optionClasses = {};

  @ViewChild('searchInput', { static: true }) searchInput: any;

  searchElement = '';

  searchElementChange: EventEmitter<any> = new EventEmitter();

  @ViewChild('selectElem', { static: true }) selectElem: any;

  selectedValue: Array<any> = [];
  selectAllChecked = false;

  startDate: Date;
  endDate: Date;

  delayTimeoutId: any;

  suggestionError = false;

  isLoadingSuggestions: boolean;

  filterName = 'filterLabel';
  iconSource = './assets/images/icons/calendar.svg';
  activeIconSource = './assets/images/icons/calendar_active.svg';
  isOpen = false;

  private cleared = false;

  constructor(public datePipe: DatePipe, public _adapter: DateAdapter<any>,
    @Inject(MAT_DATE_LOCALE) public _locale: string, @Inject(LOCALE_ID) private locale: string, public partsService: PartsService,
    private readonly formatPartSemanticDataModelToCamelCasePipe: FormatPartSemanticDataModelToCamelCasePipe,
    private injector: Injector,
    private toastService: ToastService) {
    registerLocaleData(localeDe, 'de', localeDeExtra);
    this._adapter.setLocale(locale === 'en' ? 'en-GB' : 'de-DE',);
  }

  ngOnInit(): void {
    this.strategy = this.injector.get<AutocompleteStrategy>(AutocompleteStrategyMap.get(this.tableType));
    this.searchElementChange.subscribe((value) => {
      if (this.delayTimeoutId) {
        clearTimeout(this.delayTimeoutId);
        this.delayTimeoutId = null;
        this.filterItem(value);
      }
    });

    if (this.isDate) {
      this.filterName = 'filterLabelDate';
    } else if (this.filterColumn === 'semanticDataModel' || this.filterColumn === 'status' || this.filterColumn === 'severity') {
      this.filterName = 'filterLabelSelect';
      this.setColumnClass();
      this.allOptions = [...this.options];
    }
  }

  ngOnChanges(): void {
    this.selectedValue = this.formControl.value;
  }

  removeFocus() {
    (document.activeElement as HTMLElement).blur();
  }

  toggleSelectAll(selectCheckbox: any): void {
    if (selectCheckbox.checked) {
      // if there are no suggestion but the selectAll checkbox was checked
      if (!this.searchedOptions.length) {
        this.selectedValue = this.searchElement as unknown as [];
      } else {
        this.searchedOptions.forEach(option => {
          if (!this.selectedValue.includes(option[this.value])) {
            this.selectedValue = this.selectedValue.concat([option[this.value]]);
          }
        });
      }
      this.updateOptionsAndSelections();

    } else {
      this.selectedValue = [];
      this.updateOptionsAndSelections();
    }
    this.onFilterValueChanged.emit({ filterKey: this.filterColumn, values: this.selectedValue });
  };

  changeSearchTextOptionSingleSearch() {
    this.formControl.patchValue(this.selectedValue);
  }

  shouldHideTextSearchOptionFieldSingleSearch() {
    return this.searchElement === null || this.searchElement === undefined || this.searchElement === '';
  }

  displayValue(): string[] {
    let suffix = '';
    let displayValue;
    // add +X others label if multiple
    if (this.selectedValue?.length > 1) {
      suffix = (' + ' + (this.selectedValue?.length - 1)) + ' ' + this.placeholderMultiple;
    }

    // apply CamelCase to semanticDataModel labels
    if (this.filterColumn === 'semanticDataModel') {
      displayValue = [this.formatPartSemanticDataModelToCamelCasePipe.transformModel(this.selectedValue[0]), suffix];
    } else {
      displayValue = [this.selectedValue[0], suffix];
    }

    // if no value selected, return empty string
    if (!this.selectedValue.length) {
      displayValue = [''];
    }

    return displayValue;
  }

  filterItem(value: any): void {
    if (!this.value.length || !value || value === '') {
      this.clickClear();
      return;
    }

    if (this.singleSearch) {
      return;
    }

    // emit an event that the searchElement changed
    // if there is a timeout currently, abort the changes.
    if (this.delayTimeoutId) {
      this.searchElementChange.emit(value);
      return;
    }

    // if there is no timeout currently, start the delay
    const timeoutCallback = async (): Promise<void> => {
      if (this.cleared) {
        this.cleared = false;
        return;
      }

      this.isLoadingSuggestions = true;
      try {
        firstValueFrom(this.strategy.retrieveSuggestionValues(this.tableType, this.filterColumn, this.searchElement)).then((res: any) => {
          if (this.filterColumn === 'semanticDataModel') {
            this.searchedOptions = res.filter(option => !this.selectedValue.includes(option))
              .map(option => ({
                display: this.formatPartSemanticDataModelToCamelCasePipe.transformModel(option),
                value: option,
              }));
            this.options = this.searchedOptions;
            this.allOptions = res.map(option => ({
              display: this.formatPartSemanticDataModelToCamelCasePipe.transformModel(option),
              value: option,
            }));

          } else {
            // add filter for not selected
            this.searchedOptions = res.filter(option => !this.selectedValue.includes(option))
              .map(option => ({ display: option, value: option }));
            this.options = this.searchedOptions;
            this.allOptions = res.map(option => ({ display: option, value: option }));
            this.handleAllSelectedCheckbox();
          }

          this.suggestionError = !this.searchedOptions?.length;
        }).catch((error) => {
          console.error('Error fetching data: ', error);
          this.suggestionError = !this.searchedOptions.length;
        }).finally(() => {
          this.delayTimeoutId = null;
          this.isLoadingSuggestions = false;
        });
      } catch (error) {
        console.error('Error in timeoutCallback: ', error);
      }
    };

    // Start the delay with the callback
    this.delayTimeoutId = setTimeout(() => {
      Promise.resolve().then(() => timeoutCallback());
    }, 500);
  }

  startDateSelected(event: MatDatepickerInputEvent<Date>) {
    this.startDate = event.value;
    this.searchElement = this.datePipe.transform(this.startDate, 'yyyy-MM-dd');
  }

  endDateSelected(event: MatDatepickerInputEvent<Date>) {
    this.endDate = event.value;
    if (!this.endDate) {
      return;
    }
    this.searchElement += ',' + this.datePipe.transform(this.endDate, 'yyyy-MM-dd');
  }

  onFilterChanged(isOpen: boolean) {
    this.isOpen = isOpen;

    setTimeout(() => {
      this.removeFocus();
    }, 500);
  }

  clickClear(): void {
    this.selectAllChecked = false;

    this.searchElement = '';

    this.startDate = null;
    this.endDate = null;
    this.searchedOptions = [];

    if (!this.singleSearch && this.allOptions?.length > 0 && this.filterName !== 'filterLabel') {
      this.options?.forEach(option => {
        option.checked = false;
      });

      this.options = [...this.allOptions];
    } else {
      this.options = [];
      this.allOptions = [];
    }

    this.optionsSelected = [];

    this.selectedValue = [];
    this.suggestionError = false;
    this.cleared = true;

    this.onFilterValueChanged.emit({ filterKey: this.filterColumn, values: [] });
  }

  private updateOptionsAndSelections() {
    if (this.singleSearch) {
      return;
    }
    if (!this.allOptions) {
      this.allOptions = [];
    }
    this.options = this.allOptions.filter(option => !this.selectedValue.includes(option.value));
    if (!this.selectedValue) {
      this.options = this.allOptions;
    }

    if (!this.searchedOptions) {
      this.searchedOptions = [];
    }
    const filter = this.searchedOptions.filter(val => this.selectedValue.includes(val));
    for (const selected of this.selectedValue) {
      filter.push({ display: selected, value: selected });
    }
    this.optionsSelected = filter;
    this.handleAllSelectedCheckbox();
  }

  dateFilter() {
    this.onFilterValueChanged.emit({ filterKey: this.filterColumn, values: this.searchElement });
  }

  onSelectValue(selected: boolean, value: any) {
    if (!selected) {
      this.optionsSelected.splice(this.optionsSelected.indexOf(value), 1);
      this.options.push(value);
    } else {
      this.options.splice(this.options.indexOf(value), 1);
      this.optionsSelected.push(value);
    }

    this.selectedValue = [];

    this.optionsSelected.forEach(option => {
      if (!this.selectedValue.includes(option.value)) {
        this.selectedValue = this.selectedValue.concat(option.value);
      }
    });

    this.onFilterValueChanged.emit({ filterKey: this.filterColumn, values: this.selectedValue });
  }

  private handleAllSelectedCheckbox() {
    const noSelectedValues = this.selectedValue?.length === 0;
    const oneOptionSelected = this.optionsSelected?.length === 1 && this.allOptions?.length === 0;
    this.selectAllChecked = noSelectedValues ? false : oneOptionSelected || this.allOptions?.length + this.optionsSelected?.length === this.selectedValue?.length;
  }

  filterKeyCommands(event: any) {
    if (event.key === 'Enter' || (event.ctrlKey && event.key === 'a' || event.key === ' ')) {
      event.stopPropagation();
    }
  }

  containsIllegalCharactersForI18nKey(text: string) {
    const allowedCharacters = /^\w+$/;
    return !allowedCharacters.test(text);
  }

  private setColumnClass(): void {
    for (const option of this.options) {
      const optionClass = { 'body-large': true };
      if (option['display'].includes('status')) {
        optionClass['notification-display-status'] = true;
        optionClass[option['displayClass']] = true;
      } else if (option['display'].includes('severity')) {
        optionClass['notification-display-severity'] = true;
      }
      this.optionClasses[option['display']] = optionClass;
    }
  }

  public someSelected(): boolean {
    return this.options.filter(option => option.checked).length > 0 && !this.selectAllChecked;
  }

  public toggleSelect = function (val: any, isSelectAll: boolean): void {
    // if there is no timeout currently, start the delay
    const timeoutCallback = async (): Promise<void> => {
      try {
        if (isSelectAll) {
          if (val.checked) {
            this.options.forEach(option => {
              option.checked = true;
            });
          } else {
            this.options.forEach(option => (option.checked = false));
          }
        } else if (!isSelectAll && !val.checked && this.selectAllChecked) {
          this.selectAllChecked = false;
        } else if (!isSelectAll && val.checked && !this.selectAllChecked && this.options.filter(option => !option.checked).length === 0) {
          this.selectAllChecked = true;
        }

        this.selectedValue = [];

        this.options.forEach(option => {
          if (option.checked) {
            if (!this.selectedValue.includes(option.value)) {
              this.selectedValue = this.selectedValue.concat(option.value);
            }
          }
        });

        if (this.selectedValue.length === 0) {
          setTimeout(() => {
            this.clickClear();
          }, 200);
        } else {
          this.onFilterValueChanged.emit({ filterKey: this.filterColumn, values: this.selectedValue });
        }

      } catch (error) {
        console.error('Error in timeoutCallback: ', error);
      }
    };

    // Start the delay with the callback
    this.delayTimeoutId = setTimeout(() => {
      Promise.resolve().then(() => timeoutCallback());
    }, 100);
  };
}
