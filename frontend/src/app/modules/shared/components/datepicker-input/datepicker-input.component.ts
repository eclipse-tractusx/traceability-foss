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
import { Component } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { BaseFilterInputComponent } from '@shared/abstraction/baseInputFilter/baseInputFilter.component';

import { FilterService } from '@shared/service/filter.service';

@Component({
  selector: 'app-datepicker-input',
  templateUrl: './datepicker-input.component.html',
  styleUrls: ['./datepicker-input.component.scss'],
})
export class DatepickerInputComponent extends BaseFilterInputComponent {
  dateRange = new FormGroup({
    start: new FormControl<Date | null>(null),
    end: new FormControl<Date | null>(null),
  });

  constructor(
    filterService: FilterService,
    public datePipe: DatePipe
  ) {
    super(filterService);

    this.dateRange.valueChanges.subscribe((range) => {
      if (!range.start || !range.end) {
        return;
      }
      const startStr = this.datePipe.transform(range.start, 'yyyy-MM-dd');
      const endStr = this.datePipe.transform(range.end, 'yyyy-MM-dd');
      const filterValue = `${startStr},${endStr}`;

      this.updateParentFormControl(filterValue);
      this.announcer.announce(`Selected date range from ${startStr} to ${endStr}`);
    });
  }

  protected onFilterValueChanged(value: unknown): void {
    if (typeof value === 'string') {
      const [startStr, endStr] = value.split(',');
      this.dateRange.setValue({
        start: startStr ? new Date(startStr) : null,
        end: endStr ? new Date(endStr) : null,
      });
    } else {
      this.dateRange.setValue({ start: null, end: null });
    }
  }

  clearRange(): void {
    this.dateRange.setValue({ start: null, end: null });
    this.updateParentFormControl('');
    this.removeFilterValue();
    this.announcer.announce(`Date range cleared`);
  }
}
