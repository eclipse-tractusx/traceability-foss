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
import { BpnConfig } from '@page/admin/core/admin.model';
import { AdminService } from '@page/admin/core/admin.service';
import { Observable } from 'rxjs';

@Injectable()
export class AdminFacade {
  constructor( private readonly adminService: AdminService) {
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

  public postJsonImport(file: File): Observable<void>{
    return this.adminService.postJsonFile(file);
  }

}
