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

import type { CalendarDateModel } from '@core/model/calendar-date.model';
import type { PaginationResponse } from '@core/model/pagination.model';

export type SortableHeaders =
  | 'id'
  | 'name'
  | 'manufacturer'
  | 'serialNumber'
  | 'partNumber'
  | 'productionCountry'
  | 'nameAtCustomer'
  | 'customerPartId'
  | 'qualityType'
  | 'productionDate';

export enum QualityType {
  Ok = 'ok',
  Minor = 'minor',
  Major = 'major',
  Critical = 'critical',
  LifeThreatening = 'life-threatening',
}

export interface Part {
  id: string;
  name: string;
  manufacturer: string;
  serialNumber: string;
  partNumber: string;
  productionCountry: string;
  qualityType: QualityType;
  productionDate: CalendarDateModel;
  children: string[];
  nameAtCustomer?: string;
  customerPartId?: string;
}

export interface PartResponse {
  id: string;
  idShort: string;
  nameAtManufacturer: string;
  manufacturerPartId: string;
  manufacturerId: string;
  manufacturerName: string;
  nameAtCustomer: string;
  customerPartId: string;
  manufacturingDate: string;
  manufacturingCountry: string;
  specificAssetIds: Record<string, string>;
  childDescriptions: Array<{ id: string; idShort: string }>;
}

export type PartsResponse = PaginationResponse<PartResponse>;
