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

import { ReactiveFormsModule, UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { SharedModule } from '@shared/shared.module';
import { fireEvent, screen } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { InputComponent } from '@shared/components/input/input.component';

describe('InputComponent', () => {
  const renderInput = async (label = 'Label') => {
    const form = new UntypedFormGroup({
      formField: new UntypedFormControl(undefined, [Validators.required]),
    });

    await renderComponent(
      `
      <form [formGroup]="form">
        <app-input formControlName="formField" [label]="'${label}'"></app-input>
      </form>`,
      {
        declarations: [InputComponent],
        imports: [ReactiveFormsModule, SharedModule],
        componentProperties: {
          form: new UntypedFormGroup({
            formField: new UntypedFormControl(undefined, [Validators.required]),
          }),
        },
      },
    );

    return { form };
  };

  it('should render input', async () => {
    const label = 'Some label';
    await renderInput(label);

    const inputLabelElement = screen.getByText(label);
    expect(inputLabelElement).toBeInTheDocument();
  });

  it('should not render error message if input is not touched', async () => {
    const { form } = await renderInput();

    const inputLabelElement = screen.queryByText('errorMessage.required');
    expect(form.controls.formField.errors).toEqual({ required: true });
    expect(inputLabelElement).not.toBeInTheDocument();
  });

  it('should render error message if input is touched', async () => {
    const label = 'Some label';
    const { form } = await renderInput(label);

    fireEvent.blur(screen.getByLabelText(label));
    const inputLabelElement = screen.getByText('errorMessage.required');
    expect(form.controls.formField.errors).toEqual({ required: true });
    expect(inputLabelElement).toBeInTheDocument();
  });
});
