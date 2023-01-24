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
import { InvestigationsFacade } from '@page/investigations/core/investigations.facade';
import { Notification, NotificationStatus } from '@shared/model/notification.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class InvestigationHelperService {
  constructor(private readonly roleService: RoleService, private readonly investigationsFacade: InvestigationsFacade) {}

  public approve(id: string): Observable<void> {
    return this.investigationsFacade.approveInvestigation(id);
  }

  public cancel(id: string): Observable<void> {
    return this.investigationsFacade.cancelInvestigation(id);
  }

  public close(id: string, reason: string): Observable<void> {
    return this.investigationsFacade.closeInvestigation(id, reason);
  }

  public acknowledge(id: string): Observable<void> {
    return this.investigationsFacade.acknowledgeInvestigation(id);
  }

  public accept(id: string, reason: string): Observable<void> {
    return this.investigationsFacade.acceptInvestigation(id, reason);
  }

  public decline(id: string, reason: string): Observable<void> {
    return this.investigationsFacade.declineInvestigation(id, reason);
  }

  public showApproveButton({ status, isFromSender } = {} as Notification): boolean {
    return isFromSender && status === NotificationStatus.CREATED && this.roleService.isAtLeastSupervisor();
  }

  public showCancelButton({ status, isFromSender } = {} as Notification): boolean {
    return isFromSender && status === NotificationStatus.CREATED && this.roleService.isAtLeastSupervisor();
  }

  public showCloseButton({ status, isFromSender } = {} as Notification): boolean {
    const disallowedStatus = [NotificationStatus.CREATED, NotificationStatus.CLOSED, NotificationStatus.CANCELED];
    return isFromSender && !disallowedStatus.includes(status) && this.roleService.isAtLeastSupervisor();
  }
  public showAcknowledgeButton({ status, isFromSender } = {} as Notification): boolean {
    return !isFromSender && status === NotificationStatus.RECEIVED && this.roleService.isAtLeastSupervisor();
  }
  public showAcceptButton({ status, isFromSender } = {} as Notification): boolean {
    return !isFromSender && status === NotificationStatus.ACKNOWLEDGED && this.roleService.isAtLeastSupervisor();
  }
  public showDeclineButton({ status, isFromSender } = {} as Notification): boolean {
    return !isFromSender && status === NotificationStatus.ACKNOWLEDGED && this.roleService.isAtLeastSupervisor();
  }
}
