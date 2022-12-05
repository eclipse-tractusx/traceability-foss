/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
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
  constructor(private readonly formatDatePipe: FormatDatePipe) {}

  public transform(value: unknown): string {
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
