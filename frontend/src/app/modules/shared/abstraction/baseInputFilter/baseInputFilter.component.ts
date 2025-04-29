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
import { OnInit, Input, inject, Component } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { LiveAnnouncer } from '@angular/cdk/a11y';
import { FilterService } from '@shared/service/filter.service';
import { TableType } from '@shared/components/multi-select-autocomplete/table-type.model';
import { Subscription } from 'rxjs';

@Component({ selector: 'app-base-filter-input', template: '' })
export abstract class BaseFilterInputComponent implements OnInit {
  @Input() filterName: string;
  @Input() tableType: TableType;
  @Input() parentControlName: string;
  @Input() parentFormGroup: FormGroup;

  placeholderKey = '';
  announcer = inject(LiveAnnouncer);

  private filterSubscription: Subscription;

  protected constructor(protected readonly filterService: FilterService) {}

  ngOnInit(): void {
    this.placeholderKey = 'filter.placeholder.' + this.filterName;

    // Handle the current filter value at the time of initialization
    const currentFilter = this.filterService.getFilter(this.tableType);
    this.onFilterValueChanged(currentFilter?.[this.filterName]);

    // Subscribe to ongoing filter changes
    this.filterSubscription = this.filterService.filterState$.subscribe((state) => {
      const relevantFilters = (this.tableType === TableType.AS_BUILT_OWN)
        ? state.asBuilt
        : state.asPlanned;

      this.onFilterValueChanged(relevantFilters[this.filterName]);
    });
  }

  ngOnDestroy(): void {
    this.filterSubscription?.unsubscribe();
  }

  protected abstract onFilterValueChanged(value: unknown): void;

  protected updateParentFormControl(value: any): void {
    if (this.parentFormGroup && this.parentControlName) {
      this.parentFormGroup.get(this.parentControlName)?.setValue(value);
    }
  }

  protected removeFilterValue(): void {
    this.filterService.removeFilterKey(this.tableType, this.filterName);
  }
}
