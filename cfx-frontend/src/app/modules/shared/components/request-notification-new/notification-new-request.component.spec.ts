/********************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
import {NotificationDetailFacade} from '@page/notifications/core/notification-detail.facade';
import {NotificationDetailState} from '@page/notifications/core/notification-detail.state';
import { NotificationAssembler } from '@shared/assembler/notification.assembler';
import {
  RequestNotificationNewComponent
} from '@shared/components/request-notification-new/notification-new-request.component';
import {Notification, NotificationType} from '@shared/model/notification.model';
import {View} from '@shared/model/view.model';
import {
  FormatPartlistSemanticDataModelToCamelCasePipe
} from '@shared/pipes/format-partlist-semantic-data-model-to-camelcase.pipe';
import {SharedModule} from '@shared/shared.module';
import {screen, waitFor} from '@testing-library/angular';
import {renderComponent} from '@tests/test-render.utils';
import {of} from 'rxjs';
import { MockEmptyAlert } from '../../../../mocks/services/alerts-mock/alerts.test.model';


describe('requestNotificationNewComponent', () => {
  let notificationDetailFacadeMock: jasmine.SpyObj<NotificationDetailFacade>; // Assuming your service is named NotificationService
  let formGroupChangedMock: jasmine.Spy<jasmine.Func>;

  const currentSelectedItems = [ { nameAtManufacturer: 'part_1' }, { nameAtManufacturer: 'part_2' }, { nameAtManufacturer: 'part_3' } ];

  const renderRequestNotificationComponent = async (editMode: boolean, title: string, notification: Notification) => {
    return renderComponent(
      `<app-notification-new-request
        (formGroupChanged)='formGroupChangedMock($event)'
        [editMode]='editMode'
        [title]="title"
        [notification]="notification"
        ></app-notification-new-request>`,
      {
        declarations: [ RequestNotificationNewComponent ],
        imports: [ SharedModule, LayoutModule ],
        translations: [ 'common' ],
        componentProperties: {
          notificationDetailFacadeMock,
          formGroupChangedMock,
          currentSelectedItems,
          title, editMode, notification
        },
        providers: [
          { provide: NotificationDetailFacade },
          { provide: NotificationDetailState },
          { provide: FormatPartlistSemanticDataModelToCamelCasePipe },
        ],
      },
    );
  };

  beforeEach(() => {

    const notification: View<Notification> = {
      data: {
        assetIds: [],
        createdBy: '',
        type: NotificationType.ALERT,
        createdByName: '',
        createdDate: undefined,
        description: '',
        isFromSender: false,
        reason: undefined,
        sendTo: '',
        sendToName: '',
        severity: undefined,
        status: undefined,
        title: '',
        id: 'abc',
      }
    };

    const notificationDetailFacadeMock = jasmine.createSpyObj('notificationDetailFacade', [ 'selected$' ]);
    notificationDetailFacadeMock.selected$.and.returnValue(of({ notification }));

  });

  describe('Request Investigation', () => {
    it('should render edit mode', async () => {
      await renderRequestNotificationComponent(true, 'edit', NotificationAssembler.assembleNotification(MockEmptyAlert));
      const headline = await waitFor(() => screen.getByText('edit'), { timeout: 2000 });
      expect(headline).toBeInTheDocument();
    });
  });
});
