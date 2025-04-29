/********************************************************************************
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { Component, ElementRef, ViewChild } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { BaseFilterInputComponent } from '@shared/abstraction/baseInputFilter/baseInputFilter.component';

import { PartsStrategy } from '@shared/components/multi-select-autocomplete/autocomplete-strategy';
import { FilterAttribute } from '@shared/model/filter.model';
import { FilterService } from '@shared/service/filter.service';
import { of } from 'rxjs';
import { debounceTime, delay, distinctUntilChanged, switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-autocomplete-chip-input',
  templateUrl: './autocomplete-chip-input.component.html',
  styleUrls: [ './autocomplete-chip-input.component.scss' ],
})
export class AutocompleteChipInputComponent extends BaseFilterInputComponent {
  separatorKeysCodes: number[] = [ ENTER, COMMA ];
  itemCtrl = new FormControl('');
  filteredItemsList: string[] = [];
  items: string[] = [];

  @ViewChild('itemInput') itemInput: ElementRef<HTMLInputElement>;

  constructor(
    filterService: FilterService,
    private readonly partsStrategy: PartsStrategy,
  ) {
    super(filterService);
    this.itemCtrl.valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        switchMap((term) => {
          if (!term?.trim()) {
            this.filteredItemsList = [];
            return of([]);
          }
          return this.partsStrategy.retrieveSuggestionValues(
            this.tableType,
            this.filterName,
            term,
          );
        }),
      )
      .subscribe((items) => {
        // @ts-ignore
        this.filteredItemsList = items ?? [];
      });
  }

  onInputFocus(): void {
    const term = this.itemCtrl.value?.trim() || '';

    this.partsStrategy
      .retrieveSuggestionValues(this.tableType, this.filterName, term)
      .pipe(delay(300))
      .subscribe((items: any[]) => {
        this.filteredItemsList = items ?? [];
      });
  }

  onFilterValueChanged(filterAttribute: FilterAttribute): void {
    if (filterAttribute?.value?.length) {
      this.items = filterAttribute.value.map(value1 => value1.value);
    } else {
      this.items = [];
    }
    this.updateParentFormControl(this.items);
  }

  remove(item: string): void {
    const index = this.items.indexOf(item);
    if (index >= 0) {
      this.items.splice(index, 1);
      this.announcer.announce(`Removed ${ item }`);
      if (this.items.length === 0) {
        this.clearAllItems();
      } else {
        this.updateParentFormControl(this.items);
      }
    }
  }

  onCheckboxClicked(event: MouseEvent, option: string): void {
    event.stopPropagation();
    if (this.items.includes(option)) {
      this.remove(option);
    } else {
      this.items.push(option);
      this.updateParentFormControl(this.items);
    }
  }

  areAllSelected(): boolean {
    if (!this.filteredItemsList?.length) return false;
    return this.filteredItemsList.every((item) => this.items.includes(item));
  }

  onSelectAllClicked(event: MouseEvent): void {
    event.stopPropagation();
    if (!this.areAllSelected()) {
      const missing = this.filteredItemsList.filter((i) => !this.items.includes(i));
      this.items.push(...missing);
    } else {
      this.items = this.items.filter((i) => !this.filteredItemsList.includes(i));
    }
    this.updateParentFormControl(this.items);
  }

  selected(event: MatAutocompleteSelectedEvent): void {
    const val = event.option.viewValue;
    if (!this.items.includes(val)) {
      this.items.push(val);
    }
    this.clearInputField();
  }

  clearInputField(): void {
    if (this.itemInput?.nativeElement) {
      this.itemInput.nativeElement.value = '';
    }
    this.itemCtrl.setValue(null);
  }

  clearAllItems(): void {
    this.itemInput.nativeElement.value = '';
    this.itemCtrl.setValue(null);
    this.items = [];
    this.removeFilterValue();
    this.updateParentFormControl(this.items);
  }
}
