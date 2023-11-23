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

import { CalendarDateModel } from '@core/model/calendar-date.model';
import {
  Notification,
  NotificationResponse,
  Notifications,
  NotificationStatus,
} from '@shared/model/notification.model';
import { View } from '@shared/model/view.model';
import { SharedModule } from '@shared/shared.module';
import { TemplateModule } from '@shared/template.module';
import { fireEvent, screen, within, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';
import { buildMockInvestigations } from '../../../../../mocks/services/investigations-mock/investigations.test.model';
import { NotificationModule } from '../notification.module';
import { PartTableType } from '@shared/components/table/table.model';

describe('NotificationsInboxComponent', () => {
  let clickHandler;

  beforeEach(() => (clickHandler = jasmine.createSpy()));

  const mapNotificationResponse = (data: NotificationResponse): Notification => {
    const isFromSender = data.channel === 'SENDER';
    const createdDate = new CalendarDateModel(data.createdDate);
    const targetDate = new CalendarDateModel(data.targetDate);
    const createdBy = { bpn: data.createdBy, name: data.createdByName };
    const sendTo = { bpn: data.createdBy, name: data.createdByName };
    delete data.channel;

    return { ...data, createdDate, targetDate, isFromSender, createdBy, sendTo };
  };

  const renderNotificationsInbox = () => {
    const qContent = buildMockInvestigations([NotificationStatus.CREATED], 'SENDER').map(mapNotificationResponse);
    const qarContent = buildMockInvestigations([NotificationStatus.RECEIVED], 'RECEIVER').map(mapNotificationResponse);

    const queuedAndRequestedNotifications$: Observable<View<Notifications>> = of({
      data: { content: qContent, page: 0, pageCount: 1, pageSize: 5, totalItems: 1 },
    }).pipe(delay(0));

    const receivedNotifications$: Observable<View<Notifications>> = of({
      data: { content: qarContent, page: 0, pageCount: 1, pageSize: 5, totalItems: 1 },
    }).pipe(delay(0));
    const menuActionsConfig = [];
    const multiSortList = ['description', 'asc'];

    return renderComponent(
      `<app-notification
          [queuedAndRequestedNotifications$]='queuedAndRequestedNotifications$'
          [receivedNotifications$]='receivedNotifications$'
          [translationContext]="'commonInvestigation'"
          [menuActionsConfig]='menuActionsConfig'
          (onReceivedPagination)='clickHandler($event)'
          [tablesType]='tablesType'
          (onQueuedAndRequestedPagination)='clickHandler($event)'
        ></app-notification>`,
      {
        imports: [SharedModule, NotificationModule, TemplateModule],
        translations: ['common'],
        componentProperties: {
          multiSortList,
          queuedAndRequestedNotifications$,
          receivedNotifications$,
          clickHandler,
          menuActionsConfig,
          tablesType: [PartTableType.INVESTIGATIONS_RECEIVED, PartTableType.INVESTIGATIONS_SENT],
        },
      },
    );
  };

  it('should render received notifications', async () => {
    const component = await renderNotificationsInbox();
    component.detectChanges();
    expect(await screen.findByText('Investigation No 1', undefined, { timeout: 10000 })).toBeInTheDocument();
  });

  it('should render received notifications with date and status', async () => {
    await renderNotificationsInbox();
    const descriptionEl = await screen.findByText('Investigation No 1', undefined, { timeout: 10000 });
    const row = descriptionEl.closest('tr');

    expect(within(row).getByText('commonInvestigation.status.RECEIVED')).toBeInTheDocument();
  });

  it('should be able to change notifications page', async () => {
    await renderNotificationsInbox();
    fireEvent.click(
      await waitFor(() => screen.getByLabelText('pagination.nextPageLabel'), {
        timeout: 10000,
      }),
    );

    expect(await screen.findByText('Investigation No 51', undefined, { timeout: 10000 })).toBeInTheDocument();
  });

  it('should render queued & requested notifications', async () => {
    await renderNotificationsInbox();

    fireEvent.click(
      await waitFor(() => screen.getByText('commonInvestigation.tabs.queuedAndRequested'), {
        timeout: 10000,
      }),
    );
    expect(await screen.findByText('Investigation No 1', undefined, { timeout: 10000 })).toBeInTheDocument();
  });
});
