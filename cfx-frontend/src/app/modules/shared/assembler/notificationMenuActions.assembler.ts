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
import { NotificationActionHelperService } from '@shared/assembler/notification-action-helper.service';
import { NotificationCommonModalComponent } from '@shared/components/notification-common-modal/notification-common-modal.component';
import { MenuActionConfig } from '@shared/components/table/table.model';
import { Notification, NotificationStatus } from '@shared/model/notification.model';
import { NotificationAction } from '@shared/modules/notification/notification-action.enum';

export class NotificationMenuActionsAssembler {
  public static getMenuActions(helperService: NotificationActionHelperService, modal: NotificationCommonModalComponent): MenuActionConfig<Notification>[] {
    return [
      {
        label: 'actions.close',
        icon: 'close',
        action: data => modal.show(NotificationStatus.CLOSED, data),
        condition: data => helperService.showCloseButton(data),
        isAuthorized: helperService.isAuthorizedForButton(NotificationAction.CLOSE),
      },
      {
        label: 'actions.approve',
        icon: 'share',
        action: data => modal.show(NotificationStatus.APPROVED, data),
        condition: data => helperService.showApproveButton(data),
        isAuthorized: helperService.isAuthorizedForButton(NotificationAction.APPROVE),
      },
      {
        label: 'actions.cancel',
        icon: 'cancel',
        action: data => modal.show(NotificationStatus.CANCELED, data),
        condition: data => helperService.showCancelButton(data),
        isAuthorized: helperService.isAuthorizedForButton(NotificationAction.CANCEL),
      },
      {
        label: 'actions.acknowledge',
        icon: 'work',
        action: data => modal.show(NotificationStatus.ACKNOWLEDGED, data),
        condition: data => helperService.showAcknowledgeButton(data),
        isAuthorized: helperService.isAuthorizedForButton(NotificationAction.ACKNOWLEDGE),
      },
      {
        label: 'actions.accept',
        icon: 'assignment_turned_in',
        action: data => modal.show(NotificationStatus.ACCEPTED, data),
        condition: data => helperService.showAcceptButton(data),
        isAuthorized: helperService.isAuthorizedForButton(NotificationAction.ACCEPT),
      },
      {
        label: 'actions.decline',
        icon: 'assignment_late',
        action: data => modal.show(NotificationStatus.DECLINED, data),
        condition: data => helperService.showDeclineButton(data),
        isAuthorized: helperService.isAuthorizedForButton(NotificationAction.DECLINE),
      },
    ];
  }
}
