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
import { Component, Input, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { FilterAttribute, FilterOperator, FilterValue } from '@page/parts/model/parts.model';
import { TableType } from '@shared/components/multi-select-autocomplete/table-type.model';
import { FilterService } from '@shared/service/filter.service';

@Component({
  selector: 'app-advanced-table-filter',
  templateUrl: './advanced-table-filter.component.html',
  styleUrls: [ './advanced-table-filter.component.scss' ],
})
export class AdvancedTableFilterComponent implements OnInit {

  advancedFilters = [
    // Row 1
    [
      {
        component: 'chip',
        filterName: 'owner',
        controlName: 'owner',
      },
      {
        component: 'chip',
        filterName: 'semanticModelId',
        controlName: 'semanticModelId',
      },
    ],

    // Row 2
    [
      {
        component: 'chip',
        filterName: 'businessPartner',
        controlName: 'businessPartner',
      },
      {
        component: 'chip',
        filterName: 'nameAtManufacturer',
        controlName: 'nameAtManufacturer',
      },
    ],

    // Row 3
    [
      {
        component: 'chip',
        filterName: 'manufacturerName',
        controlName: 'manufacturerName',
      },
      {
        component: 'chip',
        filterName: 'id',
        controlName: 'id',
      },
    ],

    // Row 4
    [
      {
        component: 'chip',
        filterName: 'manufacturerPartId',
        controlName: 'manufacturerPartId',
      },
      {
        component: 'chip',
        filterName: 'idShort',
        controlName: 'idShort',
      },
    ],

    // Row 5
    [
      {
        component: 'chip',
        filterName: 'manufacturingCountry',
        controlName: 'manufacturingCountry',
      },
      {
        component: 'datepicker',
        filterName: 'manufacturingDate',
        controlName: 'manufacturingDate',
      },
    ],

    // Row 6
    [
      {
        component: 'chip',
        filterName: 'importState',
        controlName: 'importState',
      },
      {
        component: 'chip',
        filterName: 'customerPartId',
        controlName: 'customerPartId',
      },
    ],
  ];

  @Input() tableType: TableType;
  enableSearchButton: boolean = false;
  public readonly formGroup = new FormGroup({});

  constructor(private readonly filterService: FilterService) {
  }

  ngOnInit() {
    this.advancedFilters.flat().forEach(filter => {
      this.formGroup.addControl(filter.controlName, new FormControl(null));
    });

    this.formGroup.valueChanges.subscribe(() => {
      this.checkFormState();
    });
  }

  checkFormState(): void {
    const anyValuePresent = Object.values(this.formGroup.value).some(value => {
      if (!value) return false;
      if (Array.isArray(value)) return value.length > 0;
      if (typeof value === 'string') return value.trim().length > 0;
      return true;
    });

    this.enableSearchButton = anyValuePresent && this.formGroup.valid;
  }

  search() {
    const formValues = this.formGroup.value;

    const nonEmptyValues = Object.entries(formValues).reduce((acc, [ key, value ]) => {
      if (
        value != null &&
        !(typeof value === 'string' && value.trim() === '') &&
        !(Array.isArray(value) && value.length === 0)
      ) {
        acc[key] = {
          value: (Array.isArray(value) ? value : [ value ]).map(val =>
            this.isFilterValue(val)
              ? val as FilterValue
              : { value: val, strategy: FilterOperator.EQUAL }),
          operator: "AND"
        } as FilterAttribute;
      }
      return acc;
    }, {} as Record<string, any>);

    this.filterService.setFilter(this.tableType, nonEmptyValues);
  }

  private isFilterValue(val: any) {
    return typeof val === 'object' && 'value' in val && 'strategy' in val;
  }

  clearSearch(): void {
    this.formGroup.reset();
    this.filterService.clearFilter(this.tableType);
  }
}
