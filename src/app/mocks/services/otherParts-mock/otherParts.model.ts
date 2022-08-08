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
import { PartsResponse } from '@page/parts/model/parts.model';
import { MOCK_part_1, MOCK_part_2, MOCK_part_3, MOCK_part_4, MOCK_part_5 } from '../parts-mock/parts.model';

export const OTHER_PARTS_MOCK_1 = { ...MOCK_part_1, id: 'OTHER_PARTS_MOCK_1', qualityType: null };
export const OTHER_PARTS_MOCK_2 = { ...MOCK_part_2, id: 'OTHER_PARTS_MOCK_2', qualityType: null };
export const OTHER_PARTS_MOCK_3 = { ...MOCK_part_3, id: 'OTHER_PARTS_MOCK_3', qualityType: null };
export const OTHER_PARTS_MOCK_4 = { ...MOCK_part_4, id: 'OTHER_PARTS_MOCK_4', qualityType: null };
export const OTHER_PARTS_MOCK_5 = { ...MOCK_part_5, id: 'OTHER_PARTS_MOCK_5', qualityType: null };
export const OTHER_PARTS_MOCK_6 = { ...MOCK_part_1, id: 'OTHER_PARTS_MOCK_6', qualityType: null };
export const OTHER_PARTS_MOCK_7 = { ...MOCK_part_2, id: 'OTHER_PARTS_MOCK_7', qualityType: null };
export const OTHER_PARTS_MOCK_8 = { ...MOCK_part_3, id: 'OTHER_PARTS_MOCK_8', qualityType: null };
export const OTHER_PARTS_MOCK_9 = { ...MOCK_part_4, id: 'OTHER_PARTS_MOCK_9', qualityType: null };

export const mockCustomerAssets: PartsResponse = {
  content: [OTHER_PARTS_MOCK_1, OTHER_PARTS_MOCK_2, OTHER_PARTS_MOCK_3, OTHER_PARTS_MOCK_4, OTHER_PARTS_MOCK_5],
  page: 0,
  pageCount: 1,
  pageSize: 10,
  totalItems: 5,
};

export const mockSupplierAssets: PartsResponse = {
  content: [OTHER_PARTS_MOCK_6, OTHER_PARTS_MOCK_7, OTHER_PARTS_MOCK_8, OTHER_PARTS_MOCK_9],
  page: 0,
  pageCount: 1,
  pageSize: 10,
  totalItems: 5,
};
