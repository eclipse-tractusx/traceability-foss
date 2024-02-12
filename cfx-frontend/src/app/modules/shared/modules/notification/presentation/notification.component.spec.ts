// /********************************************************************************
//  * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
//  * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
//  * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
//  *
//  * See the NOTICE file(s) distributed with this work for additional
//  * information regarding copyright ownership.
//  *
//  * This program and the accompanying materials are made available under the
//  * terms of the Apache License, Version 2.0 which is available at
//  * https://www.apache.org/licenses/LICENSE-2.0.
//  *
//  * Unless required by applicable law or agreed to in writing, software
//  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
//  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
//  * License for the specific language governing permissions and limitations
//  * under the License.
//  *
//  * SPDX-License-Identifier: Apache-2.0
//  ********************************************************************************/

// import { CalendarDateModel } from '@core/model/calendar-date.model';
// import {
//   Notification,
//   NotificationResponse,
//   Notifications,
//   NotificationStatus,
//   NotificationType,
// } from '@shared/model/notification.model';
// import { View } from '@shared/model/view.model';
// import {
//   FormatPartSemanticDataModelToCamelCasePipe
// } from '@shared/pipes/format-part-semantic-data-model-to-camelcase.pipe';
// import { SharedModule } from '@shared/shared.module';
// import { TemplateModule } from '@shared/template.module';
// import { fireEvent, screen, within } from '@testing-library/angular';
// import { renderComponent } from '@tests/test-render.utils';
// import { Observable, of } from 'rxjs';
// import { delay } from 'rxjs/operators';
// import { buildMockInvestigations } from '../../../../../mocks/services/investigations-mock/investigations.test.model';
// import { NotificationModule } from '../notification.module';
// import { NotificationChannel } from "@shared/components/multi-select-autocomplete/table-type.model";

//  TODO fix tests
// describe('NotificationsInboxComponent', () => {
//   let clickHandler;

//   beforeEach(() => (clickHandler = jasmine.createSpy()));

//   const mapNotificationResponse = (data: NotificationResponse): Notification => {
//     const isFromSender = data.channel === NotificationChannel.SENDER;
//     const createdDate = new CalendarDateModel(data.createdDate);
//     const targetDate = new CalendarDateModel(data.targetDate);
//     const createdBy = data.createdBy
//     const createdByName = data.createdByName
//     const sendTo = data.sendTo
//     const sendToName = data.sendToName;
//     delete data.channel;

//     return { ...data, createdDate, targetDate, isFromSender, createdBy, createdByName, sendTo, sendToName };
//   };

//   const renderNotificationsInbox = () => {
//     const qContent = buildMockInvestigations([NotificationStatus.CREATED], 'SENDER').map(mapNotificationResponse);
//     const qarContent = buildMockInvestigations([NotificationStatus.RECEIVED], 'RECEIVER').map(mapNotificationResponse);

//     const queuedAndRequestedNotifications$: Observable<View<Notifications>> = of({
//       data: { content: qContent, page: 0, pageCount: 1, pageSize: 5, totalItems: 1 },
//     }).pipe(delay(0));

//     const receivedNotifications$: Observable<View<Notifications>> = of({
//       data: { content: qarContent, page: 0, pageCount: 1, pageSize: 5, totalItems: 1 },
//     }).pipe(delay(0));
//     const menuActionsConfig = [];
//     const notificationType = NotificationType.INVESTIGATION;
//     const isInvestigation = true;
//     return renderComponent(
//       `<app-notification
//           [notificationType]='notificationType'
//           [queuedAndRequestedNotifications$]='queuedAndRequestedNotifications$'
//           [receivedNotifications$]='receivedNotifications$'
//           [translationContext]="'commonInvestigation'"
//           [menuActionsConfig]="'menuActionsConfig'"
//           [isInvestigation]='isInvestigation'
//            [receivedOptionalColumns]="['severity', 'createdBy', 'createdByName', 'targetDate']"
//   [receivedSortableColumns]="{description: true, status: true, createdDate: true, severity: true, createdBy: true, createdByName: true, targetDate: true, menu: false}"
//   [queuedAndRequestedOptionalColumns]="['severity', 'sendTo', 'sendToName', 'targetDate']"
//   [queuedAndRequestedSortableColumns]="{description: true, status: true, createdDate: true, severity: true, sendTo: true, sendToName: true, targetDate: true, menu: false}"
//           (onReceivedPagination)='clickHandler($event)'
//           (onQueuedAndRequestedPagination)='clickHandler($event)'
//         ></app-notification>`,
//       {
//         imports: [SharedModule, NotificationModule, TemplateModule],
//         providers: [FormatPartSemanticDataModelToCamelCasePipe],
//         translations: ['common'],
//         componentProperties: {
//           queuedAndRequestedNotifications$,
//           receivedNotifications$,
//           clickHandler,
//           menuActionsConfig,
//           notificationType,
//           isInvestigation
//         },
//       },
//     );
//   };

//   // TODO fix test
//   // it('should render received notifications', async () => {
//   //   const component = await renderNotificationsInbox();
//   //   component.detectChanges();
//   //   expect(await screen.findByText('Investigation No 1')).toBeInTheDocument();
//   // });

//   // TODO fix test
//   // it('should render received notifications with date and status', async () => {
//   //   await renderNotificationsInbox();

//   //   const descriptionEl = await screen.findByText('Investigation No 1');
//   //   const row = descriptionEl.closest('tr');

//   //   expect(within(row).getByText('commonInvestigation.status.RECEIVED')).toBeInTheDocument();
//   // });

// });
