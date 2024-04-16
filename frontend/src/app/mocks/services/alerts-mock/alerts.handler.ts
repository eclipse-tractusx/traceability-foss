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

import { environment } from '@env';
import { NotificationStatus } from '@shared/model/notification.model';
import { rest } from 'msw';
import { applyPagination, extractPaginationOfNotifications } from '../pagination.helper';
import { buildMockAlerts, getAlertById } from './alerts.model';
import {
  AlertIdPrefix as testAlertIdPrefix,
  buildMockAlerts as testBuildMockAlerts,
  getAlertById as testGetAlertById,
} from './alerts.test.model';

const commonHandler = [
  rest.post(`*${ environment.apiUrl }/notifications/:notificationId/close`, (req, res, ctx) => {
    return res(ctx.status(204));
  }),

  rest.post(`*${ environment.apiUrl }/notifications/:notificationId/approve`, (req, res, ctx) => {
    return res(ctx.status(400), ctx.json({message: "Failed to send alert to EDC"}));
  }),

  rest.post(`*${ environment.apiUrl }/notifications/:notificationId/cancel`, (req, res, ctx) => {
    return res(ctx.status(204));
  }),

  rest.post(`${ environment.apiUrl }/notifications/:notificationId/update`, (req, res, ctx) => {
    return res(ctx.status(204));
  }),
];

export const alertsHandlers = [
  rest.post(`*${ environment.apiUrl }/notifications/filter`, (req, res, ctx) => {
    const pagination = extractPaginationOfNotifications(req);

    const currentStatus = [
      NotificationStatus.CREATED,
      NotificationStatus.SENT,
      NotificationStatus.ACKNOWLEDGED,
      NotificationStatus.ACCEPTED,
      NotificationStatus.DECLINED,
      NotificationStatus.CLOSED,
      NotificationStatus.CANCELED,
    ];

    return res(ctx.status(200), ctx.json(applyPagination(buildMockAlerts(currentStatus, 'SENDER'), pagination)));
  }),

  rest.post(`*${ environment.apiUrl }/notifications/filter`, (req, res, ctx) => {
    const pagination = extractPaginationOfNotifications(req);

    const currentStatus = [
      NotificationStatus.RECEIVED,
      NotificationStatus.ACKNOWLEDGED,
      NotificationStatus.ACCEPTED,
      NotificationStatus.DECLINED,
      NotificationStatus.CLOSED,
      NotificationStatus.CANCELED,
    ];
    return res(ctx.status(200), ctx.json(applyPagination(buildMockAlerts(currentStatus, 'RECEIVER'), pagination)));
  }),

  rest.get(`*${ environment.apiUrl }/notifications/:notificationId`, (req, res, ctx) => {
    const { alertId } = req.params;

    const indexFromId = parseInt((alertId as string).replace('id-', ''), 10);

    const statusCollection = [
      NotificationStatus.CREATED,
      NotificationStatus.SENT,
      NotificationStatus.RECEIVED,
      NotificationStatus.CLOSED,
      NotificationStatus.CANCELED,
      NotificationStatus.ACKNOWLEDGED,
      NotificationStatus.ACCEPTED,
      NotificationStatus.DECLINED,

      NotificationStatus.ACKNOWLEDGED,
      NotificationStatus.ACCEPTED,
      NotificationStatus.DECLINED,
      NotificationStatus.CLOSED,
      NotificationStatus.CANCELED,
    ];
    const channel = [ 2, 8, 9, 10, 11, 12 ].includes(indexFromId) ? 'RECEIVER' : 'SENDER';
    const randomNotification = buildMockAlerts([ statusCollection[indexFromId] ], channel)[0];

    return res(ctx.status(200), ctx.json({ ...randomNotification, id: alertId }));
  }),
  rest.post(`*${ environment.apiUrl }/notifications`, (_, res, ctx) => {
    return res(ctx.status(400), ctx.json({message: "Error while sending Alert to EDC"} ));
    //return res(ctx.status(200), ctx.json({ id: AlertIdPrefix + 1 }));
  }),

  rest.put(`*${ environment.apiUrl }/notifications/:notificationId/status`, async (req, res, ctx) => {
    const { alertId } = req.params;
    const { status } = await req.json();

    const alert = getAlertById(alertId as string);
    return res(ctx.status(200), ctx.json({ ...alert, status }));
  }),
  ...commonHandler,
];

export const alertsTestHandlers = [
  rest.post(`*${ environment.apiUrl }/notifications/filter`, (req, res, ctx) => {
    const pagination = extractPaginationOfNotifications(req);

    const currentStatus = [
      NotificationStatus.CREATED,
      NotificationStatus.SENT,
      NotificationStatus.ACKNOWLEDGED,
      NotificationStatus.ACCEPTED,
      NotificationStatus.DECLINED,
    ];

    return res(ctx.status(200), ctx.json(applyPagination(testBuildMockAlerts(currentStatus, 'SENDER'), pagination)));
  }),

  rest.post(`*${ environment.apiUrl }/notifications/filter`, (req, res, ctx) => {
    const pagination = extractPaginationOfNotifications(req);

    const currentStatus = [ NotificationStatus.RECEIVED, NotificationStatus.ACKNOWLEDGED ];
    return res(ctx.status(200), ctx.json(applyPagination(testBuildMockAlerts(currentStatus, 'RECEIVER'), pagination)));
  }),

  rest.get(`*${ environment.apiUrl }/notifications/:notificationId`, (req, res, ctx) => {
    const { alertId } = req.params;

    const indexFromId = parseInt((alertId as string).replace('id-', ''), 10);

    const statusCollection = [
      NotificationStatus.CREATED,
      NotificationStatus.SENT,
      NotificationStatus.RECEIVED,
      NotificationStatus.CLOSED,
      NotificationStatus.CANCELED,
      NotificationStatus.ACKNOWLEDGED,
      NotificationStatus.ACCEPTED,
      NotificationStatus.DECLINED,
      NotificationStatus.ACKNOWLEDGED,
    ];
    const channel = indexFromId === 2 || indexFromId === 8 ? 'RECEIVER' : 'SENDER';
    const randomNotification = testBuildMockAlerts([ statusCollection[indexFromId] ], channel)[0];

    return res(ctx.status(200), ctx.json({ ...randomNotification, id: alertId }));
  }),
  rest.post(`*${ environment.apiUrl }/notifications`, (_, res, ctx) => {
    return res(ctx.status(200), ctx.json({ id: testAlertIdPrefix + 1 }));
  }),

  rest.put(`*${ environment.apiUrl }/notifications/:notificationId/status`, async (req, res, ctx) => {
    const { alertId } = req.params;
    const { status } = await req.json();

    const alert = testGetAlertById(alertId as string);
    return res(ctx.status(200), ctx.json({ ...alert, status }));
  }),
  ...commonHandler,
];
