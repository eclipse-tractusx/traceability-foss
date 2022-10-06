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
import {
  CreateHeaderFromColumns,
  DisplayColumns,
  MenuActionConfig,
  TableConfig,
  TablePaginationEventConfig,
} from '@shared/components/table/table.model';
import { Investigation, Investigations } from '@shared/model/investigations.model';
import { View } from '@shared/model/view.model';

@Component({
  selector: 'app-investigations-tab',
  templateUrl: './investigations-tab.component.html',
})
export class InvestigationsTabComponent implements AfterViewInit {
  @Input() investigations: View<Investigations>;
  @Input() labelId: string;
  @Input() hasPagination = true;
  @Output() pagination = new EventEmitter<TablePaginationEventConfig>();

  @ViewChild('statusTmp') statusTemplate: TemplateRef<unknown>;

  public readonly displayedColumns: DisplayColumns<keyof Investigation>[] = [
    'description',
    'status',
    'createDate',
    'menu',
  ];
  public tableConfig: TableConfig<keyof Investigation>;
  private menuActionsConfig: MenuActionConfig[] = [
    { label: 'actions.approve', icon: 'share', action: this.approveNotification.bind(this) },
    { label: 'actions.delete', icon: 'delete', action: this.deleteNotification.bind(this) },
  ];

  public ngAfterViewInit(): void {
    this.tableConfig = {
      displayedColumns: this.displayedColumns,
      header: CreateHeaderFromColumns(this.displayedColumns, 'pageInvestigations.column'),
      hasPagination: this.hasPagination,
      menuActionsConfig: this.menuActionsConfig,
      cellRenderers: {
        status: this.statusTemplate,
      },
    };
  }

  public onTableConfigChange(event: TablePaginationEventConfig) {
    this.pagination.emit(event);
  }

  private approveNotification(notification: any): void {}

  private deleteNotification(notification: any): void {}
}
