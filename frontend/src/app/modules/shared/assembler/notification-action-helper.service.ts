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

import { Injectable } from '@angular/core';
import { RoleService } from '@core/user/role.service';
import { Notification, NotificationStatus } from '@shared/model/notification.model';
import { NotificationAction } from '@shared/modules/notification/notification-action.enum';

@Injectable({
  providedIn: 'root',
})
export class NotificationActionHelperService {
  constructor(private readonly roleService: RoleService) {
  }

  public isAtLeastSupervisor(): boolean {
    return this.roleService.isSupervisor();
  }

  public showEditButton({ status, isFromSender } = {} as Notification): boolean {
    return isFromSender && status === NotificationStatus.CREATED;
  }

  public showApproveButton({ status, isFromSender } = {} as Notification): boolean {
    return isFromSender && status === NotificationStatus.CREATED;
  }

  public showCancelButton({ status, isFromSender } = {} as Notification): boolean {
    return isFromSender && status === NotificationStatus.CREATED;
  }

  public showCloseButton({ status, isFromSender } = {} as Notification): boolean {
    const disallowedStatus = [ NotificationStatus.CREATED, NotificationStatus.CLOSED, NotificationStatus.CANCELED ];
    return isFromSender && !disallowedStatus.includes(status);
  }

  public showAcknowledgeButton({ status, isFromSender } = {} as Notification): boolean {
    return !isFromSender && status === NotificationStatus.RECEIVED;
  }

  public showAcceptButton({ status, isFromSender } = {} as Notification): boolean {
    return !isFromSender && status === NotificationStatus.ACKNOWLEDGED;
  }

  public showDeclineButton({ status, isFromSender } = {} as Notification): boolean {
    return !isFromSender && status === NotificationStatus.ACKNOWLEDGED;
  }

  public isAllowedToEdit() {
    return this.roleService.isSupervisor() || this.roleService.isUser();
  }

  public isAuthorizedForButton(action: NotificationAction): boolean {
    if (action === NotificationAction.APPROVE || action === NotificationAction.CLOSE) {
      return this.roleService.isSupervisor();
    } else {
      return this.roleService.isUser();
    }
  }
}
