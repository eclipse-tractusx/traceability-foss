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

import { AfterViewInit, Component, OnInit } from '@angular/core';
import { Pagination } from '@core/model/pagination.model';
import { AdminFacade } from '@page/admin/core/admin.facade';
import { RegistryProcess } from '@page/admin/core/admin.model';
import { CreateHeaderFromColumns, TableConfig, TableEventConfig } from '@shared/components/table/table.model';
import { View } from '@shared/model/view.model';
import { StaticIdService } from '@shared/service/staticId.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-scheduled-registry-processes',
  templateUrl: './scheduled-registry-processes.component.html',
})
export class ScheduledRegistryProcessesComponent implements OnInit, AfterViewInit {
  public readonly titleId = this.staticIdService.generateId('AdminComponent.title');
  public tableConfig: TableConfig;

  public readonly scheduledRegistryProcesses$: Observable<View<Pagination<RegistryProcess>>>;

  constructor(private readonly adminFacade: AdminFacade, private readonly staticIdService: StaticIdService) {
    this.scheduledRegistryProcesses$ = this.adminFacade.scheduledRegistryProcesses$;
  }

  public ngOnInit(): void {
    this.adminFacade.setScheduledRegistryProcesses();
  }

  public ngAfterViewInit(): void {
    const displayedColumns = [
      'startDate',
      'endDate',
      'registryLookupStatus',
      'successShellDescriptorsFetchCount',
      'failedShellDescriptorsFetchCount',
      'shellDescriptorsFetchDelta',
    ];

    const sortableColumns = {
      startDate: true,
      endDate: true,
      registryLookupStatus: true,
      successShellDescriptorsFetchCount: true,
      failedShellDescriptorsFetchCount: true,
      shellDescriptorsFetchDelta: true,
    };

    this.tableConfig = {
      displayedColumns,
      sortableColumns,
      header: CreateHeaderFromColumns(displayedColumns, 'table.adminColumn'),
    };
  }

  public onTableConfigChange({ page, pageSize, sorting }: TableEventConfig): void {
    this.adminFacade.setScheduledRegistryProcesses(page, pageSize, sorting);
  }
}
