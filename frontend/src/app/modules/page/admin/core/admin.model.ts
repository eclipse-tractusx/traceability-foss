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

import { CalendarDateModel } from '@core/model/calendar-date.model';
import { FormArray, FormControl, FormGroup } from '@angular/forms';

export enum KnownAdminRouts {
  REGISTRY = 'registry-lookups',
  BPN = 'configure-bpn',
}

export enum RegistryLookupStatus {
  SUCCESSFUL = 'SUCCESSFUL',
  PARTIALLY_SUCCESS = 'PARTIALLY_SUCCESS',
  ERROR = 'ERROR',
}

export interface RegistryProcess {
  startDate: CalendarDateModel;
  registryLookupStatus: RegistryLookupStatus;
  successShellDescriptorsFetchCount: number;
  failedShellDescriptorsFetchCount: number;
  shellDescriptorsFetchDelta: number;
  endDate: CalendarDateModel;
}

export interface RegistryProcessResponse {
  startDate: string; // ISO8601
  registryLookupStatus: RegistryLookupStatus;
  successShellDescriptorsFetchCount: number;
  failedShellDescriptorsFetchCount: number;
  shellDescriptorsFetchDelta: number;
  endDate: string; // ISO8601
}

export interface BpnConfigResponse {
  organization?: string;
  edcType?: string;
  providedBy?: string;
  bpn: string;
  url: string;
}

export interface BpnConfig {
  bpn: string;
  url: string;
}

export type BpnConfigFormGroup = FormGroup<{ bpnConfig: FormArray<FormControl<BpnConfig>> }>;
