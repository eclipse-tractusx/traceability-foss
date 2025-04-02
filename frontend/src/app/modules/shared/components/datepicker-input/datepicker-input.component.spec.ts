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
import { DatePipe } from '@angular/common';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { TableType } from '@shared/components/multi-select-autocomplete/table-type.model';
import { FilterOperator, FilterValue } from '@shared/model/filter.model';
import { FilterService } from '@shared/service/filter.service';
import { SharedModule } from '@shared/shared.module';
import { renderComponent } from '@tests/test-render.utils';
import { BehaviorSubject } from 'rxjs';
import { DatepickerInputComponent } from './datepicker-input.component';

const filterStateSubject = new BehaviorSubject<any>({
  asBuilt: {},
  asPlanned: {},
});

const mockFilterService = {
  getFilter: jasmine.createSpy('getFilter'),
  filterState$: filterStateSubject.asObservable(),
  removeFilterKey: jasmine.createSpy('removeFilterKey'),
};


describe('DatepickerInputComponent', () => {
  const renderDateInputComponent = () => {

    return renderComponent(DatepickerInputComponent, {
      declarations: [],
      imports: [ ReactiveFormsModule, SharedModule ],
      componentProperties: {},
    });
  };
  beforeEach(() => {
    filterStateSubject.next({
      asBuilt: {},
      asPlanned: {},
    });
  });

  it('should clear datepicker input', async () => {
    const { fixture } = await renderDateInputComponent();
    const { componentInstance } = fixture;
    const parentFormGroup = new FormGroup({
      parentControlName: new FormControl('initialValue'),
    });
    componentInstance.parentFormGroup = parentFormGroup;
    componentInstance.parentControlName = 'parentControlName';

    componentInstance.clearRange();

    expect(parentFormGroup.get('parentControlName').value).toEqual('');
  });

  it('should set input according to filterstate', async () => {
    mockFilterService.getFilter.and.returnValue({
      dateField: '2025-02-01,2025-02-10',
    });

    filterStateSubject.next({
      asBuilt: {
        dateField: {
          value: [
            {
              value: '2025-02-01',
              strategy: FilterOperator.AFTER_LOCAL_DATE,
            },
            {
              value: '2025-02-10',
              strategy: FilterOperator.BEFORE_LOCAL_DATE,
            },
          ],
          operator: 'OR',
        },
      },
      asPlanned: {},
    });

    const parentForm = new FormGroup({
      dateField: new FormControl(''),
    });

    const { fixture } = await renderComponent(DatepickerInputComponent, {
      imports: [ ReactiveFormsModule ],
      providers: [
        { provide: FilterService, useValue: mockFilterService },
        DatePipe,
      ],
      componentProperties: {
        filterName: 'dateField',
        tableType: TableType.AS_BUILT_OWN,
        parentControlName: 'dateField',
        parentFormGroup: parentForm,
      },
    });

    const componentInstance = fixture.componentInstance;
    expect(componentInstance.dateRange.value.start).toEqual(new Date('2025-02-01'));
    expect(componentInstance.dateRange.value.end).toEqual(new Date('2025-02-10'));

    expect(
      (parentForm.get('dateField').value as unknown as FilterValue[])
        .map((fv) => fv.value)
        .join(',')
    ).toBe('2025-02-01,2025-02-10');
  });

  it('should update datepicker on filter state change', async () => {
    const parentForm = new FormGroup({
      dateField: new FormControl(''),
    });

    const { fixture } = await renderComponent(DatepickerInputComponent, {
      imports: [ ReactiveFormsModule ],
      providers: [
        { provide: FilterService, useValue: mockFilterService },
        DatePipe,
      ],
      componentProperties: {
        filterName: 'dateField',
        tableType: TableType.AS_BUILT_OWN,
        parentControlName: 'dateField',
        parentFormGroup: parentForm,
      },
    });
    const componentInstance = fixture.componentInstance;
    expect(componentInstance.dateRange.value).toEqual({ start: null, end: null });

    filterStateSubject.next({
      asBuilt: {
        dateField: {
          value: [
            {
              value: '2025-02-01',
              strategy: FilterOperator.AFTER_LOCAL_DATE,
            },
            {
              value: '2025-02-10',
              strategy: FilterOperator.BEFORE_LOCAL_DATE,
            },
          ],
          operator: 'OR',
        },
      },
      asPlanned: {},
    });

    fixture.detectChanges();

    expect(componentInstance.dateRange.value.start).toEqual(new Date('2025-02-01'));
    expect(componentInstance.dateRange.value.end).toEqual(new Date('2025-02-10'));
  });

});
