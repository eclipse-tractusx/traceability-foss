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

import type { CalendarDateModel } from '@core/model/calendar-date.model';
import type { PaginationResponse } from '@core/model/pagination.model';

export type SortableHeaders =
  | 'id'
  | 'name'
  | 'manufacturer'
  | 'serialNumber'
  | 'partNumber'
  | 'batchNumber'
  | 'productionCountry'
  | 'nameAtCustomer'
  | 'customerPartId'
  | 'qualityType'
  | 'productionDate'
  | 'van';

// TODO: needs to be aligned with Severity in the future in terms of coding standards and use
export enum QualityType {
  Ok = 'Ok',
  Minor = 'Minor',
  Major = 'Major',
  Critical = 'Critical',
  LifeThreatening = 'LifeThreatening',
}

export interface Part {
  id: string;
  name: string;
  manufacturer: string;
  serialNumber: string;
  partNumber: string;
  batchNumber: string;
  productionCountry: string;
  qualityType: QualityType;
  productionDate: CalendarDateModel;
  children: string[];
  nameAtCustomer?: string;
  customerPartId?: string;
  error?: boolean;
  shouldHighlight?: boolean;
  van?: string;
}

export interface PartResponse {
  id: string;
  idShort: string;
  batchId?: string;
  nameAtManufacturer: string;
  manufacturerPartId: string;
  partInstanceId: string;
  manufacturerId: string;
  manufacturerName: string;
  nameAtCustomer: string;
  customerPartId: string;
  manufacturingDate: string;
  manufacturingCountry: string;
  qualityType: QualityType;
  specificAssetIds: Record<string, string>;
  childDescriptions: Array<{ id: string; idShort: string }>;
  underInvestigation?: boolean;
  van?: string;
}

export type PartsResponse = PaginationResponse<PartResponse>;

export interface PartsCountriesMapResponse {
  [index: string]: number;
}

export interface PartsCountriesMap {
  [index: string]: number;
}
