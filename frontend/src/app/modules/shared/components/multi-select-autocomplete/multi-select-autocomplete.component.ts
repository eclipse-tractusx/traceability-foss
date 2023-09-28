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

import { Component, EventEmitter, Input, OnChanges, Output, ViewChild } from '@angular/core';
import { FormControl } from '@angular/forms';

@Component({
  selector: 'app-multiselect',
  templateUrl: 'multi-select-autocomplete.component.html',
  styleUrls: ['multi-select-autocomplete.component.scss']
})

export class MultiSelectAutocompleteComponent implements OnChanges {

  @Input()
  placeholder;
  @Input()
  options;
  @Input()
  disabled = false;
  @Input()
  display = 'display';
  @Input()
  value = 'value';
  @Input()
  formControl = new FormControl();
  @Input()
  errorMsg = 'Field is required';
  @Input()
  showErrorMsg = false;
  @Input()
  selectedOptions;
  @Input()
  multiple = true;
  @Input()
  textSearch = true;

  // New Options
  @Input()
  labelCount = 1;
  @Input()
  appearance = 'standard';

  @Output()
  selectionChange: EventEmitter<any> = new EventEmitter();

  @ViewChild('selectElem') selectElem;

  filteredOptions: Array<any> = [];
  selectedValue: Array<any> = [];
  selectAllChecked = false;
  displayString = '';

  constructor() {
  }

  ngOnChanges() {
    this.filteredOptions = this.options;
    if (this.selectedOptions) {
      this.selectedValue = this.selectedOptions;
    } else if (this.formControl.value) {
      this.selectedValue = this.formControl.value;
    }
  }

  toggleDropdown() {
    this.selectElem.toggle();
  }

  toggleSelectAll = function(val) {
    if (val.checked) {
      this.filteredOptions.forEach(option => {
        if (!this.selectedValue.includes(option[this.value])) {
          this.selectedValue = this.selectedValue.concat([ option[this.value] ]);
        }
      });
    } else {
      const filteredValues = this.getFilteredOptionsValues();
      this.selectedValue = this.selectedValue.filter(
        item => !filteredValues.includes(item),
      );
    }
    this.selectionChange.emit(this.selectedValue);
  };

  filterItem(value) {
    if (this.textSearch){
      console.log("this should be a text search");
    } else {
      console.log("this should be a static option search");
      this.filteredOptions = this.options.filter(
          item => item[this.display].toLowerCase().indexOf(value.toLowerCase()) > -1,
      );
      this.selectAllChecked = true;
      this.filteredOptions.forEach(item => {
        if (!this.selectedValue.includes(item[this.value])) {
          this.selectAllChecked = false;
        }
      });
    }

  }

  hideOption(option) {
    return !(this.filteredOptions.indexOf(option) > -1);
  }

  // Returns plain strings array of filtered values
  getFilteredOptionsValues() {
    const filteredValues = [];
    this.filteredOptions.forEach(option => {
      filteredValues.push(option.value);
    });
    return filteredValues;
  }

  onDisplayString() {
    this.displayString = '';
    if (this.selectedValue && this.selectedValue.length) {
      let displayOption = [];
      if (this.multiple) {
        // Multi select display
        for (let i = 0; i < this.labelCount; i++) {

          displayOption[i] = this.options.filter(
            option => option.value === this.selectedValue[i],
          )[0];
        }
        if (displayOption.length) {
          for (let i = 0; i < displayOption.length; i++) {

            this.displayString += displayOption[i][this.display] + ',';
          }
          this.displayString = this.displayString.slice(0, -1);
          if (this.selectedValue.length === this.options.length) {
            this.displayString = 'All';
          } else if (this.selectedValue.length > 1) {
            this.displayString += ` (+${this.selectedValue.length - this.labelCount} others)`;
          }
        }
      } else {
        // Single select display
        displayOption = this.options.filter(
          option => option[this.value] === this.selectedValue,
        );
        if (displayOption.length) {
          this.displayString = displayOption[0][this.display];
        }
      }
    }
    return this.displayString;
  }

  onSelectionChange(val) {
    const filteredValues = this.getFilteredOptionsValues();
    let count = 0;
    if (this.multiple) {
      this.selectedValue.filter(item => {
        if (filteredValues.includes(item)) {
          count++;
        }
      });
      this.selectAllChecked = count === this.filteredOptions.length;
    }
    this.selectedValue = val.value;
    this.selectionChange.emit(this.selectedValue);
  }

}
