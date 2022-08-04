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

import { Component, OnInit } from '@angular/core';
import { Pagination } from '@core/model/pagination.model';
import { PartsFacade } from '@page/parts/core/parts.facade';
import { Part } from '@page/parts/model/parts.model';
import { TableConfig, TableEventConfig } from '@shared/components/table/table.model';
import { View } from '@shared/model/view.model';
import { PartDetailsFacade } from '@shared/modules/part-details/core/partDetails.facade';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-parts',
  templateUrl: './parts.component.html',
  styleUrls: ['./parts.component.scss'],
})
export class PartsComponent implements OnInit {
  public readonly displayedColumns: string[] = [
    'id',
    'name',
    'manufacturer',
    'partNumber',
    'serialNumber',
    'qualityType',
    'productionDate',
    'productionCountry',
  ];

  public readonly sortableColumns: Record<string, boolean> = {
    id: true,
    name: true,
    manufacturer: true,
    partNumber: true,
    serialNumber: true,
    qualityType: true,
    productionDate: true,
    productionCountry: true,
  };

  public readonly parts$: Observable<View<Pagination<Part>>>;
  public readonly tableConfig: TableConfig = {
    displayedColumns: this.displayedColumns,
    header: this.displayedColumns.map(column => `pageParts.column.${column}`),
    sortableColumns: this.sortableColumns,
  };

  constructor(private readonly partsFacade: PartsFacade, private readonly partDetailsFacade: PartDetailsFacade) {
    this.parts$ = this.partsFacade.parts$;
  }

  public ngOnInit(): void {
    this.partsFacade.setParts();
  }

  public onSelectItem($event: Record<string, unknown>) {
    this.partDetailsFacade.selectedPart = $event as unknown as Part;
  }

  onTableConfigChange({ page, pageSize, sorting }: TableEventConfig) {
    this.partsFacade.setParts(page, pageSize, sorting);
  }
}
