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
import { Component, EventEmitter, Input, Optional, Output, ViewChild } from '@angular/core';
import { NotificationHelperService } from '@page/notifications/core/notification-helper.service';
import { NotificationsFacade } from '@page/notifications/core/notifications.facade';
import { Notification, NotificationStatus } from '@shared/model/notification.model';
import { NotificationActionModalComponent } from '@shared/modules/notification/modal/actions/notification-action-modal.component';

@Component({
  selector: 'app-notification-common-modal',
  templateUrl: './notification-common-modal.component.html',
})

export class NotificationCommonModalComponent {
  @Input() selectedNotification: Notification;
  @Input() helperService: NotificationHelperService;
  @Output() confirmActionCompleted = new EventEmitter<void>();

  @ViewChild(NotificationActionModalComponent) notificationActionModalComponent: NotificationActionModalComponent;


// TODO do not delete the facade here. This will lead to a nullpointer exception within the modal call.
  public constructor(
    @Optional() private readonly notificationsFacade: NotificationsFacade,
  ) {
  }


  public handleModalConfirmActionCompletedEvent(): void {
    this.confirmActionCompleted.emit();
  }

  public show(desiredStatus: NotificationStatus, notification?: Notification) {
    let notificationToShow = notification || this.selectedNotification;
    this.notificationActionModalComponent.show(notificationToShow, desiredStatus);
  }

  protected readonly NotificationStatus = NotificationStatus;
}
