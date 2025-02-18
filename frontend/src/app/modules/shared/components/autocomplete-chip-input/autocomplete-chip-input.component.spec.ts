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
import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { PartsStrategy } from '@shared/components/multi-select-autocomplete/autocomplete-strategy';
import { TableType } from '@shared/components/multi-select-autocomplete/table-type.model';
import { FilterService } from '@shared/service/filter.service';
import { renderComponent } from '@tests/test-render.utils';
import { BehaviorSubject, of } from 'rxjs';
import { delay } from 'rxjs/operators';

import { AutocompleteChipInputComponent } from './autocomplete-chip-input.component';

fdescribe('AutocompleteChipInputComponent', () => {
  const filterStateSubject = new BehaviorSubject<any>({ asBuilt: {}, asPlanned: {} });
  const mockFilterService = {
    getFilter: jasmine.createSpy('getFilter').and.returnValue({}),
    filterState$: filterStateSubject.asObservable(),
    removeFilterKey: jasmine.createSpy('removeFilterKey'),
  };

  /* Mock partsStrategy */
  const mockPartsStrategy = {
    retrieveSuggestionValues: jasmine
      .createSpy('retrieveSuggestionValues')
      .and.returnValue(of(['defaultSuggestion'])),
  };

  const renderAutocompleteChipInput = async (
    overrides: Partial<{
      filterName: string;
      tableType: TableType;
      parentControlName: string;
      parentFormGroup: FormGroup;
    }> = {}
  ) => {
    const defaultParentForm = new FormGroup({
      chipControl: new FormControl([]),
    });

    return renderComponent(AutocompleteChipInputComponent, {
      imports: [ReactiveFormsModule],
      providers: [
        { provide: FilterService, useValue: mockFilterService },
        { provide: PartsStrategy, useValue: mockPartsStrategy },
        { provide: 'announcer', useValue: { announce: jasmine.createSpy('announce') } },
      ],
      componentProperties: {
        filterName: 'chipControl',
        tableType: TableType.AS_BUILT_OWN,
        parentControlName: 'chipControl',
        parentFormGroup: defaultParentForm,
        ...overrides,
      },
    });
  };

  it('should create the component', async () => {
    const { fixture } = await renderAutocompleteChipInput();
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should set items from an array when onFilterValueChanged is called', async () => {
    const { fixture } = await renderAutocompleteChipInput();
    const { componentInstance } = fixture;

    componentInstance.onFilterValueChanged(['item1', 'item2']);
    expect(componentInstance.items).toEqual(['item1', 'item2']);
  });

  it('should clear items if the provided filter value is not an array', async () => {
    const { fixture } = await renderAutocompleteChipInput();
    const { componentInstance } = fixture;

    componentInstance.items = ['existingItem'];
    componentInstance.onFilterValueChanged('someNonArrayValue');
    expect(componentInstance.items).toEqual([]);
  });

  it('should clear all items if last one is removed', async () => {
    const { fixture } = await renderAutocompleteChipInput();
    const { componentInstance } = fixture;

    spyOn(componentInstance, 'clearAllItems').and.callThrough();

    componentInstance.items = ['onlyItem'];
    componentInstance.remove('onlyItem');

    expect(componentInstance.clearAllItems).toHaveBeenCalled();
  });

  it('should toggle an item on checkbox click', async () => {
    const { fixture } = await renderAutocompleteChipInput();
    const { componentInstance } = fixture;

    const event = new MouseEvent('click');
    componentInstance.onCheckboxClicked(event, 'newItem');
    expect(componentInstance.items).toEqual(['newItem']);

    componentInstance.onCheckboxClicked(event, 'newItem');
    expect(componentInstance.items).toEqual([]);
  });

  it('should return true from areAllSelected if all items are in items array', async () => {
    const { fixture } = await renderAutocompleteChipInput();
    const { componentInstance } = fixture;

    componentInstance.filteredItemsList = ['a', 'b', 'c'];
    componentInstance.items = ['a', 'b', 'c'];

    expect(componentInstance.areAllSelected()).toBeTrue();
  });

  it('should add new selected item on autocomplete selected()', async () => {
    const { fixture } = await renderAutocompleteChipInput();
    const { componentInstance } = fixture;

    const fakeEvent = {
      option: { viewValue: 'newItem' },
    } as MatAutocompleteSelectedEvent;

    componentInstance.items = ['existingItem'];
    componentInstance.selected(fakeEvent);
    expect(componentInstance.items).toEqual(['existingItem', 'newItem']);
  });

  it('should not duplicate item if already in items array', async () => {
    const { fixture } = await renderAutocompleteChipInput();
    const { componentInstance } = fixture;

    const fakeEvent = {
      option: { viewValue: 'existingItem' },
    } as MatAutocompleteSelectedEvent;

    componentInstance.items = ['existingItem'];
    componentInstance.selected(fakeEvent);
    expect(componentInstance.items).toEqual(['existingItem']);
  });

  it('should clear all items (and remove filter value) with clearAllItems()', async () => {
    const { fixture } = await renderAutocompleteChipInput();
    const { componentInstance } = fixture;

    componentInstance.itemInput = {
      nativeElement: { value: 'some text' },
    } as any;

    componentInstance.items = ['item1', 'item2'];
    componentInstance.clearAllItems();

    expect(componentInstance.itemInput.nativeElement.value).toBe('');
    expect(componentInstance.itemCtrl.value).toBeNull();
    expect(componentInstance.items).toEqual([]);
    expect(mockFilterService.removeFilterKey).toHaveBeenCalledWith(
      componentInstance.tableType,
      componentInstance.filterName
    );
  });

});
