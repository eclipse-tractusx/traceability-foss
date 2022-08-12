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
import { TableConfig, TablePaginationEventConfig } from '@shared/components/table/table.model';
import { Investigation, Investigations } from '@shared/model/investigations.model';
import { View } from '@shared/model/view.model';

@Component({
  selector: 'app-investigations-tab',
  templateUrl: './investigations-tab.component.html',
})
export class InvestigationsTabComponent implements AfterViewInit {
  @Input() investigations: View<Investigations>;
  @Input() labelId: string;
  @Output() pagination = new EventEmitter<TablePaginationEventConfig>();

  @ViewChild('statusTmp') statusTemplate: TemplateRef<unknown>;

  public readonly displayedColumns: (keyof Investigation)[] = ['description', 'status', 'created'];
  public tableConfig: TableConfig<keyof Investigation>;

  public ngAfterViewInit(): void {
    this.tableConfig = {
      displayedColumns: this.displayedColumns,
      header: this.displayedColumns.map(column => `pageInvestigations.column.${column}`),
      cellRenderers: {
        status: this.statusTemplate,
      },
    };
  }

  public onTableConfigChange(event: TablePaginationEventConfig) {
    this.pagination.emit(event);
  }
}
