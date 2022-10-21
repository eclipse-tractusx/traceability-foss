/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
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
import { FormControl, FormGroup, Validators } from '@angular/forms';
import {
  CreateHeaderFromColumns,
  DisplayColumns,
  MenuActionConfig,
  TableConfig,
  TablePaginationEventConfig,
} from '@shared/components/table/table.model';
import { Notification, Notifications } from '@shared/model/notification.model';
import { View } from '@shared/model/view.model';
import { ModalService } from '@shared/modules/modal/core/modal.service';
import { ConfirmModalData } from '@shared/modules/modal/core/modal.model';
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
  @Input() translationContext: 'commonInvestigation' | 'pageAlerts';

  @Output() pagination = new EventEmitter<TablePaginationEventConfig>();
  @Output() selected = new EventEmitter<Notification>();

  @ViewChild('statusTmp') statusTemplate: TemplateRef<unknown>;

  @ViewChild('ModalApproval') modalApproval: TemplateRef<unknown>;
  @ViewChild('ModalDeletion') modalDeletion: TemplateRef<unknown>;

  public readonly displayedColumns: DisplayColumns<keyof Notification>[] = [
    'description',
    'status',
    'createdDate',
    'menu',
  ];

  public tableConfig: TableConfig<keyof Notification>;
  public selectedInvestigation: Notification;

  private menuActionsConfig: MenuActionConfig[] = [
    { label: 'actions.approve', icon: 'share', action: this.approveNotification.bind(this) },
    { label: 'actions.delete', icon: 'delete', action: this.deleteNotification.bind(this) },
  ];

  private readonly textAreaControl = new FormControl();
  public readonly deletionFormGroup = new FormGroup({ investigationId: this.textAreaControl });

  constructor(private readonly confirmModalService: ModalService) {}

  public ngAfterViewInit(): void {
    this.tableConfig = {
      displayedColumns: this.displayedColumns,
      header: CreateHeaderFromColumns(this.displayedColumns, 'table.partsColumn'),
      hasPagination: this.hasPagination,
      menuActionsConfig: this.menuActionsConfig,
      cellRenderers: {
        status: this.statusTemplate,
      },
    };
  }

  public selectNotification(notification: Record<string, unknown>): void {
    this.selected.emit(notification as unknown as Notification);
  }

  private approveNotification(notification: Notification): void {
    this.selectedInvestigation = notification;
    const onConfirm = (isConfirmed: boolean) => console.log(isConfirmed);

    const options: ConfirmModalData = {
      title: `${this.translationContext}.modal.approvalTitle`,
      confirmText: `${this.translationContext}.modal.confirm`,
      cancelText: `${this.translationContext}.modal.cancel`,

      template: this.modalApproval,
      onConfirm,
    };

    this.confirmModalService.open(options);
  }

  private deleteNotification(notification: any): void {
    this.selectedInvestigation = notification;
    this.textAreaControl.setValidators([Validators.required, Validators.pattern(this.selectedInvestigation.id)]);
    const onConfirm = (isConfirmed: boolean) => console.log(isConfirmed);

    const options: ConfirmModalData = {
      title: `${this.translationContext}.modal.deletionTitle`,
      confirmText: `${this.translationContext}.modal.confirm`,
      cancelText: `${this.translationContext}.modal.cancel`,

      template: this.modalDeletion,
      formGroup: this.deletionFormGroup,
      onConfirm,
    };

    this.confirmModalService.open(options);
  }
}
