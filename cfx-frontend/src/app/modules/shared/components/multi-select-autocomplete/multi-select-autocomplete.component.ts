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

import {
  Component,
  EventEmitter,
  Inject,
  Input,
  LOCALE_ID,
  OnChanges,
  Output,
  ViewChild,
} from '@angular/core';
import { FormControl } from '@angular/forms';
import { DatePipe, registerLocaleData } from '@angular/common';
import { DateAdapter, MAT_DATE_LOCALE } from '@angular/material/core';
import { MatSelect } from '@angular/material/select';
import { pairwise, startWith } from 'rxjs';
import { CustomDateAdapter } from '@shared/helper/custom-date-adapter';
import { Platform } from '@angular/cdk/platform';
import { MatDatepicker, MatDatepickerInputEvent } from '@angular/material/datepicker';
import localeDe from '@angular/common/locales/de';
import localeDeExtra from '@angular/common/locales/extra/de';


@Component({
  selector: 'app-multiselect',
  templateUrl: 'multi-select-autocomplete.component.html',
  styleUrls: ['multi-select-autocomplete.component.scss'],
  providers: [
    { provide: DateAdapter, useClass: CustomDateAdapter, deps: [MAT_DATE_LOCALE, Platform] },
  ],
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
  panelWidth = 'auto';
  @Input()
  maxDate = new Date();

  @Input()
  multiple = false;
  @Input()
  textSearch = true;
  @Input()
  isDate = false;

  // New Options
  @Input()
  labelCount = 1;
  @Input()
  appearance = 'standard';


  @ViewChild('searchInput', { static: true }) searchInput: any;

  @ViewChild('selectElem', { static: true }) selectElem: MatSelect;
  @ViewChild(MatDatepicker) datePicker: MatDatepicker<Date>;

  @Output() triggerFilter = new EventEmitter<void>();

  selectionChange: EventEmitter<any> = new EventEmitter();

  public filterName = 'filterLabel';
  public filteredOptions: Array<any> = [];
  public selectedValue: Array<any> = [];
  public selectAllChecked = false;
  public displayString = '';
  public selectedCheckboxOptions: Array<any> = [];
  public filterActive = '';
  public searched = false;
  private inputTimer;
  public runningTimer = false;
  public optionClasses = {};
  public isSeverity = false;
  public theSearchElement = '';
  public theSearchDate: FormControl<Date> = new FormControl();
  public cleared = true;

  constructor(
    public datePipe: DatePipe,
    public _adapter: DateAdapter<any>,
    @Inject(MAT_DATE_LOCALE) public _locale: string,
    @Inject(LOCALE_ID) private locale: string,
  ) {
    let localeTime = 'en-GB';
    if (locale === 'de') {
      localeTime = 'de-DE';
    }
    registerLocaleData(localeDe, 'de', localeDeExtra);
    this._adapter.setLocale(localeTime);
    this._adapter.getFirstDayOfWeek = () => { return 1; };
  }

  ngOnInit(): void {
    if (this.textSearch) {
      this.formControl.valueChanges.pipe(startWith(0), pairwise()).subscribe(([_prev, next]: [any, any]) => {
        this.theSearchElement = next;
        this.searched = true;
        this.triggerFilteringTimeout('text');
      });
    } else if (this.isDate) {
      this.filterName = 'filterLabelDate';
    } else if (this.multiple) {
      this.filterName = 'filterLabelSelect';
      this.setColumnClass();
    }
  }

  ngOnChanges(): void {
    this.theSearchElement = this.formControl.value;
    this.filteredOptions = this.options;
    if (this.formControl?.value) {
      this.selectedValue = this.formControl.value;
      this.formControl.patchValue(this.selectedValue);
    }
  }

  public triggerFilteringTimeout(searchType: string): void {
    this.runningTimer = true;
    let timeoutTime = 200;
    if (searchType === 'text') {
      timeoutTime = 500;
    } else if (searchType === 'date') {
      timeoutTime = 750;
    }
    clearTimeout(this.inputTimer);
    this.inputTimer = setTimeout(() => {
      this.triggerFiltering(searchType);
    }, timeoutTime);
  }

  public triggerFiltering(searchType: string): void {
    this.runningTimer = false;
    if (searchType === 'text') {
      this.setFilterActive();
    } else if (searchType === 'date') {
      const value = this.datePipe.transform(this.theSearchDate.value, 'yyyy-MM-dd');
      this.formControl.patchValue(value);
      if (this.cleared && value === null) {
        return;
      }
      this.cleared = value === null;
    }
    this.triggerFilter.emit();
  }

  public toggleSelect = function (val: any, isSelectAll: boolean): void {
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
    this.triggerFilteringTimeout('multiple');
  };


  public someSelected(): boolean {
    return this.options.filter(option => option.checked).length > 0 && !this.selectAllChecked;
  }

  public setFilterActive(): void {
    this.filterActive = this.theSearchElement;
    this.cleared = this.theSearchElement === '';
  }

  public dateSelectionEvent(event: MatDatepickerInputEvent<Date>) {
    if (this.runningTimer) {
      clearTimeout(this.inputTimer);
      this.runningTimer = false;
    }
    const value = this.datePipe.transform(event.value, 'yyyy-MM-dd');
    this.formControl.patchValue(value);
    if (this.cleared && value === null) {
      return;
    }
    this.cleared = value === null;
    this.triggerFilter.emit();
  }

  public clickClear(extern = false): boolean {
    let wasSet = false;
    if (this.searchInput) {
      this.searchInput.value = '';
    }
    this.theSearchElement = '';
    this.selectedValue = [];
    wasSet = this.formControl.value !== null && ((Array.isArray(this.formControl.value) && this.formControl.value.length > 0) || (!Array.isArray(this.formControl.value) && this.formControl.value !== ''));
    if (this.isDate) {
      this.formControl.patchValue(null);
      this.theSearchDate.patchValue(null);
    } else if (this.multiple) {
      this.toggleSelect({ checked: false }, true);
      this.selectAllChecked = false;
    } else {
      this.formControl.patchValue('');
      this.filterActive = '';
    }
    clearTimeout(this.inputTimer);
    this.runningTimer = false;
    this.searched = false;
    if (!extern && (!this.cleared || this.multiple)) {
      this.triggerFilter.emit();
    }
    this.cleared = true;
    return wasSet;
  }

  private setColumnClass(): void {
    for (const option of this.options) {
      const optionClass = { 'body-large': true };
      if (option['display'].includes('status')) {
        optionClass['notification-display-status'] = true;
        optionClass[option['displayClass']] = true;
      } else if (option['display'].includes('severity')) {
        optionClass['notification-display-severity'] = true;
        this.isSeverity = true;
      }
      this.optionClasses[option['display']] = optionClass;
    }
  }

  public onBlur(inputfield: string): void {
    if (this.runningTimer) {
      let triggeredFilter = '';
      clearTimeout(this.inputTimer);
      if (inputfield === 'textFilter') {
        triggeredFilter = 'text';
      } else if (inputfield === 'dateFilter') {
        triggeredFilter = 'date';
      } else if (inputfield === 'selectionFilter') {
        triggeredFilter = 'multiple';
      }
      if (triggeredFilter !== '') {
        this.triggerFiltering(triggeredFilter);
      }
    }

  }
}
