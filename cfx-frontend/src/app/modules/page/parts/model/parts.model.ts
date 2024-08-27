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

import type { PaginationResponse } from '@core/model/pagination.model';
import { SemanticModel, TractionBatteryCode } from '@page/parts/model/aspectModels.model';
import { DetailAspectModel } from '@page/parts/model/detailAspectModel.model';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { Owner } from '@page/parts/model/owner.enum';

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
  PARTASPLANNED = 'PARTASPLANNED',
  JUSTINSEQUENCE = 'JUSTINSEQUENCE',
  TRACTIONBATTERYCODE = 'TRACTIONBATTERYCODE',
  UNKNOWN = 'UNKNOWN'
}

export enum SemanticDataModelInCamelCase {
  BATCH = 'Batch',
  SERIALPART = 'SerialPart',
  PARTASPLANNED = 'PartAsPlanned',
  JUSTINSEQUENCE = 'JustInSequence',
  TRACTIONBATTERYCODE = 'TractionBatteryCode',
  UNKNOWN = 'Unknown'
}

export interface Relation {
  id: string;
  idShort: string;
}

export enum ImportState {
  TRANSIENT = "TRANSIENT",
  PERSISTENT = "PERSISTENT",
  IN_SYNCHRONIZATION = "IN_SYNCHRONIZATION",
  ERROR = "ERROR",
  UNSET = "UNSET"
}

export enum ImportStateInCamelCase {
  TRANSIENT = "Transient",
  PERSISTENT = "Persistent",
  IN_SYNCHRONIZATION = "In Synchronization",
  ERROR = "Error",
  UNSET = "Unset"
}

export enum FilterOperator {
  EQUAL = 'EQUAL',
  AT_LOCAL_DATE = 'AT_LOCAL_DATE',
  STARTS_WITH = 'STARTS_WITH',
  BEFORE_LOCAL_DATE = 'BEFORE_LOCAL_DATE',
  AFTER_LOCAL_DATE = 'AFTER_LOCAL_DATE',
  NOTIFICATION_COUNT_EQUAL = 'NOTIFICATION_COUNT_EQUAL'
}

export function getFilterOperatorValue(operator: FilterOperator) {
  return operator as string;
}

export interface AssetAsBuiltFilter {
  id?: string,
  idShort?: string,
  name?: string,
  manufacturerName?: string,
  partId?: string,
  manufacturerPartId?: string,
  customerPartId?: string,
  classification?: string,
  nameAtCustomer?: string,
  semanticModelId?: string,
  semanticDataModel?: string[],
  manufacturingDate?: string,
  manufacturingCountry?: string
}

export interface AssetAsPlannedFilter {
  id?: string,
  idShort?: string,
  name?: string,
  manufacturer?: string,
  manufacturerPartId?: string,
  classification?: string,
  semanticDataModel?: string[],
  semanticModelId?: string,
  validityPeriodFrom?: string,
  validityPeriodTo?: string,
  psFunction?: string,
  catenaXSiteId?: string,
  functionValidFrom?: string,
  functionValidUntil?: string,
}

export interface AssetAsDesignedFilter {
  id?: string;
}

export interface AssetAsSupportedFilter {
  id?: string;
}

export interface AssetAsOrderedFilter {
  id?: string;
}

export interface AssetAsRecycledFilter {
  id?: string;
}

export interface Part {
  id: string;
  idShort: string;
  manufacturerName: string;
  manufacturerPartId: string;
  nameAtManufacturer: string;
  businessPartner: string;
  semanticModel?: SemanticModel;
  semanticModelId: string;
  qualityType: QualityType;
  children: string[];
  parents?: string[];
  error?: boolean;
  van: string;
  owner: Owner;
  semanticDataModel: SemanticDataModel;
  classification: string;

  mainAspectType: MainAspectType;

  // aspectmodel props are temporarely hardcoded here because Tables and Views only accept root level prop array
  // as built
  partId?: string;
  customerPartId?: string;
  nameAtCustomer?: string;
  manufacturingDate?: string;
  manufacturingCountry?: string;

  productType?: string;
  tractionBatteryCode?: string;
  subcomponents: TractionBatteryCode[];

  // as planned
  validityPeriodFrom?: string;
  validityPeriodTo?: string;
  //partSiteInformationAsPlanned
  catenaXSiteId: string;
  psFunction: string;
  functionValidFrom?: string;
  functionValidUntil?: string;

  // count of notifications
  sentActiveAlerts: string[];
  sentActiveInvestigations: string[];
  receivedActiveAlerts: string[];
  receivedActiveInvestigations: string[];

  importNote?: string;
  importState?: ImportState;
}

export interface PartResponse {
  id: string;
  idShort: string;
  semanticModelId: string;
  manufacturerPartId: string;
  businessPartner: string;
  manufacturerName: string;
  nameAtManufacturer: string;
  owner: Owner;
  childRelations: Relation[];
  parentRelations: Relation[];
  qualityType: QualityType;
  van: string;
  semanticDataModel: SemanticDataModel;
  classification: string;
  detailAspectModels: DetailAspectModel[];
  // TODO: Delete ? flag when AsPlanned Parts do not return the props anymore
  sentQualityAlertIdsInStatusActive: string[],
  receivedQualityAlertIdsInStatusActive: string[],
  sentQualityInvestigationIdsInStatusActive: string[],
  receivedQualityInvestigationIdsInStatusActive: string[]
  importNote?: string,
  importState?: ImportState
}

export type PartsResponse = PaginationResponse<PartResponse>;
