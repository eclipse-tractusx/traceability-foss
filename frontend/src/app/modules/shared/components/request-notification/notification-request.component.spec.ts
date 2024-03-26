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

import {LayoutModule} from '@layout/layout.module';
import {OtherPartsModule} from '@page/other-parts/other-parts.module';
import {NotificationType} from '@shared/model/notification.model';
import {SharedModule} from '@shared/shared.module';
import {fireEvent, screen, waitFor} from '@testing-library/angular';
import {renderComponent} from '@tests/test-render.utils';
import {sleepForTests} from '../../../../../test';
import {RequestNotificationComponent} from '@shared/components/request-notification/request-notification.component';
import {NotificationService} from "@shared/service/notification.service";
import { of } from 'rxjs';


describe('requestNotificationComponent', () => {
  let deselectPartMock: jasmine.Spy<jasmine.Func>;
  let clearSelectedMock: jasmine.Spy<jasmine.Func>;
  let submittedMock: jasmine.Spy<jasmine.Func>;
  let notificationServiceMock: jasmine.SpyObj<NotificationService>; // Assuming your service is named NotificationService

  const currentSelectedItems = [ { nameAtManufacturer: 'part_1' }, { nameAtManufacturer: 'part_2' }, { nameAtManufacturer: 'part_3' } ];

  const renderRequestNotificationComponent = async (notificationType: NotificationType) => {
    return renderComponent(
      `<app-notification-request
        (deselectPart)='deselectPartMock($event)'
        (clearSelected)='clearSelectedMock($event)'
        (submitted)='submittedMock($event)'
        [selectedItems]='currentSelectedItems'
        [notificationType]="notificationType"
        ></app-notification-request>`,
      {
        declarations: [ RequestNotificationComponent ],
        imports: [ SharedModule, LayoutModule, OtherPartsModule ],
        translations: [ 'page.otherParts', 'partDetail' ],
        componentProperties: {
          deselectPartMock,
          clearSelectedMock,
          submittedMock,
          currentSelectedItems,
          notificationType,
        },
      },
    );
  };

  beforeEach(() => {
    deselectPartMock = jasmine.createSpy();
    clearSelectedMock = jasmine.createSpy();
    submittedMock = jasmine.createSpy();
    notificationServiceMock = jasmine.createSpyObj('NotificationService', ['createAlert' /* add more methods as needed */]);
  });

  describe('Request Investigation', () => {
    it('should render', async () => {
      await renderRequestNotificationComponent(NotificationType.INVESTIGATION);
      await shouldRender('requestInvestigations');

    });

    it('should render parts in chips', async () => {
      await renderRequestNotificationComponent(NotificationType.INVESTIGATION);
      await shouldRenderPartsInChips();
    });

    it('should render textarea', async () => {
      await renderRequestNotificationComponent(NotificationType.INVESTIGATION);
      await shouldRenderTextarea();
    });

    it('should render buttons', async () => {
      await renderRequestNotificationComponent(NotificationType.INVESTIGATION);
      await shouldRenderButtons();
    });

    it('should submit alert', async () => {
      await renderRequestNotificationComponent(NotificationType.INVESTIGATION);
      await shouldSubmitParts();
    });

  });

  describe('Request Alert', () => {
    it('should render', async () => {
      await renderRequestNotificationComponent(NotificationType.ALERT);
      await shouldRender('requestAlert');
    });

    it('should render parts in chips', async () => {
      await renderRequestNotificationComponent(NotificationType.ALERT);
      await shouldRenderPartsInChips();
    });

    it('should render textarea', async () => {
      await renderRequestNotificationComponent(NotificationType.ALERT);
      await shouldRenderTextarea();
    });

    it('should render buttons', async () => {
      await renderRequestNotificationComponent(NotificationType.ALERT);
      await shouldRenderButtons();
    });
    it('should submit alert', async () => {
      await renderRequestNotificationComponent(NotificationType.ALERT);
      await shouldSubmitParts(true);
    });

  });

  const shouldRender = async (context: string) => {
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
  const shouldSubmitParts = async (shouldFillBpn = false) => {
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

    await sleepForTests(2000);
    debugger;
    expect(textArea.value).toEqual('');
  };
});
