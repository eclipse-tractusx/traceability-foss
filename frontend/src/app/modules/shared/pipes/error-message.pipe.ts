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

import { Inject, Pipe, PipeTransform } from '@angular/core';
import { ValidationErrors } from '@angular/forms';
import { CalendarDateModel } from '@core/model/calendar-date.model';
import { ParameterizedMessage } from '@shared/model/i18n-message';
import { FormatDatePipe } from '@shared/pipes/format-date.pipe';
import { I18NEXT_SERVICE, ITranslationService } from 'angular-i18next';

type MinError = { min: number; actual: number };
type MaxError = { max: number; actual: number };
type MinLengthError = { requiredLength: number; actualLength: number };
type MaxLengthError = { requiredLength: number; actualLength: number };
type PatternError = { requiredPattern: string; actualValue: string };
type DateError = { date: Date; actualValue: string };

@Pipe({
  name: 'errorMessage',
})
export class ErrorMessagePipe implements PipeTransform {
  private datePipe: FormatDatePipe;

  constructor(@Inject(I18NEXT_SERVICE) translationService: ITranslationService) {
    this.datePipe = new FormatDatePipe(translationService);
  }

  public transform(errors: ValidationErrors): string {
    if (!errors) {
      return '';
    }

    const getErrorMapping = (key: string, value?: any, options: Record<string, unknown> = {}): ParameterizedMessage => {
      return { id: `errorMessage.${ key }`, values: { [key]: value, ...options } };
    };

    const formatDate = (date: Date): string => {
      const dateConfig: Intl.DateTimeFormatOptions = { dateStyle: 'long', timeStyle: 'short' };
      const formattedDate = this.datePipe.transform(new CalendarDateModel(date.toString()), dateConfig);
      return decodeURIComponent(formattedDate);
    };

    const errorMessageMapping = new Map<string, any>([
      [ 'min', ({ min }: MinError) => getErrorMapping('min', min) ],
      [ 'max', ({ max }: MaxError) => getErrorMapping('max', max) ],
      [
        'minlength',
        ({ requiredLength, actualLength }: MinLengthError) =>
          getErrorMapping('minLength', requiredLength, { current: actualLength }),
      ],
      [
        'maxlength',
        ({ requiredLength, actualLength }: MaxLengthError) =>
          getErrorMapping('maxLength', requiredLength, { current: actualLength }),
      ],
      [ 'pattern', ({ requiredPattern }: PatternError) => getErrorMapping('pattern', requiredPattern) ],
      [ 'maxDate', ({ date }: DateError) => getErrorMapping('maxDate', formatDate(date)) ],
      [ 'minDate', ({ date }: DateError) => getErrorMapping('minDate', formatDate(date)) ],
      [ 'currentDate', ({ date }: DateError) => getErrorMapping('currentDate', formatDate(date)) ],

      [ 'required', _ => getErrorMapping('required') ],
      [ 'url', _ => getErrorMapping('url') ],
      [ 'bpn', _ => getErrorMapping('bpn') ],
      [ 'invalidBpn', _ => getErrorMapping('invalidBpn' ) ],
      [ 'email', _ => getErrorMapping('email') ],
      [ 'pastDate', _ => getErrorMapping('pastDate') ],
      [ 'generic', _ => getErrorMapping('generic') ],
      [ 'minimumOneConstraint', _ => getErrorMapping('minimumOneConstraint') ],
    ]);

    const keys = Object.keys(errors);
    const errorKey = keys.includes('required') ? 'required' : keys[0];

    return errorMessageMapping.get(errorKey)?.(errors[errorKey]) || errorMessageMapping.get('generic')();
  }
}
