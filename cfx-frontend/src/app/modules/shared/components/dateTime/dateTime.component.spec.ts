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

import { UntypedFormControl, UntypedFormGroup, ValidatorFn } from '@angular/forms';
import { DateTimeComponent } from '@shared/components/dateTime/dateTime.component';
import { DateValidators } from '@shared/components/dateTime/dateValidators.model';
import { fireEvent, screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { sleepForTests } from '../../../../../test';
import { SharedModule } from '@shared/shared.module';

describe('DateTimeComponent', () => {
  const renderDateTime = async (label = 'Label', validators: ValidatorFn[] = []) => {
    const form = new UntypedFormGroup({
      formField: new UntypedFormControl(undefined, validators),
    });

    const { fixture } = await renderComponent(
      `
      <form [formGroup]="form">
        <app-date-time formControlName="formField" [max]="maxDate" [label]="'${label}'"></app-date-time>
      </form>`,
      {
        declarations: [DateTimeComponent],
        componentProperties: { form },
      },
    );

    return { form, fixture };
  };

  const renderDateTimeComponent = async (maxDate = null) => {
    return await renderComponent(DateTimeComponent, {
      imports: [SharedModule],
      componentInputs: {
        max: maxDate,
      }
    });
  };

  it('should render', async () => {
    const label = 'Some label';
    await renderDateTime(label);

    const dateTimeElement = screen.getByText(label);
    expect(dateTimeElement).toBeInTheDocument();
  });

  it('should render minimum date error message', async () => {
    const label = 'Some label';
    const minDate = new Date('2022-02-20T12:00');
    const dateInput = 'Sat, 20 Feb 2021 12:00:00 GMT';

    const { form } = await renderDateTime(label, [DateValidators.min(minDate)]);

    const inputElement = screen.getByTestId('BaseInputElement-0');
    expect(inputElement).toBeInTheDocument();

    fireEvent.input(inputElement, { target: { value: dateInput } });
    form.controls.formField.markAsTouched();
    form.controls.formField.updateValueAndValidity();

    const errorMessageLabel = await waitFor(() => screen.getByText('errorMessage.minDate'));
    expect(errorMessageLabel).toBeInTheDocument();

    expect(form.controls.formField.errors).toEqual({ minDate: { actualValue: dateInput, date: minDate } });
  });

  it('should set the maximum date', async () => {
    const label = 'Some label';

    const maxDate = new Date('2022-02-20T12:00');

    const { fixture } = await renderDateTimeComponent(maxDate);
    expect(fixture.componentInstance.maxDate).toEqual(maxDate);
  });

  it('should render maximum date error message', async () => {
    const label = 'Some label';

    const maxDate = new Date('2022-02-20T12:00');
    const dateInput = 'Mon, 20 Feb 2023 11:00:00 GMT';

    const { form } = await renderDateTime(label, [DateValidators.max(maxDate)]);

    const inputElement = screen.getByTestId('BaseInputElement-0');
    expect(inputElement).toBeInTheDocument();
    fireEvent.input(inputElement, { target: { value: dateInput } });
    form.controls.formField.markAsTouched();
    form.controls.formField.updateValueAndValidity();

    await sleepForTests(1000);
    const errorMessageLabel = await waitFor(() => screen.getByText('errorMessage.maxDate'));
    expect(errorMessageLabel).toBeInTheDocument();

    expect(form.controls.formField.errors).toEqual({ maxDate: { actualValue: dateInput, date: maxDate } });
  });

  it('should render current date error message', async () => {
    const label = 'Some label';
    const dateInput = new Date().toISOString().substring(0, 16);

    const { form } = await renderDateTime(label, [DateValidators.atLeastNow()]);

    const inputElement = screen.getByTestId('BaseInputElement-0');
    expect(inputElement).toBeInTheDocument();

    fireEvent.input(inputElement, { target: { value: dateInput } });
    form.controls.formField.markAsTouched();
    form.controls.formField.updateValueAndValidity();

    const errorMessageLabel = await waitFor(() => screen.getByText('errorMessage.currentDate'));
    expect(errorMessageLabel).toBeInTheDocument();
  });
});
