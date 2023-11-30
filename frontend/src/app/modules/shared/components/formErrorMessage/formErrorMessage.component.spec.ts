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

import { FormControl, Validators } from '@angular/forms';
import { DateValidators } from '@shared/components/dateTime/dateValidators.model';
import { FormErrorMessageComponent } from '@shared/components/formErrorMessage/formErrorMessage.component';
import { screen } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { MyErrorStateMatcher } from '@shared/abstraction/baseInput/baseInput.helper';

describe('FormErrorMessageComponent', () => {
  const renderErrorMessage = async (value, validators) => {
    const control = new FormControl(value, validators);
    const matcher = new MyErrorStateMatcher();

    control.markAsTouched();
    control.updateValueAndValidity();

    return await renderComponent(FormErrorMessageComponent, { componentProperties: { control, matcher } });
  };

  it('should render required error message', async () => {
    await renderErrorMessage('', [ Validators.required ]);

    expect(screen.getByText('errorMessage.required')).toBeInTheDocument();
  });

  it('should render required error message', async () => {
    await renderErrorMessage('', [ Validators.required ]);

    expect(screen.getByText('errorMessage.required')).toBeInTheDocument();
  });

  it('should render min error message', async () => {
    await renderErrorMessage(0, [ Validators.min(1) ]);

    expect(screen.getByText('errorMessage.min')).toBeInTheDocument();
  });

  it('should render max error message', async () => {
    await renderErrorMessage(11, [ Validators.max(10) ]);

    expect(screen.getByText('errorMessage.max')).toBeInTheDocument();
  });

  it('should render email error message', async () => {
    await renderErrorMessage('not an email', [ Validators.email ]);

    expect(screen.getByText('errorMessage.email')).toBeInTheDocument();
  });

  it('should render minLength error message', async () => {
    await renderErrorMessage('123', [ Validators.minLength(5) ]);

    expect(screen.getByText('errorMessage.minLength')).toBeInTheDocument();
  });

  it('should render maxLength error message', async () => {
    await renderErrorMessage('123456', [ Validators.maxLength(5) ]);

    expect(screen.getByText('errorMessage.maxLength')).toBeInTheDocument();
  });

  it('should render pattern error message', async () => {
    await renderErrorMessage('a', [ Validators.pattern(/d/) ]);

    expect(screen.getByText('errorMessage.pattern')).toBeInTheDocument();
  });

  it('should render maxDate error message', async () => {
    await renderErrorMessage(new Date('2024-02-02'), [ DateValidators.max(new Date('2023-02-02')) ]);

    expect(screen.getByText('errorMessage.maxDate')).toBeInTheDocument();
  });

  it('should render minDate error message', async () => {
    await renderErrorMessage(new Date('2022-02-02'), [ DateValidators.min(new Date('2023-02-02')) ]);

    expect(screen.getByText('errorMessage.minDate')).toBeInTheDocument();
  });

  it('should render currentDate error message', async () => {
    await renderErrorMessage(new Date('2022-02-02'), [ DateValidators.atLeastNow() ]);

    expect(screen.getByText('errorMessage.currentDate')).toBeInTheDocument();
  });

  it('should render generic error message', async () => {
    const customValidator = _ => ({ notFound: true });
    await renderErrorMessage('', [ customValidator ]);

    expect(screen.getByText('errorMessage.generic')).toBeInTheDocument();
  });
});
