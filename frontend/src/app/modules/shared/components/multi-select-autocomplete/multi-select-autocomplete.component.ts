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

import {Component, EventEmitter, Inject, Input, LOCALE_ID, OnChanges, Output, ViewChild} from '@angular/core';
import {FormControl} from '@angular/forms';
import {MatDatepickerInputEvent} from "@angular/material/datepicker";
import {DatePipe, registerLocaleData} from '@angular/common';
import {DateAdapter, MAT_DATE_LOCALE} from '@angular/material/core';
import localeDe from '@angular/common/locales/de';
import localeDeExtra from '@angular/common/locales/extra/de';

@Component({
    selector: 'app-multiselect',
    templateUrl: 'multi-select-autocomplete.component.html',
    styleUrls: ['multi-select-autocomplete.component.scss']
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
    multiple = true;
    @Input()
    textSearch = true;
    @Input()
    isDate = false;

    // New Options
    @Input()
    labelCount = 1;
    @Input()
    appearance = 'standard';

    public readonly minDate = new Date();

    @ViewChild('searchInput', {static: true}) searchInput: any;

    theSearchElement: string = '';

    @Output()
    selectionChange: EventEmitter<any> = new EventEmitter();

    @ViewChild('selectElem', {static: true}) selectElem: any;

    filteredOptions: Array<any> = [];
    selectedValue: Array<any> = [];
    selectAllChecked = false;
    displayString = '';

    constructor(public datePipe: DatePipe, public _adapter: DateAdapter<any>,
                @Inject(MAT_DATE_LOCALE) public _locale: string, @Inject(LOCALE_ID) private locale: string) {
        registerLocaleData(localeDe, 'de', localeDeExtra);
        this._adapter.setLocale(locale);
    }

    shouldHideTextSearchOptionField(): boolean {
        return !this.textSearch || this.textSearch && (this.theSearchElement === null || this.theSearchElement === '');
    }

    ngOnChanges(): void {
        this.filteredOptions = this.options;
        if (this.selectedOptions) {
            this.selectedValue = this.selectedOptions;
            this.formControl.patchValue(this.selectedValue);
        } else if (this.formControl?.value) {
            this.selectedValue = this.formControl.value;
            this.formControl.patchValue(this.selectedValue);
        }
    }

    toggleSelectAll = function (val: any): void {
        if (val.checked) {
            this.filteredOptions.forEach(option => {
                if (!this.selectedValue.includes(option[this.value])) {
                    this.selectedValue = this.selectedValue.concat([option[this.value]]);
                }
            });
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
        if (this.textSearch) {
            return;
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
        }

    }

    hideOption(option: any): boolean {
        return !(this.filteredOptions.indexOf(option) > -1);
    }

    // Returns plain strings array of filtered values
    getFilteredOptionsValues(): any[] {
        const filteredValues = [];
        this.filteredOptions.forEach(option => {
            filteredValues.push(option.value);
        });
        return filteredValues;
    }

    changeSearchTextOption(): void {
        this.formControl.patchValue(this.theSearchElement);
        this.selectedValue = this.theSearchElement as unknown as [];
    }

    dateSelectionEvent(event: MatDatepickerInputEvent<Date>) {
        let value = this.datePipe.transform(event.value, 'yyyy-MM-dd');
        this.formControl.patchValue(value);
        this.selectedValue = value as unknown as [];
        this.theSearchElement = value;
    }

    clickClear(): void {
        this.formControl.patchValue("");
        this.formControl.reset();
        if (this.searchInput) {
            this.searchInput.value = ''
        }
        this.theSearchElement = null;
        this.selectedValue = [];
    }


    onDisplayString(): string {
        this.displayString = '';
        if (this.textSearch) {
            this.displayString = this.theSearchElement || 'All';
            return this.displayString;
        }

        if (this.selectedValue?.length) {
            let displayOption = [];
            if (this.multiple) {
                this.handleMultipleSelectDisplay(displayOption);
            } else {
                this.handleSingleSelectDisplay(displayOption);
            }
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
                this.displayString += ` (+${this.selectedValue.length - this.labelCount} others)`;
            }
        }
    }

    private handleSingleSelectDisplay(displayOption: any) {
        let options = displayOption;
        options = this.options.filter(
            option => option[this.value] === this.selectedValue,
        );
        if (options.length) {
            this.displayString = options[0][this.display];
        }
    }

    onSelectionChange(val: any) {


        const filteredValues = this.getFilteredOptionsValues();
        if (this.multiple) {
            const selectedCount = this.selectedValue.filter(item => filteredValues.includes(item)).length;
            this.selectAllChecked = selectedCount === this.filteredOptions.length;
        }
        this.selectedValue = val.value;
        this.formControl.patchValue(val.value);
        this.selectionChange.emit(this.selectedValue);
        this.theSearchElement = val.value;
    }

}
