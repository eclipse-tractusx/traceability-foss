/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

import { ActivatedRoute } from '@angular/router';
import { NotificationDetailComponent } from '@page/notifications/detail/notification-detail.component';
import { NotificationsModule } from '@page/notifications/notifications.module';
import { NotificationService } from '@shared/service/notification.service';
import { screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { of } from 'rxjs';

describe('NotificationDetailComponent', () => {

  const renderNotificationDetail = async (id?: string) => {
    return await renderComponent(NotificationDetailComponent, {
      imports: [ NotificationsModule ],
      providers: [
        NotificationService,
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: () => id || 'id-2',
              },
            },
            queryParams: of({ pageNumber: 0, tabIndex: 0 }),
          },
        },
      ],
    });
  };


  it('should render specific text for queued or requested notifications', async () => {
    await renderNotificationDetail('id-1');
    await waitFor(() => expect(screen.getByText('pageAlert.subHeadline.supplierParts')).toBeInTheDocument());
  });

  it('should render specific text for back button', async () => {
    await renderNotificationDetail('id-1');
    await waitFor(() => expect(screen.getByText('actions.goBack')).toBeInTheDocument());
  });

});
