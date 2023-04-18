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
import { SharedModule } from '@shared/shared.module';
import { fireEvent, screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { sleepForTests } from '../../../../../test';
import { RequestInvestigationComponent } from '@shared/components/request-notification/request-investigation.component';
import { RequestAlertComponent } from '@shared/components/request-notification/request-alert.component';
import { RequestContext } from '@shared/components/request-notification/request-notification.base';

describe('requestInvestigationComponent', () => {
  let deselectPartMock: jasmine.Spy<jasmine.Func>;
  let clearSelectedMock: jasmine.Spy<jasmine.Func>;
  let submittedMock: jasmine.Spy<jasmine.Func>;
  const currentSelectedItems = [{ name: 'part_1' }, { name: 'part_2' }, { name: 'part_3' }];

  const renderRequestInvestigationComponent = async () => {
    return renderComponent(
      `<app-request-investigation
        (deselectPart)='deselectPartMock($event)'
        (clearSelected)='clearSelectedMock($event)'
        (submitted)='submittedMock($event)'
        [selectedItems]='currentSelectedItems'
        ></app-request-investigation>`,
      {
        declarations: [RequestInvestigationComponent],
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
  };

  const renderRequestAlertComponent = async () => {
    return renderComponent(
      `<app-request-alert
        (deselectPart)='deselectPartMock($event)'
        (clearSelected)='clearSelectedMock($event)'
        (submitted)='submittedMock($event)'
        [selectedItems]='currentSelectedItems'
        ></app-request-alert>`,
      {
        declarations: [RequestAlertComponent],
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
  };

  beforeEach(() => {
    deselectPartMock = jasmine.createSpy();
    clearSelectedMock = jasmine.createSpy();
    submittedMock = jasmine.createSpy();
  });

  describe('Request Investigation', () => {
    it('should render', async () => {
      await renderRequestInvestigationComponent();
      await shouldRender('requestInvestigations');
    });

    it('should render parts in chips', async () => {
      await renderRequestInvestigationComponent();
      await shouldRenderPartsInChips();
    });

    it('should render textarea', async () => {
      await renderRequestInvestigationComponent();
      await shouldRenderTextarea();
    });

    it('should render buttons', async () => {
      await renderRequestInvestigationComponent();
      await shouldRenderButtons();
    });

    it('should submit parts', async () => {
      await renderRequestInvestigationComponent();
      await shouldSubmitParts('requestInvestigations');
    });
  });

  describe('Request Alert', () => {
    it('should render', async () => {
      await renderRequestAlertComponent();
      await shouldRender('requestAlert');
    });

    it('should render parts in chips', async () => {
      await renderRequestAlertComponent();
      await shouldRenderPartsInChips();
    });

    it('should render textarea', async () => {
      await renderRequestAlertComponent();
      await shouldRenderTextarea();
    });

    it('should render buttons', async () => {
      await renderRequestAlertComponent();
      await shouldRenderButtons();
    });

    it('should submit parts', async () => {
      await renderRequestAlertComponent();
      await shouldSubmitParts('requestAlert', true);
    });
  });

  const shouldRender = async (context: RequestContext) => {
    const headline = await waitFor(() => screen.getByText(context + '.headline'), { timeout: 2000 });
    expect(headline).toBeInTheDocument();
  };

  const shouldRenderPartsInChips = async () => {
    const part_1 = await waitFor(() => screen.getByText('part_1'));
    const part_2 = await screen.getByText('part_2');
    const part_3 = await screen.getByText('part_3');

    expect(part_1).toBeInTheDocument();
    expect(part_2).toBeInTheDocument();
    expect(part_3).toBeInTheDocument();
  };

  const shouldRenderTextarea = async () => {
    const textAreaElement = await waitFor(() => screen.getByText('requestNotification.textAreaLabel'));

    expect(textAreaElement).toBeInTheDocument();
  };

  const shouldRenderButtons = async () => {
    const cancelElement = await waitFor(() => screen.getByText('requestNotification.cancel'));
    const submitElement = await screen.getByText('requestNotification.submit');

    expect(cancelElement).toBeInTheDocument();
    expect(submitElement).toBeInTheDocument();
  };

  const shouldSubmitParts = async (context: RequestContext, shouldFillBpn = false) => {
    const testText = 'This is for a testing purpose.';
    const textArea = (await waitFor(() => screen.getByTestId('BaseInputElement-1'))) as HTMLTextAreaElement;
    fireEvent.input(textArea, { target: { value: testText } });

    if (shouldFillBpn) {
      const bpnInput = (await waitFor(() => screen.getByTestId('BaseInputElement-3'))) as HTMLTextAreaElement;
      fireEvent.input(bpnInput, { target: { value: 'BPNA0123TEST0123' } });
    }

    const submit = await waitFor(() => screen.getByText('requestNotification.submit'));
    expect(submit).toBeInTheDocument();
    expect(textArea.value).toEqual(testText);
    fireEvent.click(submit);

    await sleepForTests(1000);
    expect(await waitFor(() => screen.getByText(context + '.success'))).toBeInTheDocument();
    expect(textArea.value).toEqual('');
    expect(submittedMock).toHaveBeenCalledTimes(1);
  };
});
