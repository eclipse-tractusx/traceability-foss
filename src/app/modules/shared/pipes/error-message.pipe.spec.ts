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

import { FormControl, ValidatorFn, Validators } from '@angular/forms';
import { screen } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { SharedModule } from '..';

describe('ErrorMessagePipe', () => {
  const getErrorsForRules = (value: string | number, rules: ValidatorFn[]) => {
    const control = new FormControl(value, rules);
    control.updateValueAndValidity();

    return control.errors;
  };

  it('should render error message for required', async () => {
    const errors = getErrorsForRules('', [Validators.required]);
    await renderComponent(`{{ errors | errorMessage | i18n }}`, {
      imports: [SharedModule],
      componentProperties: { errors },
    });

    expect(screen.getByText('This field is required!')).toBeInTheDocument();
  });

  it('should render error message for minimum number', async () => {
    const errors = getErrorsForRules(9, [Validators.min(10)]);
    await renderComponent(`{{ errors | errorMessage | i18n }}`, {
      imports: [SharedModule],
      componentProperties: { errors },
    });

    expect(screen.getByText('Please enter a number that is larger than: 10.')).toBeInTheDocument();
  });

  it('should render error message for maximum number', async () => {
    const errors = getErrorsForRules(10, [Validators.max(5)]);
    await renderComponent(`{{ errors | errorMessage | i18n }}`, {
      imports: [SharedModule],
      componentProperties: { errors },
    });

    expect(screen.getByText('Please enter a number that is smaller than: 5.')).toBeInTheDocument();
  });

  it('should render error message for minimum length', async () => {
    const errors = getErrorsForRules('12345', [Validators.minLength(10)]);
    await renderComponent(`{{ errors | errorMessage | i18n }}`, {
      imports: [SharedModule],
      componentProperties: { errors },
    });

    expect(screen.getByText('Please enter a text that is larger than: 10.')).toBeInTheDocument();
  });

  it('should render error message for maximum length', async () => {
    const errors = getErrorsForRules('123456', [Validators.maxLength(5)]);
    await renderComponent(`{{ errors | errorMessage | i18n }}`, {
      imports: [SharedModule],
      componentProperties: { errors },
    });

    expect(screen.getByText('Please enter a text that is smaller than: 5.')).toBeInTheDocument();
  });

  it('should render error message for a specific pattern', async () => {
    const errors = getErrorsForRules('123', [Validators.pattern(/[a-z]+/g)]);
    await renderComponent(`{{ errors | errorMessage | i18n }}`, {
      imports: [SharedModule],
      componentProperties: { errors },
    });

    expect(screen.getByText('Please enter data that matches this pattern: &#x2F;[a-z]+&#x2F;g.')).toBeInTheDocument();
  });

  it('should render error message for invalid email', async () => {
    const errors = getErrorsForRules('thisIsNotAndEmail(AT).catena.de', [Validators.email]);
    await renderComponent(`{{ errors | errorMessage | i18n }}`, {
      imports: [SharedModule],
      componentProperties: { errors },
    });

    expect(screen.getByText('Please enter a valid E-Mail.')).toBeInTheDocument();
  });

  it('should render error message a not defined validation function', async () => {
    const customValidation = () => ({ customValidation: { valid: false } });
    const errors = getErrorsForRules('', [customValidation]);
    await renderComponent(`{{ errors | errorMessage | i18n }}`, {
      imports: [SharedModule],
      componentProperties: { errors },
    });

    expect(screen.getByText('Please enter valid data.')).toBeInTheDocument();
  });

  it('should render required error message first if multiple fail', async () => {
    const errors = getErrorsForRules('', [Validators.required, Validators.minLength(10)]);
    await renderComponent(`{{ errors | errorMessage | i18n }}`, {
      imports: [SharedModule],
      componentProperties: { errors },
    });

    expect(screen.getByText('This field is required!')).toBeInTheDocument();
  });
});
