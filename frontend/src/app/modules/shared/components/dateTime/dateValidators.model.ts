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

import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export class DateValidators {
  public static atLeastNow(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) {
        return null;
      }

      const now = new Date();
      const currentDate = new Date(control.value);

      const isError = !currentDate || now.getTime() >= currentDate.getTime();
      return isError ? { currentDate: { actualValue: control.value, date: now } } : null;
    };
  }

  public static min(minDate: Date): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) {
        return null;
      }
      const currentDate = new Date(control.value);

      const isError = !currentDate || minDate.getTime() >= currentDate.getTime();
      return isError ? { minDate: { actualValue: control.value, date: minDate } } : null;
    };
  }

  public static max(maxDate: Date): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) {
        return null;
      }
      const currentDate = new Date(control.value);

      const isError = !currentDate || maxDate.getTime() <= currentDate.getTime();
      return isError ? { maxDate: { actualValue: control.value, date: maxDate } } : null;
    };
  }
}
