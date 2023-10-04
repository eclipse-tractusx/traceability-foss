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

import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MenuActionConfig, TableEventConfig, TableHeaderSort } from '@shared/components/table/table.model';
import { Notification, Notifications } from '@shared/model/notification.model';
import { View } from '@shared/model/view.model';
import { StaticIdService } from '@shared/service/staticId.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-notification',
  templateUrl: './notification.component.html',
})
export class NotificationComponent {
  @Input() receivedNotifications$: Observable<View<Notifications>>;
  @Input() queuedAndRequestedNotifications$: Observable<View<Notifications>>;
  @Input() translationContext: 'commonInvestigation' | 'commonAlert';
  @Input() menuActionsConfig: MenuActionConfig<Notification>[];
  @Input() receivedOptionalColumns: Array<'targetDate' | 'severity' | 'createdBy'> = [];
  @Input() receivedSortableColumns: Record<string, boolean> = {};
  @Input() queuedAndRequestedOptionalColumns: Array<'targetDate' | 'severity' | 'sendTo'> = [];
  @Input() queuedAndRequestedSortableColumns: Record<string, boolean> = {};
  @Input() receivedMultiSortList: TableHeaderSort[] = [];
  @Input() queuedAndRequestedMultiSortList: TableHeaderSort[] = [];

  @Output() onReceivedTableConfigChanged = new EventEmitter<TableEventConfig>();
  @Output() onQueuedAndRequestedTableConfigChanged = new EventEmitter<TableEventConfig>();
  @Output() selected = new EventEmitter<Notification>();

  public readonly tabIndex$ = this.route.queryParams.pipe(map(params => parseInt(params.tabIndex, 10) || 0));

  public readonly receivedTabLabelId = this.staticIdService.generateId('Notification.receivedTab');
  public readonly queuedAndRequestedTabLabelId = this.staticIdService.generateId('Notification.queuedAndRequestedTab');

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly staticIdService: StaticIdService,
  ) {}

  public onTabChange(tabIndex: number): void {
    void this.router.navigate([], { queryParams: { tabIndex }, replaceUrl: true });
  }
}
