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
import { Component, EventEmitter, Inject, Input, LOCALE_ID, OnChanges, Output, ViewChild } from '@angular/core';
import { FormControl } from '@angular/forms';
import { DateAdapter, MAT_DATE_LOCALE } from '@angular/material/core';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';
import { Owner } from '@page/parts/model/owner.enum';
import { PartTableType } from '@shared/components/table/table.model';
import { FormatPartSemanticDataModelToCamelCasePipe } from '@shared/pipes/format-part-semantic-data-model-to-camelcase.pipe';
import { PartsService } from '@shared/service/parts.service';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-multiselect',
  templateUrl: 'multi-select-autocomplete.component.html',
  styleUrls: [ 'multi-select-autocomplete.component.scss' ],
})

export class MultiSelectAutocompleteComponent implements OnChanges {

  @Input()
  placeholder: string;
  @Input()
  options: any;
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
  partTableType = PartTableType.AS_BUILT_OWN;

  @Input()
  isAsBuilt: boolean;

  public readonly minDate = new Date();

  @ViewChild('searchInput', { static: true }) searchInput: any;

  searchElement: string = '';

  searchElementChange: EventEmitter<any> = new EventEmitter();

  @Output()
  selectionChange: EventEmitter<any> = new EventEmitter();

  @ViewChild('selectElem', { static: true }) selectElem: any;

  filteredOptions: Array<any> = [];
  selectedValue: Array<any> = [];
  selectAllChecked = false;

  startDate: Date;
  endDate: Date;

  delayTimeoutId: any;

  suggestionError: boolean = false;

  isLoadingSuggestions: boolean;

  constructor(public datePipe: DatePipe, public _adapter: DateAdapter<any>,
              @Inject(MAT_DATE_LOCALE) public _locale: string, @Inject(LOCALE_ID) private locale: string, public partsService: PartsService, private readonly formatPartSemanticDataModelToCamelCasePipe: FormatPartSemanticDataModelToCamelCasePipe) {
    registerLocaleData(localeDe, 'de', localeDeExtra);
    this._adapter.setLocale(locale);
  }

  ngOnInit(): void {
    this.searchElementChange.subscribe((value) => {
      if (this.delayTimeoutId) {
        clearTimeout(this.delayTimeoutId);
        this.delayTimeoutId = null;
        this.filterItem(value);
      }
    });
  }

  ngOnChanges(): void {
    this.selectedValue = this.formControl.value;
    this.formControl.patchValue(this.selectedValue);
  }

  toggleSelectAll = function(selectCheckbox: any): void {

    if (selectCheckbox.checked) {
      // if there are no suggestion but the selectAll checkbox was checked
      if (!this.filteredOptions.length) {
        this.formControl.patchValue(this.searchElement);
        this.selectedValue = this.searchElement as unknown as [];
      } else {
        this.filteredOptions.forEach(option => {
          if (!this.selectedValue.includes(option[this.value])) {
            this.selectedValue = this.selectedValue.concat([ option[this.value] ]);
          }
        });
      }

    } else {
      this.selectedValue = [];
    }
    this.formControl.patchValue(this.selectedValue);
    this.selectionChange.emit(this.selectedValue);
  };

  changeSearchTextOption() {
    this.formControl.patchValue(this.selectedValue);
    this.selectionChange.emit(this.selectedValue);
  }

  shouldHideTextSearchOptionField() {
    return this.searchElement === null || this.searchElement === undefined || this.searchElement === '';
  }

  displayValue() {
    if(!this.searchElement.length) {
      return;
    }
    let suffix = '';
    let displayValue;
    // add +X others label if multiple
    if (this.selectedValue?.length > 1) {
      suffix = (' + ' + (this.selectedValue?.length - 1)) + ' ' + this.placeholderMultiple;
    }

    // apply CamelCase to semanticDataModel labels
    if (this.filterColumn === 'semanticDataModel') {
      displayValue = this.formatPartSemanticDataModelToCamelCasePipe.transformModel(this.selectedValue[0]) + suffix;
    } else {
      displayValue = this.selectedValue[0] + suffix;
    }

    // if no value selected, return empty string
    if(!this.selectedValue.length) {
      displayValue = ''
    }

    return displayValue;
  }

