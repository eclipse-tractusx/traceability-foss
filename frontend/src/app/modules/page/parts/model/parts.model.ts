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
import { Owner, SemanticModel } from '@page/parts/model/semanticModel.model';

// TODO: needs to be aligned with Severity in the future in terms of coding standards and use
export enum QualityType {
  Ok = 'Ok',
  Minor = 'Minor',
  Major = 'Major',
  Critical = 'Critical',
  LifeThreatening = 'LifeThreatening',
}

export enum SemanticDataModel {
  BATCH = 'BATCH',
  SERIALPART = 'SERIALPART',
  PARTASPLANNED = 'PARTASPLANNED'
}

export interface Part {
  id: string;
  name: string;
  manufacturer: string;
  semanticModelId: string;
  partNumber: string;
  productionCountry: string;
  qualityType: QualityType;
  productionDate: CalendarDateModel;
  children: string[];
  parents?: string[];
  nameAtCustomer?: string;
  customerPartId?: string;
  error?: boolean;
  activeInvestigation?: boolean;
  activeAlert: boolean;
  van?: string;
  semanticDataModel: SemanticDataModel;
}

export interface PartResponse {
  id: string;
  idShort: string;
  semanticModelId: string;
  manufacturerId: string;
  manufacturerName: string;
  semanticModel: SemanticModel;
  owner: Owner;
  childRelations: Array<{ id: string; idShort: string }>;
  parentRelations?: Array<{ id: string; idShort: string }>;
  activeAlert: boolean;
  underInvestigation?: boolean;
  qualityType: QualityType;
  van?: string;
  semanticDataModel: SemanticDataModel;
}

export type PartsResponse = PaginationResponse<PartResponse>;
