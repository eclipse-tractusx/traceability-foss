/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { Pipe, PipeTransform } from '@angular/core';
import { CalendarDateModel } from '@core/model/calendar-date.model';
import { QualityType } from '@page/parts/model/parts.model';
import { FormatDatePipe } from './format-date.pipe';

/**
 * This pipe allows us to format known objects/types properly in the same way.
 * Currently, it supports:
 * - CalendarDateModel - treat it as a calendar date object
 * - string - return as it is
 */
@Pipe({ name: 'autoFormat', pure: false })
export class AutoFormatPipe implements PipeTransform {
  constructor(private formatDatePipe: FormatDatePipe) {}

  transform(value: unknown): string {
    if (value instanceof CalendarDateModel) {
      return this.formatDatePipe.transform(value);
    }

    if (typeof value !== 'string') {
      return String(value);
    }

    if (Object.values(QualityType).includes(value as QualityType)) {
      return `qualityType.${value}`;
    }

    return value;
  }
}
