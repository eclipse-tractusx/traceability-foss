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
import { ValidationErrors } from '@angular/forms';
import { TranslationObject } from '@shared/pipes/i18n.pipe';

type MinError = { min: number; actual: number };
type MaxError = { max: number; actual: number };
type MinLengthError = { requiredLength: number; actualLength: number };
type MaxLengthError = { requiredLength: number; actualLength: number };
type PatternError = { requiredPattern: string; actualValue: string };

@Pipe({
  name: 'errorMessage',
})
export class ErrorMessagePipe implements PipeTransform {
  transform(errors: ValidationErrors): string {
    if (!errors) {
      return '';
    }
    const getErrorMapping = (key: string, value?: any): TranslationObject => {
      return { id: `errorMessage.${key}`, values: { [key]: value } };
    };

    const errorMessageMapping = new Map<string, any>([
      ['min', ({ min }: MinError) => getErrorMapping('min', min)],
      ['max', ({ max }: MaxError) => getErrorMapping('max', max)],
      ['minlength', ({ requiredLength }: MinLengthError) => getErrorMapping('minLength', requiredLength)],
      ['maxlength', ({ requiredLength }: MaxLengthError) => getErrorMapping('maxLength', requiredLength)],
      ['pattern', ({ requiredPattern }: PatternError) => getErrorMapping('pattern', requiredPattern)],

      ['required', _ => getErrorMapping('required')],
      ['email', _ => getErrorMapping('email')],
      ['generic', _ => getErrorMapping('generic')],
    ]);

    const keys = Object.keys(errors);
    const errorKey = keys.includes('required') ? 'required' : keys[0];

    return errorMessageMapping.get(errorKey)?.(errors[errorKey]) || errorMessageMapping.get('generic')();
  }
}
