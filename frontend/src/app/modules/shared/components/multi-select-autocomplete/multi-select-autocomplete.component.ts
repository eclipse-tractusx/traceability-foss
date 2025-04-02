/********************************************************************************
 * Copyright (c) 2023, 2025 Contributors to the Eclipse Foundation
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
  OnInit,
  ViewChild,
} from '@angular/core';
import { FormControl } from '@angular/forms';
import { DateAdapter, MAT_DATE_LOCALE } from '@angular/material/core';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';
import { MatSelectChange } from '@angular/material/select';
import {
  AutocompleteStrategy,
  AutocompleteStrategyMap,
} from '@shared/components/multi-select-autocomplete/autocomplete-strategy';
import { TableType } from '@shared/components/multi-select-autocomplete/table-type.model';
import { FilterOperator, FilterValue } from '@shared/model/filter.model';
import { FormatPartSemanticDataModelToCamelCasePipe } from '@shared/pipes/format-part-semantic-data-model-to-camelcase.pipe';
import { FilterService } from '@shared/service/filter.service';
import { PartsService } from '@shared/service/parts.service';
import { QuickfilterService } from '@shared/service/quickfilter.service';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-multiselect',
  templateUrl: 'multi-select-autocomplete.component.html',
  styleUrls: [ 'multi-select-autocomplete.component.scss' ],
})

export class MultiSelectAutocompleteComponent implements OnChanges, OnInit {

  @Input()
  placeholder: string;
  @Input()
  options: any;
  allOptions: any;
  searchedOptions: any;
  optionsSelected: any;
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

  strategy: AutocompleteStrategy;

  public readonly minDate = new Date();

  @ViewChild('searchInput', { static: true }) searchInput: any;

  searchElement: string = '';

  @Input()
  inAssetIds: string[] = [];

  searchElementChange: EventEmitter<any> = new EventEmitter();

  @ViewChild('selectElem', { static: true }) selectElem: any;

  selectedValue: Array<any> = [];
  selectAllChecked = false;

  startDate: Date;
  endDate: Date;

  delayTimeoutId: any;

  suggestionError: boolean = false;

  isLoadingSuggestions: boolean;
  @Input() prefilterValue?: string;

  constructor(public datePipe: DatePipe, public _adapter: DateAdapter<any>,
              @Inject(MAT_DATE_LOCALE) public _locale: string, @Inject(LOCALE_ID) private locale: string, public partsService: PartsService,
              private readonly formatPartSemanticDataModelToCamelCasePipe: FormatPartSemanticDataModelToCamelCasePipe,
              private injector: Injector,
              private readonly quickFilterService: QuickfilterService,
              public filterService: FilterService) {
    registerLocaleData(localeDe, 'de', localeDeExtra);
    this._adapter.setLocale(locale);
  }

  ngOnInit(): void {
    this.initStrategy();
    this.subscribeToSearchElementChange();
    this.initializePrefilterValue();
    this.initializeExistingFilter();
    this.subscribeToFilterUpdates();
  }

  ngOnChanges(): void {
    this.selectedValue = this.formControl.value;
    this.formControl.patchValue(this.selectedValue);
  }

  toggleSelectAll(selectCheckbox: any): void {
    if (selectCheckbox.checked) {
      // if there are no suggestion but the selectAll checkbox was checked
      if (!this.searchedOptions.length) {
        this.formControl.patchValue(this.searchElement);
        this.selectedValue = this.searchElement as unknown as [];
      } else {
        this.searchedOptions.forEach(option => {
          if (!this.selectedValue.includes(option[this.value])) {
            this.selectedValue = this.selectedValue.concat([ option[this.value] ]);
          }
        });
      }
      this.updateOptionsAndSelections();

    } else {
      this.selectedValue = [];
      this.updateOptionsAndSelections();
    }
    this.formControl.patchValue(this.selectedValue);
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

    displayValue = [ this.selectedValue[0], suffix ];


    // if no value selected, return empty string
    if (!this.selectedValue.length) {
      displayValue = [ '' ];
    }

    return displayValue;
  }

  filterItem(value: any): void {
    if (!this.searchElement?.length) {
      return;
    }

    if (!value) {
      this.searchedOptions = [];
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
      this.isLoadingSuggestions = true;
      try {
        firstValueFrom(this.strategy.retrieveSuggestionValues(this.tableType, this.filterColumn, this.searchElement, this.inAssetIds)).then((res) => {
          // @ts-ignore
          this.searchedOptions = res.filter(option => !this.selectedValue?.includes(option))
            .map(option => ({ display: option, value: option }));
          this.options = this.searchedOptions;
          // @ts-ignore
          this.allOptions = res.map(option => ({ display: option, value: option }));
          this.handleAllSelectedCheckbox();
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

  clickClear(): void {
    this.selectAllChecked = false;
    this.formControl.patchValue('');
    this.formControl.reset();
    this.searchElement = '';

    this.startDate = null;
    this.endDate = null;
    this.searchedOptions = [];
    this.allOptions = [];
    this.optionsSelected = [];
    this.options = [];
    this.selectedValue = [];
    this.suggestionError = false;

    this.filterService.setFilter(this.tableType, { [this.filterColumn]: this.selectedValue });

    this.updateOptionsAndSelections();
  }

  dateFilter() {

    const dates = Array.isArray(this.searchElement)
      ? this.searchElement
      : this.searchElement?.split(',');
    let filterAttribute: any;
    if (dates.length === 2) {
      filterAttribute = {
        [this.filterColumn]: {
          value:
            [
              {
                value: dates[0],
                strategy: FilterOperator.AFTER_LOCAL_DATE,
              },
              {
                value: dates[1],
                strategy: FilterOperator.BEFORE_LOCAL_DATE,
              },
            ],
          operator: 'AND',
        },
      };
      this.filterService.setFilter(this.tableType, filterAttribute);
    } else {
      filterAttribute = {
        [this.filterColumn]: {
          value: [ {
            value: dates[0],
            strategy: FilterOperator.AT_LOCAL_DATE,
          } ], operator: 'AND',
        },
      };
      this.filterService.setFilter(this.tableType, filterAttribute);
    }
    this.formControl.patchValue([ filterAttribute[this.filterColumn] ]);
  }

  onSelectionChange(matSelectChange: MatSelectChange) {
    this.selectedValue = matSelectChange.value;
    this.formControl.patchValue(matSelectChange.value);
    this.updateOptionsAndSelections();
    let filterValues: FilterValue[] = this.selectedValue.map(value => ({
      value: value,
      strategy: FilterOperator.EQUAL,
    }));
    this.filterService.setFilter(this.tableType, { [this.filterColumn]: { value: filterValues, operator: 'OR' } });
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

  private initStrategy(): void {
    this.strategy = this.injector.get<AutocompleteStrategy>(
      AutocompleteStrategyMap.get(this.tableType),
    );
  }

  private subscribeToSearchElementChange(): void {
    this.searchElementChange.subscribe((value) => {
      if (this.delayTimeoutId) {
        clearTimeout(this.delayTimeoutId);
        this.delayTimeoutId = null;
        this.filterItem(value);
      }
    });
  }

  private initializePrefilterValue(): void {
    if (!this.prefilterValue?.length) {
      return;
    }

    this.searchElement = this.prefilterValue;
    this.selectedValue = [ this.searchElement ];
    this.formControl.patchValue(this.selectedValue);
    this.updateOptionsAndSelections();
  }

  private initializeExistingFilter(): void {
    if (!this.filterService.isFilterSet(this.tableType, this.filterColumn)) {
      return;
    }

    const currFilter = this.filterService.getFilter(this.tableType);

    if (!this.isDate) {
      this.optionsSelected = (currFilter[this.filterColumn]?.['value'] || []).map((item: FilterValue) => ({
        display: item.value,
        value: item.value,
      }));
      return;
    }

    this.searchElement = currFilter[this.filterColumn];
    const filterValue: FilterValue[] = this.searchElement?.['value'];
    const [ startStr, endStr ] = filterValue.map(filter => filter.value);
    this.startDate = new Date(startStr);
    this.endDate = new Date(endStr);
  }

  private subscribeToFilterUpdates(): void {
    this.filterService.filterState$.subscribe((state) => {
      const relevant = this.tableType === TableType.AS_BUILT_OWN
        ? state.asBuilt
        : state.asPlanned;

      if (this.isDate) {
        this.handleDateFilterChange(relevant);
      } else {
        this.handleNonDateFilterChange(relevant);
      }
    });
  }

  private handleDateFilterChange(relevant: any): void {
    const filterAttribute = relevant[this.filterColumn];

    if (!filterAttribute?.value) {
      this.startDate = null;
      this.endDate = null;
      return;
    }

    filterAttribute.value.forEach((filterValue: any) => {
      if (filterValue.strategy === FilterOperator.AT_LOCAL_DATE || filterValue.strategy === FilterOperator.AFTER_LOCAL_DATE) {
        this.startDate = new Date(filterValue.value);
      }
      if (filterValue.strategy === FilterOperator.BEFORE_LOCAL_DATE) {
        this.endDate = new Date(filterValue.value);
      }
    });
  }

  private handleNonDateFilterChange(relevant: any): void {

    //if there is a saved filter state, set this as value otherwise use preFilter Value
    if (!Array.isArray(relevant[this.filterColumn]) && relevant[this.filterColumn] !== undefined) {
      const newSelectedValue: FilterValue[] = relevant[this.filterColumn].value ?? [];
      let filterValues: string[] = newSelectedValue.map(filter => filter.value);
      if (this.prefilterValue && filterValues.length === 0) {
        this.selectedValue = [ this.prefilterValue ];
      } else {
        this.selectedValue = filterValues;
      }
      this.formControl.patchValue(filterValues);
      this.updateOptionsAndSelections();
    }
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

  private handleAllSelectedCheckbox() {
    const noSelectedValues = this.selectedValue?.length === 0;
    const oneOptionSelected = this.optionsSelected?.length === 1 && this.allOptions?.length === 0;
    this.selectAllChecked = noSelectedValues ? false : oneOptionSelected || this.allOptions?.length + this.optionsSelected?.length === this.selectedValue?.length;
  }

}
