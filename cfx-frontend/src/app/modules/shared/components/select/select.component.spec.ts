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

import { FormControl } from '@angular/forms';
import { SelectComponent, SelectOption } from '@shared/components/select/select.component';
import { SharedModule } from '@shared/shared.module';
import { screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';

describe('SelectComponent', () => {
  it('should render the select component', async () => {
    const label = 'Test';
    await renderComponent(`<app-select [label]='label'></app-select>`, {
      imports: [SharedModule],
      declarations: [SelectComponent],
      componentProperties: { label },
    });

    expect(screen.getByText(label)).toBeInTheDocument();
  });

  it('should render the select component with options', async () => {
    const label = 'Test';
    const options: SelectOption[] = [{ label: 'Test_01' }, { label: 'Test_02' }];
    const fixture = await renderComponent(`<app-select [label]='label' [options]='options'></app-select>`, {
      imports: [SharedModule],
      declarations: [SelectComponent],
      componentProperties: { label, options },
    });

    const selectComponent = screen.getByText(label);
    expect(selectComponent).toBeInTheDocument();
    selectComponent.click();
    fixture.detectChanges();

    await waitFor(() => expect(screen.getByText(options[0].label)).toBeInTheDocument());
    await waitFor(() => expect(screen.getByText(options[1].label)).toBeInTheDocument());
  });

  it('should select correct option if selected value is set', async () => {
    const label = 'Test';
    const options: SelectOption[] = [{ label: 'Test_01' }, { label: 'Test_02' }];
    const formControl = new FormControl(options[1].label);

    await renderComponent(`<app-select [label]='label' [options]='options' [formControl]='formControl'></app-select>`, {
      imports: [SharedModule],
      declarations: [SelectComponent],
      componentProperties: { label, options, formControl },
    });

    const selectComponent = screen.getByText(label);
    expect(selectComponent).toBeInTheDocument();

    await waitFor(() => expect(screen.getByText(options[1].label)).toBeInTheDocument());
  });

  it('should select correct option with value label mapping if selected value is set', async () => {
    const label = 'Test';
    const options: SelectOption[] = [
      { label: 'Test_01', value: '111' },
      { label: 'Test_02', value: '222' },
    ];
    const formControl = new FormControl(options[1].value);

    await renderComponent(`<app-select [label]='label' [options]='options' [formControl]='formControl'></app-select>`, {
      imports: [SharedModule],
      declarations: [SelectComponent],
      componentProperties: { label, options, formControl },
    });

    const selectComponent = screen.getByText(label);
    expect(selectComponent).toBeInTheDocument();

    await waitFor(() => expect(screen.getByText(options[1].label)).toBeInTheDocument());
  });

  it('should render the select component with custom option renderer', async () => {
    const label = 'Test';
    const options: SelectOption[] = [{ label: 'Test_01' }, { label: 'Test_02' }];
    const fixture = await renderComponent(
      `<ng-template #test let-value='value'>_TEST_{{value}}</ng-template>
                <app-select [label]='label' [options]='options' [optionsRenderer]='test'></app-select>`,
      {
        imports: [SharedModule],
        declarations: [SelectComponent],
        componentProperties: { label, options },
      },
    );

    const selectComponent = screen.getByText(label);
    expect(selectComponent).toBeInTheDocument();
    selectComponent.click();
    fixture.detectChanges();

    await waitFor(() => expect(screen.getByText('_TEST_' + options[0].label)).toBeInTheDocument());
    await waitFor(() => expect(screen.getByText('_TEST_' + options[1].label)).toBeInTheDocument());
  });
});
