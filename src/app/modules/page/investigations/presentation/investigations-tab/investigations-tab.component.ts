/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { AfterViewInit, Component, EventEmitter, Input, Output, TemplateRef, ViewChild } from '@angular/core';
import { Pagination } from '@core/model/pagination.model';
import { View } from '@shared/model/view.model';
import { Investigation } from '@shared/model/investigations.model';
import { TableConfig, TablePaginationEventConfig } from '@shared/components/table/table.model';

@Component({
  selector: 'app-investigations-tab',
  templateUrl: './investigations-tab.component.html',
})
export class InvestigationsTabComponent implements AfterViewInit {
  @Input() investigations: View<Pagination<Investigation>>;
  @Output() pagination = new EventEmitter<TablePaginationEventConfig>();

  @ViewChild('statusTmp') statusTmp: TemplateRef<unknown>;

  public readonly displayedColumns: (keyof Investigation)[] = ['description', 'status', 'created'];

  public tableConfig: TableConfig<keyof Investigation>;

  public ngAfterViewInit(): void {
    this.tableConfig = {
      displayedColumns: this.displayedColumns,
      header: this.displayedColumns.map(column => `pageInvestigations.column.${column}`),
      cellRenderers: {
        status: this.statusTmp,
      },
    };
  }

  public onTableConfigChange(event: TablePaginationEventConfig) {
    this.pagination.emit(event);
  }
}
