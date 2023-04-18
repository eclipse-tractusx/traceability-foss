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

import { LayoutModule } from '@layout/layout.module';
import { OtherPartsModule } from '@page/other-parts/other-parts.module';
import { RequestNotificationBase } from '@shared/components/request-notification/request-notification.base';
import { SharedModule } from '@shared/shared.module';
import { fireEvent, screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { sleepForTests } from '../../../../../test';

describe('requestInvestigationComponent', () => {
  let deselectPartMock: jasmine.Spy<jasmine.Func>;
  let clearSelectedMock: jasmine.Spy<jasmine.Func>;
  let submittedMock: jasmine.Spy<jasmine.Func>;
  const currentSelectedItems = [{ name: 'part_1' }, { name: 'part_2' }, { name: 'part_3' }];

  const renderRequestInvestigationComponent = () =>
    renderComponent(
      `<app-request-investigation
  (deselectPart)='deselectPartMock($event)'
  (clearSelected)='clearSelectedMock($event)'
  (submitted)='submittedMock($event)'
  [selectedItems]='currentSelectedItems'
></app-request-investigation>`,
      {
        declarations: [RequestNotificationBase],
        imports: [SharedModule, LayoutModule, OtherPartsModule],
        translations: ['page.otherParts', 'partDetail'],
        componentProperties: {
          deselectPartMock,
          clearSelectedMock,
          submittedMock,
          currentSelectedItems,
        },
      },
    );

  beforeEach(() => {
    deselectPartMock = jasmine.createSpy();
    clearSelectedMock = jasmine.createSpy();
    submittedMock = jasmine.createSpy();
  });

  it('should render', async () => {
    await renderRequestInvestigationComponent();
    const headline = await waitFor(() => screen.getByText('requestInvestigations.headline'), { timeout: 2000 });
    expect(headline).toBeInTheDocument();
  });

  it('should render parts in chips', async () => {
    await renderRequestInvestigationComponent();
    const part_1 = await waitFor(() => screen.getByText('part_1'));
    const part_2 = await screen.getByText('part_2');
    const part_3 = await screen.getByText('part_3');

    expect(part_1).toBeInTheDocument();
    expect(part_2).toBeInTheDocument();
    expect(part_3).toBeInTheDocument();
  });

  it('should render textarea', async () => {
    await renderRequestInvestigationComponent();
    const textAreaElement = await waitFor(() => screen.getByText('requestInvestigations.textAreaLabel'));

    expect(textAreaElement).toBeInTheDocument();
  });

  it('should render buttons', async () => {
    await renderRequestInvestigationComponent();
    const cancelElement = await waitFor(() => screen.getByText('requestInvestigations.cancel'));
    const submitElement = await screen.getByText('requestInvestigations.submit');

    expect(cancelElement).toBeInTheDocument();
    expect(submitElement).toBeInTheDocument();
  });

  it('should submit parts', async () => {
    await renderRequestInvestigationComponent();

    const testText = 'This is for a testing purpose.';
    const textArea = (await waitFor(() => screen.getByTestId('BaseInputElement-2'))) as HTMLTextAreaElement;
    fireEvent.input(textArea, { target: { value: testText } });

    const submit = await waitFor(() => screen.getByText('requestInvestigations.submit'));
    expect(submit).toBeInTheDocument();
    expect(textArea.value).toEqual(testText);
    fireEvent.click(submit);

    await sleepForTests(1000);
    expect(await waitFor(() => screen.getByText('qualityInvestigation.success'))).toBeInTheDocument();
    expect(textArea.value).toEqual('');
    expect(submittedMock).toHaveBeenCalledTimes(1);
  });
});
