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

import {DatePipe, registerLocaleData} from '@angular/common';
import localeDe from '@angular/common/locales/de';
import localeDeExtra from '@angular/common/locales/extra/de';
import {Component, EventEmitter, Inject, Input, LOCALE_ID, OnChanges, Output, ViewChild} from '@angular/core';
import {FormControl} from '@angular/forms';
import {DateAdapter, MAT_DATE_LOCALE} from '@angular/material/core';
import {MatDatepickerInputEvent} from '@angular/material/datepicker';
import {Owner} from '@page/parts/model/owner.enum';
import {PartTableType} from '@shared/components/table/table.model';
import {PartsService} from '@shared/service/parts.service';

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
  selectedOptions;

  @Input()
  textSearch = true;
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
  isAsBuilt : boolean;

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
  displayString = '';

  startDate: Date;
  endDate: Date;

  delayTimeoutId: any;

  suggestionError: boolean = false;

  isLoadingSuggestions: boolean;

  constructor(public datePipe: DatePipe, public _adapter: DateAdapter<any>,
              @Inject(MAT_DATE_LOCALE) public _locale: string, @Inject(LOCALE_ID) private locale: string, private partsService: PartsService) {
    registerLocaleData(localeDe, 'de', localeDeExtra);
    this._adapter.setLocale(locale);
  }

  shouldHideTextSearchOptionField(): boolean {
    return !this.textSearch || this.textSearch && (this.searchElement === null || this.searchElement === '');
  }

  ngOnInit(): void {
    this.searchElementChange.subscribe((value) => {
      if (this.delayTimeoutId) {
        clearTimeout(this.delayTimeoutId);
        this.delayTimeoutId = null;
        this.filterItem(value);
        console.log('Timeout cleared, function aborted.');
      }
    });
  }

  ngOnChanges(): void {
    /*
    if (this.selectedOptions) {
      this.selectedValue = this.selectedOptions;
      this.formControl.patchValue(this.selectedValue);
    } else if (this.formControl?.value) {*/
      this.selectedValue = this.formControl.value;
      this.formControl.patchValue(this.selectedValue);
    //}
  }

  toggleSelectAll = function(selectCheckbox: any): void {

    if (selectCheckbox.checked) {
      // if there are no suggestion but the selectAll checkbox was checked
      if(!this.filteredOptions.length) {
        console.log(this.searchElement);
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
      const filteredValues = this.getFilteredOptionsValues();
      this.selectedValue = this.selectedValue.filter(
        item => !filteredValues.includes(item),
      );
    }
    this.formControl.patchValue(this.selectedValue);
    this.selectionChange.emit(this.selectedValue);
  };

  filterItem(value: any): void {
    // emit a event that the searchElement changed
    // if there is a timeout currently, abort the changes.
    if (this.delayTimeoutId) {
      this.searchElementChange.emit(value);
      return;
    }
    // if there is no timeout currently, start the delay
    this.delayTimeoutId = setTimeout(async () => {
      this.isLoadingSuggestions = true;

      // when delay ended
      if (this.textSearch) {

        const tableOwner = this.getOwnerOfTable(this.partTableType);

        try {
          const res = await this.partsService.getDistinctFilterValues(this.isAsBuilt, tableOwner, this.filterColumn, this.searchElement).toPromise();
          this.options = res.map(option => ({ display: option, value: option }));
          this.filteredOptions = this.options;
          this.suggestionError = !this.filteredOptions.length;
        } catch (error) {
          console.error('Error fetching data: ', error);
          this.suggestionError = !this.filteredOptions.length;
        }

        this.delayTimeoutId = null;
        this.isLoadingSuggestions = false;
      } else {
        this.filteredOptions = this.options.filter(
          item => item[this.display].toLowerCase().indexOf(value.toLowerCase()) > -1,
        );
        this.selectAllChecked = true;
        this.filteredOptions.forEach(item => {
          if (!this.selectedValue.includes(item[this.value])) {
            this.selectAllChecked = false;
          }
        });
        this.searchElement = '';
      }
      this.delayTimeoutId = null;
      this.isLoadingSuggestions = false;
    }, 500);
  }

  hideOption(option: any): boolean {
    return !(this.filteredOptions.indexOf(option) > -1);
  }

  // Returns plain strings array of filtered values
  getFilteredOptionsValues(): string[] {
    const filteredValues = [];
    this.filteredOptions.forEach(option => {
      filteredValues.push(option.value);
    });
    return filteredValues;
  }

// this seems to be the function that updates the value of a textsearch which would be sent to the request (maybe formControl)
  updateSearchTextOptions(): void {
    this.formControl.patchValue(this.searchElement);
    this.selectedValue = this.searchElement as unknown as [];
  }

  startDateSelected(event: MatDatepickerInputEvent<Date>) {
    this.startDate = event.value;
    this.searchElement = this.datePipe.transform(this.startDate, 'yyyy-MM-dd');
    this.formControl.patchValue(this.searchElement);
    this.selectedValue = this.searchElement as unknown as [];
  }

  endDateSelected(event: MatDatepickerInputEvent<Date>) {
    this.endDate = event.value;
    if (!this.endDate) {
      return;
    }
    this.searchElement += ',' + this.datePipe.transform(this.endDate, 'yyyy-MM-dd');
    this.formControl.patchValue(this.searchElement);
    this.selectedValue = this.searchElement as unknown as [];
  }

  clickClear(): void {
    this.formControl.patchValue('');
    this.formControl.reset();
    if (this.searchInput) {
      this.searchInput.value = '';
    }
    this.searchElement = '';
    this.selectedValue = [];
    this.startDate = null;
    this.endDate = null;
    this.filterItem('');
  }

// TODO Dont understand what this function is good for
  onDisplayString(): string {
    this.displayString = '';
    if (this.textSearch) {
      this.displayString = this.searchElement || 'All';
      return this.displayString;
    }

    if (this.selectedValue?.length) {
        this.handleMultipleSelectDisplay([]);
    }
    return this.displayString;
  }

  private handleMultipleSelectDisplay(displayOption: any) {
    const options = displayOption;
    // Multi select display
    for (let i = 0; i < this.labelCount; i++) {
      options[i] = this.options.filter(
        option => option.value === this.selectedValue[i],
      )[0];
    }
    if (options.length) {
      for (let i = 0; i < options.length; i++) {
        this.displayString += options[i][this.display] + ',';
      }
      this.displayString = this.displayString.slice(0, -1);
      if (this.selectedValue.length === this.options.length) {
        this.displayString = 'All';
      } else if (this.selectedValue.length > 1) {
        this.displayString += ` (+${ this.selectedValue.length - this.labelCount } others)`;
      }
    }
  }

  onSelectionChange(val: any) {

    const filteredValues = this.getFilteredOptionsValues();

    const selectedCount = this.selectedValue.filter(item => filteredValues.includes(item)).length;
    this.selectAllChecked = selectedCount === this.filteredOptions.length;

    this.selectedValue = val.value;
    this.formControl.patchValue(val.value);
    this.selectionChange.emit(this.selectedValue);
    this.searchElement = val.value;
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

}
