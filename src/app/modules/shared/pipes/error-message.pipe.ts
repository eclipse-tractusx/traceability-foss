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

type MinError = { min: number; actual: number };
type MaxError = { max: number; actual: number };
type MinLengthError = { requiredLength: number; actualLength: number };
type MaxLengthError = { requiredLength: number; actualLength: number };
type PatternError = { requiredPattern: string; actualValue: string };

@Pipe({
  name: 'ErrorMessage',
})
export class ErrorMessagePipe implements PipeTransform {
  transform(errors: ValidationErrors): string {
    if (!errors) {
      return '';
    }

    const errorMessageMapping = new Map<string, any>([
      ['min', ({ min }: MinError) => `errorMessage.min:min:${min}`],
      ['max', ({ max }: MaxError) => `errorMessage.max:max:${max}`],
      ['minlength', ({ requiredLength }: MinLengthError) => `errorMessage.minLength:minLength:${requiredLength}`],
      ['maxlength', ({ requiredLength }: MaxLengthError) => `errorMessage.maxLength:maxLength:${requiredLength}`],
      ['pattern', ({ requiredPattern }: PatternError) => `errorMessage.pattern:pattern:${requiredPattern}`],

      ['required', _ => 'errorMessage.required'],
      ['email', _ => 'errorMessage.email'],
      ['generic', _ => 'errorMessage.generic'],
    ]);

    const errorKey = Object.keys(errors).reduce((p, c) => (!p || c === 'required' ? c : p), '');
    return errorMessageMapping.get(errorKey)?.(errors[errorKey]) || errorMessageMapping.get('generic')();
  }
}
