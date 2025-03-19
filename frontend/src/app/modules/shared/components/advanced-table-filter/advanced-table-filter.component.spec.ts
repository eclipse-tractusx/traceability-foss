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
import { ComponentFixture } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { FilterOperator } from '@page/parts/model/parts.model';
import { TableType } from '@shared/components/multi-select-autocomplete/table-type.model';
import { FilterService } from '@shared/service/filter.service';
import { SharedModule } from '@shared/shared.module';
import { renderComponent } from '@tests/test-render.utils';
import { of } from 'rxjs';

import { AdvancedTableFilterComponent } from './advanced-table-filter.component';


const mockFilterService = {
  setFilter: jasmine.createSpy('setFilter'),
  clearFilter: jasmine.createSpy('clearFilter'),
  getFilter: jasmine.createSpy('getFilter').and.returnValue({}),
  filterState$: of({ asBuilt: {}, asPlanned: {} }),
};


describe('AdvancedTableFilterComponent', () => {
  let component: AdvancedTableFilterComponent;
  let fixture: ComponentFixture<AdvancedTableFilterComponent>;

  async function renderAdvancedTableFilter(tableType = TableType.AS_BUILT_OWN) {
    return renderComponent(AdvancedTableFilterComponent, {
      imports: [ ReactiveFormsModule, SharedModule ],
      providers: [
        { provide: FilterService, useValue: mockFilterService },
      ],
      componentProperties: {
        tableType,
      },
    });
  }

  it('should create the component', async () => {
    const { fixture } = await renderAdvancedTableFilter();
    const instance = fixture.componentInstance;
    expect(instance).toBeTruthy();
  });

  it('should create form controls for each item in advancedFilters on init', async () => {
    const { fixture } = await renderAdvancedTableFilter();
    const instance = fixture.componentInstance;

    const flattened = instance.advancedFilters.flat();

    flattened.forEach(filterConfig => {
      const control = instance.formGroup.get(filterConfig.controlName);
      expect(control).toBeTruthy(`Missing control ${ filterConfig.controlName }`);
    });
  });

  it('should set enableSearchButton to false if all form controls are empty', async () => {
    const { fixture } = await renderAdvancedTableFilter();
    const instance = fixture.componentInstance;

    expect(instance.enableSearchButton).toBeFalse();
  });

  it('should set enableSearchButton to true if any control has a value', async () => {
    const { fixture } = await renderAdvancedTableFilter();
    const instance = fixture.componentInstance;

    const anyControlName = instance.advancedFilters[0][0].controlName;
    instance.formGroup.get(anyControlName).setValue('someValue');

    expect(instance.enableSearchButton).toBeTrue();
  });

  it('should call filterService.clearFilter and reset the form in clearSearch()', async () => {
    const { fixture } = await renderAdvancedTableFilter();
    const instance = fixture.componentInstance;

    // @ts-ignore
    instance.formGroup.get('owner').setValue([ 'OEM' ]);
    // @ts-ignore
    instance.formGroup.get('businessPartner').setValue('BP123');

    instance.clearSearch();

    expect(instance.formGroup.value).toEqual({
      owner: null,
      semanticModelId: null,
      businessPartner: null,
      nameAtManufacturer: null,
      manufacturerName: null,
      id: null,
      manufacturerPartId: null,
      idShort: null,
      manufacturingCountry: null,
      manufacturingDate: null,
      importState: null,
      customerPartId: null,
    });

    expect(mockFilterService.clearFilter).toHaveBeenCalledWith(instance.tableType);
  });

  it('should call filterService.setFilter with non-empty values in search()', async () => {
    const { fixture } = await renderAdvancedTableFilter(TableType.AS_PLANNED_OWN);
    const instance = fixture.componentInstance;

    // @ts-ignore
    instance.formGroup.get('owner').setValue([ 'OEM' ]);
    // @ts-ignore
    instance.formGroup.get('businessPartner').setValue('BP123');

    instance.search();

    expect(mockFilterService.setFilter).toHaveBeenCalledWith(TableType.AS_PLANNED_OWN, {
      owner: { value: [ { value: 'OEM', strategy: FilterOperator.EQUAL } ], operator: 'AND' },
      businessPartner: { value: [ { value: 'BP123', strategy: FilterOperator.EQUAL } ], operator: 'AND' },

    });
  });
});
