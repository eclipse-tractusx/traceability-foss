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


import { Component, OnDestroy, OnInit } from '@angular/core';
import { Pagination } from '@core/model/pagination.model';
import { OtherPartsFacade } from '@page/other-parts/core/other-parts.facade';
import { Part } from '@page/parts/model/parts.model';
import { CreateHeaderFromColumns, TableConfig, TableEventConfig } from '@shared/components/table/table.model';
import { View } from '@shared/model/view.model';
import { PartDetailsFacade } from '@shared/modules/part-details/core/partDetails.facade';
import { StaticIdService } from '@shared/service/staticId.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-customer-parts',
  templateUrl: './customer-parts.component.html'
})
export class CustomerPartsComponent implements OnInit, OnDestroy {
  public readonly displayedColumns: string[] = [
    'name',
    'manufacturer',
    'partNumber',
    'serialNumber',
    'batchNumber',
    'productionDate',
  ];

  public readonly sortableColumns: Record<string, boolean> = {
    name: true,
    manufacturer: true,
    partNumber: true,
    serialNumber: true,
    batchNumber: true,
    productionDate: true,
  };

  public readonly tableConfig: TableConfig = {
    displayedColumns: this.displayedColumns,
    header: CreateHeaderFromColumns(this.displayedColumns, 'table.column'),
    sortableColumns: this.sortableColumns,
  };

  public readonly customerParts$: Observable<View<Pagination<Part>>>;

  public readonly customerTabLabelId = this.staticIdService.generateId('OtherParts.customerTabLabel');

  constructor(
    private readonly otherPartsFacade: OtherPartsFacade,
    private readonly partDetailsFacade: PartDetailsFacade,
    private readonly staticIdService: StaticIdService,
  ) {
    this.customerParts$ = this.otherPartsFacade.customerParts$;
  }

  public ngOnInit(): void {
    this.otherPartsFacade.setCustomerParts();
  }

  public ngOnDestroy(): void {
    this.otherPartsFacade.unsubscribeParts();
  }

  public onSelectItem(event: Record<string, unknown>): void {
    this.partDetailsFacade.selectedPart = event as unknown as Part;
  }

  public onTableConfigChange({ page, pageSize, sorting }: TableEventConfig): void {
      this.otherPartsFacade.setCustomerParts(page, pageSize, sorting);
  }
}
