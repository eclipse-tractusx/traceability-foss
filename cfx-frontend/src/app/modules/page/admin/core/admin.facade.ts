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

import { Injectable } from '@angular/core';
import { Pagination } from '@core/model/pagination.model';
import { BpnConfig, RegistryProcess } from '@page/admin/core/admin.model';
import { AdminService } from '@page/admin/core/admin.service';
import { AdminState } from '@page/admin/core/admin.state';
import { TableHeaderSort } from '@shared/components/table/table.model';
import { View } from '@shared/model/view.model';
import _deepClone from 'lodash-es/cloneDeep';
import { Observable, Subscription } from 'rxjs';

@Injectable()
export class AdminFacade {
  private scheduledRegistryProcessesSubscription: Subscription;

  constructor(private readonly adminState: AdminState, private readonly adminService: AdminService) {}

  public get scheduledRegistryProcesses$(): Observable<View<Pagination<RegistryProcess>>> {
    return this.adminState.scheduledRegistryProcesses$;
  }

  public set scheduledRegistryProcesses(view: View<Pagination<RegistryProcess>>) {
    this.adminState.scheduledRegistryProcesses = _deepClone(view);
  }

  public setScheduledRegistryProcesses(page = 0, pageSize = 50, sorting: TableHeaderSort = null) {
    this.scheduledRegistryProcessesSubscription?.unsubscribe();
    this.scheduledRegistryProcessesSubscription = this.adminService
      .getScheduledRegistryProcesses(page, pageSize, sorting)
      .subscribe({
        next: data => (this.adminState.scheduledRegistryProcesses = { data }),
        error: error => (this.adminState.scheduledRegistryProcesses = { error }),
      });
  }

  public createBpnFallbackConfig(bpnConfig: BpnConfig[]): Observable<BpnConfig[]> {
    return this.adminService.createBpnFallbackConfig(bpnConfig);
  }

  public readBpnFallbackConfig(): Observable<BpnConfig[]> {
    return this.adminService.readBpnFallbackConfig();
  }

  public updateBpnFallbackConfig(bpnConfig: BpnConfig[]): Observable<BpnConfig[]> {
    return this.adminService.updateBpnFallbackConfig(bpnConfig);
  }

  public deleteBpnFallbackConfig(bpnId: string): Observable<void> {
    return this.adminService.deleteBpnFallbackConfig(bpnId);
  }
}