  filterItem(value: any): void {
    if(!this.searchElement.length) {
      return;
    }

    if (!value) {
      this.filteredOptions = [];
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
    const timeoutCallback = (): void => {
      this.isLoadingSuggestions = true;
      const tableOwner = this.getOwnerOfTable(this.partTableType);

      try {
        firstValueFrom(this.partsService.getDistinctFilterValues(
            this.isAsBuilt,
            tableOwner,
            this.filterColumn,
            this.searchElement
        )).then((res) => {
          if (this.filterColumn === 'semanticDataModel') {
            this.options = res.map(option => ({
              display: this.formatPartSemanticDataModelToCamelCasePipe.transformModel(option),
              value: option,
            }));
          } else {
            this.options = res.map(option => ({ display: option, value: option }));
          }

          this.filteredOptions = this.options;
          this.suggestionError = !this.filteredOptions.length;
        }).catch((error) => {
          console.error('Error fetching data: ', error);
          this.suggestionError = !this.filteredOptions.length;
        }).finally(() => {
          this.delayTimeoutId = null;
          this.isLoadingSuggestions = false;
        });
      } catch (error) {
        console.error('Error in timeoutCallback: ', error);
      }
    };

    // Start the delay with the callback
    this.delayTimeoutId = setTimeout(timeoutCallback, 500);
  }


  isUnsupportedAutoCompleteField(fieldName: string) {
    return fieldName === 'activeAlerts' || fieldName === 'activeInvestigations';
  }

  hideOption(option: any): boolean {
    if(!this.searchElement.length) {
      return true;
    }
    return !(this.filteredOptions.indexOf(option) > -1);
  }

  // Returns plain strings array of filtered values
  getFilteredOptionsValues(): string[] {
    const filteredValues = [];
    this.filteredOptions.forEach(option => {
      if(option.length) {
          filteredValues.push(option.value);
      }
    });
    return filteredValues;
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
    this.formControl.patchValue('');
    this.formControl.reset();
    this.searchElement = '';
    this.selectedValue = [];
    this.startDate = null;
    this.endDate = null;
    this.filteredOptions = [];
  }

  dateFilter(){
    this.formControl.patchValue(this.searchElement);
  }


  onSelectionChange(val: any) {
    if(!this.searchElement.length) {
      return;
    }
    const filteredValues = this.getFilteredOptionsValues();

    const selectedCount = this.selectedValue.filter(item => filteredValues.includes(item)).length;
    this.selectAllChecked = selectedCount === this.filteredOptions.length;

    this.selectedValue = val.value;
    this.formControl.patchValue(val.value);
    this.selectionChange.emit(this.selectedValue);
    this.searchElement = this.displayValue();
  }

  getOwnerOfTable(partTableType: PartTableType): Owner {
    if (partTableType === PartTableType.AS_BUILT_OWN || partTableType === PartTableType.AS_PLANNED_OWN) {
      return Owner.OWN;
    } else if (partTableType === PartTableType.AS_BUILT_CUSTOMER || partTableType === PartTableType.AS_PLANNED_CUSTOMER) {
      return Owner.CUSTOMER;
    } else if (partTableType === PartTableType.AS_BUILT_SUPPLIER || partTableType === PartTableType.AS_PLANNED_SUPPLIER) {
      return Owner.SUPPLIER;
    } else {
      return Owner.UNKNOWN;
    }
  }

    filterKeyCommands(event: any) {
    if (event.key === 'Enter' || (event.ctrlKey && event.key === 'a' || event.key === ' ')) {
          event.stopPropagation();
        }
    }
}
