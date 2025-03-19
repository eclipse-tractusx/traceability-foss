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
import { Component } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { FilterAttribute, FilterOperator, FilterValue } from '@page/parts/model/parts.model';
import { BaseFilterInputComponent } from '@shared/abstraction/baseInputFilter/baseInputFilter.component';

import { FilterService } from '@shared/service/filter.service';

@Component({
  selector: 'app-datepicker-input',
  templateUrl: './datepicker-input.component.html',
  styleUrls: [ './datepicker-input.component.scss' ],
})
export class DatepickerInputComponent extends BaseFilterInputComponent {
  dateRange = new FormGroup({
    start: new FormControl<Date | null>(null),
    end: new FormControl<Date | null>(null),
  });

  constructor(
    filterService: FilterService,
    public datePipe: DatePipe,
  ) {
    super(filterService);

    this.dateRange.valueChanges.subscribe((range) => {
      if (!range.start || !range.end) {
        return;
      }
      const startStr = this.datePipe.transform(range.start, 'yyyy-MM-dd');
      const endStr = this.datePipe.transform(range.end, 'yyyy-MM-dd');
      const filterValue = [
        {
          value: startStr,
          strategy: FilterOperator.AFTER_LOCAL_DATE,
        },
        {
          value: endStr,
          strategy: FilterOperator.BEFORE_LOCAL_DATE,
        },
      ];

      this.updateParentFormControl(filterValue);
      this.announcer.announce(`Selected date range from ${ startStr } to ${ endStr }`);
    });
  }

  clearRange(): void {
    this.dateRange.setValue({ start: null, end: null });
    this.updateParentFormControl('');
    this.removeFilterValue();
    this.announcer.announce(`Date range cleared`);
  }

  protected onFilterValueChanged(value: unknown): void {
    if (this.isFilterAttribute(value)) {
      const filterValues: FilterValue[] = value.value;
      let start: Date | null = null;
      let end: Date | null = null;

      filterValues.forEach(filterValue => {
        if (filterValue.strategy === FilterOperator.AFTER_LOCAL_DATE) {
          start = new Date(filterValue.value);
        } else if (filterValue.strategy === FilterOperator.BEFORE_LOCAL_DATE) {
          end = new Date(filterValue.value);
        }
      });

      this.dateRange.setValue({ start, end });
    } else {
      this.dateRange.setValue({ start: null, end: null });
    }
  }

  private isFilterAttribute(value: unknown): value is FilterAttribute {
    return (
      typeof value === "object" &&
      value !== null &&
      "value" in value &&
      Array.isArray(value.value) &&
      value.value.every(
        (fv) =>
          typeof fv === "object" &&
          fv !== null &&
          "value" in fv &&
          "strategy" in fv
      )
    );
  }
}
