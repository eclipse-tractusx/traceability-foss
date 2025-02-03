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

import { UntypedFormControl, ValidatorFn, Validators } from '@angular/forms';
import { screen } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { SharedModule } from '@shared/shared.module';

describe('ErrorMessagePipe', () => {
  const getErrorsForRules = (value: string | number, rules: ValidatorFn[]) => {
    const control = new UntypedFormControl(value, rules);
    control.updateValueAndValidity();

    return control.errors;
  };

  it('should render error message for required', async () => {
    const errors = getErrorsForRules('', [Validators.required]);
    await renderComponent(`{{ errors | errorMessage | i18n }}`, {
      imports: [SharedModule],
      componentProperties: { errors },
    });

    expect(screen.getByText('errorMessage.required')).toBeInTheDocument();
  });

  it('should render error message for minimum number', async () => {
    const errors = getErrorsForRules(9, [Validators.min(10)]);
    await renderComponent(`{{ errors | errorMessage | i18n }}`, {
      imports: [SharedModule],
      componentProperties: { errors },
    });

    expect(screen.getByText('errorMessage.min')).toBeInTheDocument();
  });

  it('should render error message for maximum number', async () => {
    const errors = getErrorsForRules(10, [Validators.max(5)]);
    await renderComponent(`{{ errors | errorMessage | i18n }}`, {
      imports: [SharedModule],
      componentProperties: { errors },
    });

    expect(screen.getByText('errorMessage.max')).toBeInTheDocument();
  });

  it('should render error message for minimum length', async () => {
    const errors = getErrorsForRules('12345', [Validators.minLength(10)]);
    await renderComponent(`{{ errors | errorMessage | i18n }}`, {
      imports: [SharedModule],
      componentProperties: { errors },
    });

    expect(screen.getByText('errorMessage.minLength')).toBeInTheDocument();
  });

  it('should render error message for maximum length', async () => {
    const errors = getErrorsForRules('123456', [Validators.maxLength(5)]);
    await renderComponent(`{{ errors | errorMessage | i18n }}`, {
      imports: [SharedModule],
      componentProperties: { errors },
    });

    expect(screen.getByText('errorMessage.maxLength')).toBeInTheDocument();
  });

  it('should render error message for a specific pattern', async () => {
    const errors = getErrorsForRules('123', [Validators.pattern(/[a-z]+/g)]);
    await renderComponent(`{{ errors | errorMessage | i18n }}`, {
      imports: [SharedModule],
      componentProperties: { errors },
    });

    expect(screen.getByText('errorMessage.pattern')).toBeInTheDocument();
  });

  it('should render error message for invalid email', async () => {
    const errors = getErrorsForRules('thisIsNotAndEmail(AT).catena.de', [Validators.email]);
    await renderComponent(`{{ errors | errorMessage | i18n }}`, {
      imports: [SharedModule],
      componentProperties: { errors },
    });

    expect(screen.getByText('errorMessage.email')).toBeInTheDocument();
  });

  it('should render error message a not defined validation function', async () => {
    const customValidation = () => ({ customValidation: { valid: false } });
    const errors = getErrorsForRules('', [customValidation]);
    await renderComponent(`{{ errors | errorMessage | i18n }}`, {
      imports: [SharedModule],
      componentProperties: { errors },
    });

    expect(screen.getByText('errorMessage.generic')).toBeInTheDocument();
  });

  it('should render required error message first if multiple fail', async () => {
    const errors = getErrorsForRules('', [Validators.required, Validators.minLength(10)]);
    await renderComponent(`{{ errors | errorMessage | i18n }}`, {
      imports: [SharedModule],
      componentProperties: { errors },
    });

    expect(screen.getByText('errorMessage.required')).toBeInTheDocument();
  });
});
