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

import { AfterViewInit, Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { Pagination } from '@core/model/pagination.model';
import { PartsFacade } from '@page/parts/core/parts.facade';
import { Part } from '@page/parts/model/parts.model';
import { StaticIdService } from '@shared/service/staticId.service';
import { TableConfig, TableEventConfig } from '@shared/components/table/table.model';
import { View } from '@shared/model/view.model';
import { PartDetailsFacade } from '@shared/modules/part-details/core/partDetails.facade';
import { BehaviorSubject, Observable } from 'rxjs';

@Component({
  selector: 'app-parts',
  templateUrl: './parts.component.html',
  styleUrls: ['./parts.component.scss'],
})
export class PartsComponent implements OnInit, AfterViewInit {
  @ViewChild('statusTmp') statusTmp: TemplateRef<unknown>;
  public readonly displayedColumns: string[] = [
    'id',
    'name',
    'manufacturer',
    'partNumber',
    'serialNumber',
    'qualityType',
    'productionDate',
    'productionCountry',
    'childInvestigation',
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

  public readonly titleId = this.staticIdService.generateId('PartsComponent.title');

  public tableConfig: TableConfig;

  public readonly isInvestigationOpen$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  public readonly parts$: Observable<View<Pagination<Part>>>;
  public readonly currentSelectedItems$: Observable<Part[]>;

  constructor(
    private readonly partsFacade: PartsFacade,
    private readonly partDetailsFacade: PartDetailsFacade,
    private readonly staticIdService: StaticIdService,
  ) {
    this.parts$ = this.partsFacade.parts$;
    this.currentSelectedItems$ = this.partsFacade.selectedParts$;
  }

  public ngOnInit(): void {
    this.partsFacade.setParts();
  }

  public ngAfterViewInit(): void {
    this.tableConfig = {
      displayedColumns: this.displayedColumns,
      header: this.displayedColumns.map(column => `pageParts.column.${column}`),
      sortableColumns: this.sortableColumns,
      cellRenderers: {
        childInvestigation: this.statusTmp,
      },
    };
  }

  public onSelectItem($event: Record<string, unknown>): void {
    this.partDetailsFacade.selectedPart = $event as unknown as Part;
  }

  public onTableConfigChange({ page, pageSize, sorting }: TableEventConfig): void {
    this.partsFacade.setParts(page, pageSize, sorting);
  }

  public startInvestigation(event: MouseEvent, row: Part): void {
    event.stopPropagation();
    this.isInvestigationOpen$.next(true);
    this.partsFacade.setSelectedParts(
      row.children.filter((value, index, self) => {
        return self.indexOf(value) === index;
      }),
    );
  }

  public removeItemFromSelection(part: Part): void {
    this.partsFacade.removeSelectedPart(part);
  }

  public addItemToSelection(part: Part): void {
    this.partsFacade.addItemToSelection(part);
  }

  public clearSelected(): void {
    this.partsFacade.selectedParts = [];
  }
}
