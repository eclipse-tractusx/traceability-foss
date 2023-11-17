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

import { AfterViewInit, Component, EventEmitter, Input, Output, TemplateRef, ViewChild } from '@angular/core';
import {
  CreateHeaderFromColumns,
  DisplayColumns,
  MenuActionConfig,
  TableConfig,
  TableEventConfig,
  TableHeaderSort,
} from '@shared/components/table/table.model';
import { Notification, Notifications } from '@shared/model/notification.model';
import { View } from '@shared/model/view.model';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-notifications-tab',
  templateUrl: './notification-tab.component.html',
  styleUrls: ['./notification-tab.component.scss'],
})
export class NotificationTabComponent implements AfterViewInit {
  @Input() notificationsView$: Observable<View<Notifications>>;
  @Input() labelId: string;
  @Input() hasPagination = true;
  @Input() translationContext: 'commonInvestigation' | 'commonAlert';
  @Input() menuActionsConfig: MenuActionConfig<Notification>[];
  @Input() optionalColumns: Array<'targetDate' | 'severity' | 'createdBy' | 'sendTo'> = [];
  @Input() sortableColumns: Record<string, boolean> = {};
  @Input() multiSortList: TableHeaderSort[] = [];
  @Input() enableScroll: boolean = true;
  @Input() filterConfig: any[] = [];

  @Output() tableConfigChanged = new EventEmitter<TableEventConfig>();
  @Output() selected = new EventEmitter<Notification>();
  @Output() itemCount = new EventEmitter<number>();

  @ViewChild('idTmp') idTemplate: TemplateRef<unknown>;
  @ViewChild('statusTmp') statusTemplate: TemplateRef<unknown>;
  @ViewChild('severityTmp') severityTemplate: TemplateRef<unknown>;
  @ViewChild('descriptionTmp') descriptionTemplate: TemplateRef<unknown>;
  @ViewChild('targetDateTmp') targetDateTemplate: TemplateRef<unknown>;
  @ViewChild('userTmp') userTemplate: TemplateRef<unknown>;

  public tableConfig: TableConfig<keyof Notification>;

  public ngAfterViewInit(): void {
    const defaultColumns: DisplayColumns<keyof Notification>[] = ['createdDate', 'description', 'status'];
    const displayedColumns: DisplayColumns<keyof Notification>[] = [...defaultColumns, ...this.optionalColumns, 'menu'];
    const sortableColumns: Record<string, boolean> = this.sortableColumns;
    const filterConfig: any[] = this.filterConfig;
    this.tableConfig = {
      displayedColumns,
      sortableColumns,
      filterConfig,
      header: CreateHeaderFromColumns(displayedColumns, 'table.column'),
      hasPagination: this.hasPagination,
      menuActionsConfig: this.menuActionsConfig || [],
      cellRenderers: {
        id: this.idTemplate,
        status: this.statusTemplate,
        severity: this.severityTemplate,
        description: this.descriptionTemplate,
        targetDate: this.targetDateTemplate,
        createdBy: this.userTemplate,
        sendTo: this.userTemplate,
      },
    };
  }

  public onItemCountChange(itemCount: number): void {
    this.itemCount.emit(itemCount);
  }

  public selectNotification(notification: Record<string, unknown>): void {
    this.selected.emit(notification as unknown as Notification);
  }

  public onTableConfigChange(tableEventConfig: TableEventConfig): void {
    this.tableConfigChanged.emit(tableEventConfig);
  }
}
