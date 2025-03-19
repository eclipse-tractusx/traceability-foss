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
import { DatePipe } from '@angular/common';
import { ElementRef } from '@angular/core';
import { fakeAsync, tick } from '@angular/core/testing';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { FilterOperator } from '@page/parts/model/parts.model';
import { PartsStrategy } from '@shared/components/multi-select-autocomplete/autocomplete-strategy';
import { TableType } from '@shared/components/multi-select-autocomplete/table-type.model';
import { FilterService } from '@shared/service/filter.service';
import { SharedModule } from '@shared/shared.module';
import { renderComponent } from '@tests/test-render.utils';
import { BehaviorSubject, of } from 'rxjs';

import { AutocompleteInputComponent } from './autocomplete-input.component';

const filterStateSubject = new BehaviorSubject<any>({
  asBuilt: {},
  asPlanned: {},
});

const mockFilterService = {
  getFilter: jasmine.createSpy('getFilter'),
  filterState$: filterStateSubject.asObservable(),
  removeFilterKey: jasmine.createSpy('removeFilterKey'),
};

const mockPartsStrategy = {
  retrieveSuggestionValues: jasmine
    .createSpy('retrieveSuggestionValues')
    .and.returnValue(of([ 'defaultSuggestion' ])),
};


describe('AutocompleteInputComponent', () => {
  const renderAutocompleteInput = (overrides: Partial<{
    filterName: string;
    tableType: TableType;
    parentControlName: string;
    parentFormGroup: FormGroup;
  }> = {}) => {
    const defaultParentForm = new FormGroup({
      autoComplete: new FormControl([]),
    });

    return renderComponent(AutocompleteInputComponent, {
      imports: [ ReactiveFormsModule, SharedModule ],
      providers: [
        { provide: FilterService, useValue: mockFilterService },
        { provide: 'announcer', useValue: { announce: jasmine.createSpy('announce') } },
        { provide: DatePipe, useClass: DatePipe },
        { provide: PartsStrategy, useValue: mockPartsStrategy },
      ],
      componentProperties: {
        filterName: 'autoComplete',
        tableType: TableType.AS_BUILT_OWN,
        parentControlName: 'autoComplete',
        parentFormGroup: defaultParentForm,
        ...overrides,
      },
    });
  };

  it('should set input according to filterstate', async () => {
    mockFilterService.getFilter.and.returnValue({
      autoComplete: { value: [ { value: 'test', strategy: FilterOperator.EQUAL } ], operator: 'OR' },
    });

    filterStateSubject.next({
      asBuilt: {
        autoComplete: { value: [ { value: 'test', strategy: FilterOperator.EQUAL } ], operator: 'OR' },
      },
      asPlanned: {},
    });

    const parentForm = new FormGroup({
      autoComplete: new FormControl(''),
    });

    const { fixture } = await renderComponent(AutocompleteInputComponent, {
      imports: [ ReactiveFormsModule ],
      providers: [
        { provide: FilterService, useValue: mockFilterService },
        DatePipe,
      ],
      componentProperties: {
        filterName: 'autoComplete',
        tableType: TableType.AS_BUILT_OWN,
        parentControlName: 'autoComplete',
        parentFormGroup: parentForm,
      },
    });

    const componentInstance = fixture.componentInstance;
    expect(componentInstance.items).toEqual([ 'test' ]);

    expect(parentForm.get('autoComplete').value).toEqual([ 'test' ]);
  });

  it('should shorten visible selected elements', async () => {
    const { fixture } = await renderAutocompleteInput();
    const { componentInstance } = fixture;

    componentInstance.items = [ 'test', 'test2', 'test3', 'test4', 'test5', 'test6', 'test7', 'test8' ];
    const trimmedString = componentInstance.displaySelectedItemsTruncated();
    expect(trimmedString).toEqual('test... + 7');
    componentInstance.items = [ 'thisisareallylongstringthatshouldbetrimmed' ];
    const anotherTrimmedString = componentInstance.displaySelectedItemsTruncated();
    expect(anotherTrimmedString).toEqual('thisisareallylongstringthatsho...');
  });

  it('should call partsStrategy when onInputFocus is triggered and update filteredItemsList', fakeAsync(async () => {
    mockPartsStrategy.retrieveSuggestionValues.and.returnValue(of([ 'val1', 'val2' ]));

    const { fixture } = await renderAutocompleteInput();
    const { componentInstance } = fixture;

    componentInstance.onInputFocus();

    tick(500);

    fixture.detectChanges();

    expect(mockPartsStrategy.retrieveSuggestionValues).toHaveBeenCalledWith(
      componentInstance.tableType,
      componentInstance.filterName,
      '',
    );
    expect(componentInstance.filteredItemsList).toEqual([ 'val1', 'val2' ]);
  }));

  it('should return true in areAllSelected() if all filtered items are in items', async () => {
    const { fixture } = await renderAutocompleteInput();
    const { componentInstance } = fixture;

    componentInstance.filteredItemsList = [ 'a', 'b', 'c' ];
    componentInstance.items = [ 'a', 'b', 'c' ];

    expect(componentInstance.areAllSelected()).toBeTrue();
  });

  it('should clear the input, reset items, update parent form, and remove filter value', async () => {
    const parentForm = new FormGroup({
      autoComplete: new FormControl([ 'initialValue' ]),
    });

    const { fixture } = await renderAutocompleteInput();
    const { componentInstance } = fixture;

    componentInstance.parentFormGroup = parentForm;
    componentInstance.itemInput = {
      nativeElement: { value: 'some text' },
    } as ElementRef<HTMLInputElement>;

    componentInstance.items = [ 'initialValue' ];
    componentInstance.itemCtrl.setValue('some text');

    componentInstance.clearInput();

    expect(componentInstance.itemInput.nativeElement.value).toBe('');

    expect(componentInstance.itemCtrl.value).toBeNull();

    expect(componentInstance.items).toEqual([]);

    expect(parentForm.get('autoComplete').value).toEqual([]);

    expect(mockFilterService.removeFilterKey).toHaveBeenCalledWith(
      TableType.AS_BUILT_OWN,
      'autoComplete',
    );
  });
});
