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

import { PartsResponse } from '@page/parts/model/parts.model';
import {
  MOCK_part_1,
  MOCK_part_2,
  MOCK_part_3,
  MOCK_part_4,
  MOCK_part_5,
} from '../parts-mock/partsAsBuilt/partsAsBuilt.test.model';
import { supplierPartsAsPlannedAssets } from './supplierPartsAsPlanned.model';

export const OTHER_PARTS_MOCK_1 = { ...MOCK_part_1, id: 'OTHER_PARTS_MOCK_1', qualityType: null };
export const OTHER_PARTS_MOCK_2 = { ...MOCK_part_2, id: 'OTHER_PARTS_MOCK_2', qualityType: null };
export const OTHER_PARTS_MOCK_3 = { ...MOCK_part_3, id: 'OTHER_PARTS_MOCK_3', qualityType: null };
export const OTHER_PARTS_MOCK_4 = { ...MOCK_part_4, id: 'OTHER_PARTS_MOCK_4', qualityType: null };
export const OTHER_PARTS_MOCK_5 = { ...MOCK_part_5, id: 'OTHER_PARTS_MOCK_5', qualityType: null };
export const OTHER_PARTS_MOCK_6 = { ...MOCK_part_1, id: 'OTHER_PARTS_MOCK_6', qualityType: null };
export const OTHER_PARTS_MOCK_7 = { ...MOCK_part_2, id: 'OTHER_PARTS_MOCK_7', qualityType: null };
export const OTHER_PARTS_MOCK_8 = { ...MOCK_part_3, id: 'OTHER_PARTS_MOCK_8', qualityType: null };
export const OTHER_PARTS_MOCK_9 = { ...MOCK_part_4, id: 'OTHER_PARTS_MOCK_9', qualityType: null };

export const SUPPLIER_PARTS_ASPLANNED = { ...supplierPartsAsPlannedAssets };

export const mockCustomerAssets: PartsResponse = {
  content: [ OTHER_PARTS_MOCK_1, OTHER_PARTS_MOCK_2, OTHER_PARTS_MOCK_3, OTHER_PARTS_MOCK_4, OTHER_PARTS_MOCK_5 ],
  page: 0,
  pageCount: 1,
  pageSize: 10,
  totalItems: 5,
};

export const mockSupplierAssets: PartsResponse = {
  content: [ OTHER_PARTS_MOCK_6, OTHER_PARTS_MOCK_7, OTHER_PARTS_MOCK_8, OTHER_PARTS_MOCK_9 ],
  page: 0,
  pageCount: 1,
  pageSize: 10,
  totalItems: 5,
};

