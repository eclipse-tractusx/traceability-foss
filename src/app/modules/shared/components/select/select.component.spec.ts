/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
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

import { SelectComponent, SelectOption } from '@shared/components/select/select.component';
import { SharedModule } from '@shared/shared.module';
import { screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';

describe('SelectComponent', () => {
  it('should render the select component', async () => {
    const lable = 'Test';
    await renderComponent(`<app-select [lable]='lable'></app-select>`, {
      imports: [SharedModule],
      declarations: [SelectComponent],
      componentProperties: { lable },
    });

    expect(screen.getByText(lable)).toBeInTheDocument();
  });

  it('should render the select component with options', async () => {
    const lable = 'Test';
    const options: SelectOption[] = [{ lable: 'Test_01' }, { lable: 'Test_02' }];
    await renderComponent(`<app-select [lable]='lable' [options]='options'></app-select>`, {
      imports: [SharedModule],
      declarations: [SelectComponent],
      componentProperties: { lable, options },
    });

    const selectComponent = screen.getByText(lable);
    expect(selectComponent).toBeInTheDocument();
    selectComponent.click();

    await waitFor(() => expect(screen.getByText(options[0].lable)).toBeInTheDocument());
    await waitFor(() => expect(screen.getByText(options[1].lable)).toBeInTheDocument());
  });

  it('should select correct option if selected value is set', async () => {
    const lable = 'Test';
    const options: SelectOption[] = [{ lable: 'Test_01' }, { lable: 'Test_02' }];
    await renderComponent(
      `<app-select [lable]='lable' [options]='options' [selectedValue]='options[0].lable'></app-select>`,
      {
        imports: [SharedModule],
        declarations: [SelectComponent],
        componentProperties: { lable, options },
      },
    );

    const selectComponent = screen.getByText(lable);
    expect(selectComponent).toBeInTheDocument();

    await waitFor(() => expect(screen.getByText(options[0].lable)).toBeInTheDocument());
  });

  it('should select correct option with value lable mapping if selected value is set', async () => {
    const lable = 'Test';
    const options: SelectOption[] = [
      { lable: 'Test_01', value: '111' },
      { lable: 'Test_02', value: '222' },
    ];
    await renderComponent(
      `<app-select [lable]='lable' [options]='options' [selectedValue]='options[1].value'></app-select>`,
      {
        imports: [SharedModule],
        declarations: [SelectComponent],
        componentProperties: { lable, options },
      },
    );

    const selectComponent = screen.getByText(lable);
    expect(selectComponent).toBeInTheDocument();

    await waitFor(() => expect(screen.getByText(options[1].lable)).toBeInTheDocument());
  });

  it('should render the select component with custom option renderer', async () => {
    const lable = 'Test';
    const options: SelectOption[] = [{ lable: 'Test_01' }, { lable: 'Test_02' }];
    await renderComponent(
      `<ng-template #test let-value='value'>_TEST_{{value}}</ng-template>
                <app-select [lable]='lable' [options]='options' [optionsRenderer]='test'></app-select>`,
      {
        imports: [SharedModule],
        declarations: [SelectComponent],
        componentProperties: { lable, options },
      },
    );

    const selectComponent = screen.getByText(lable);
    expect(selectComponent).toBeInTheDocument();
    selectComponent.click();

    await waitFor(() => expect(screen.getByText('_TEST_' + options[0].lable)).toBeInTheDocument());
    await waitFor(() => expect(screen.getByText('_TEST_' + options[1].lable)).toBeInTheDocument());
  });
});
