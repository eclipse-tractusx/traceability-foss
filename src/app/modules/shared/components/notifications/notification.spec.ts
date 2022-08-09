/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { TestBed } from '@angular/core/testing';
import { SharedModule } from '@shared/shared.module';
import { screen, within } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { NotificationService } from './notification.service';

describe('notifications', () => {
  const renderNotificationLayout = async () => {
    await renderComponent(
      `
      <app-notification-container></app-notification-container>
    `,
      {
        imports: [SharedModule],
      },
    );

    return TestBed.inject(NotificationService);
  };

  it('should render success notification', async () => {
    const notificationService = await renderNotificationLayout();

    notificationService.success('some success');

    const notification = await screen.findByTestId('notification-container');

    expect(notification).toHaveClass('status-bar-success');
    expect(within(notification).getByText('some success')).toBeInTheDocument();
  });

  it('should render info notification', async () => {
    const notificationService = await renderNotificationLayout();

    notificationService.info('some info');

    const notification = await screen.findByTestId('notification-container');

    expect(notification).toHaveClass('status-bar-informative');
    expect(within(notification).getByText('some info')).toBeInTheDocument();
  });

  it('should render warning notification', async () => {
    const notificationService = await renderNotificationLayout();

    notificationService.warning('some warning');

    const notification = await screen.findByTestId('notification-container');

    expect(notification).toHaveClass('status-bar-warning');
    expect(within(notification).getByText('some warning')).toBeInTheDocument();
  });

  it('should render error notification', async () => {
    const notificationService = await renderNotificationLayout();

    notificationService.error('some error');

    const notification = await screen.findByTestId('notification-container');

    expect(notification).toHaveClass('status-bar-error');
    expect(within(notification).getByText('some error')).toBeInTheDocument();
  });
});
