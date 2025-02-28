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
import { TableType } from '@shared/components/multi-select-autocomplete/table-type.model';
import { FilterService } from '@shared/service/filter.service';

@Component({
  selector: 'app-advanced-table-filter',
  templateUrl: './advanced-table-filter.component.html',
  styleUrls: ['./advanced-table-filter.component.scss']
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
        component: 'autocomplete',
        filterName: 'semanticModelId',
        controlName: 'semanticModelId',
      },
    ],

    // Row 2
    [
      {
        component: 'autocomplete',
        filterName: 'businessPartner',
        controlName: 'businessPartner',
      },
      {
        component: 'autocomplete',
        filterName: 'nameAtManufacturer',
        controlName: 'nameAtManufacturer',
      },
    ],

    // Row 3
    [
      {
        component: 'autocomplete',
        filterName: 'manufacturerName',
        controlName: 'manufacturerName',
      },
      {
        component: 'autocomplete',
        filterName: 'id',
        controlName: 'id',
      },
    ],

    // Row 4
    [
      {
        component: 'autocomplete',
        filterName: 'manufacturerPartId',
        controlName: 'manufacturerPartId',
      },
      {
        component: 'autocomplete',
        filterName: 'idShort',
        controlName: 'idShort',
      },
    ],

    // Row 5
    [
      {
        component: 'autocomplete',
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
        component: 'autocomplete',
        filterName: 'customerPartId',
        controlName: 'customerPartId',
      },
    ]
  ];


  @Input() tableType: TableType;
  enableSearchButton: boolean = false;

  constructor(private readonly filterService: FilterService) {
  }

  public readonly formGroup = new FormGroup({});

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

    const nonEmptyValues = Object.entries(formValues).reduce((acc, [key, value]) => {
      if (
        value != null &&
        !(typeof value === 'string' && value.trim() === '') &&
        !(Array.isArray(value) && value.length === 0)
      ) {
        acc[key] = value;
      }
      return acc;
    }, {} as Record<string, any>);

    this.filterService.setFilter(this.tableType, nonEmptyValues);
  }

  clearSearch(): void {
    this.formGroup.reset();
    this.filterService.clearFilter(this.tableType);
  }
}
