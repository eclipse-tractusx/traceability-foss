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

const mock_1 = { ...MOCK_part_1, id: 'mock_1', qualityType: null };
const mock_2 = { ...MOCK_part_2, id: 'mock_2', qualityType: null };
const mock_3 = { ...MOCK_part_3, id: 'mock_3', qualityType: null };
const mock_4 = { ...MOCK_part_4, id: 'mock_4', qualityType: null };
const mock_5 = { ...MOCK_part_5, id: 'mock_5', qualityType: null };
const mock_6 = { ...MOCK_part_1, id: 'mock_6', qualityType: null };
const mock_7 = { ...MOCK_part_2, id: 'mock_7', qualityType: null };
const mock_8 = { ...MOCK_part_3, id: 'mock_8', qualityType: null };
const mock_9 = { ...MOCK_part_4, id: 'mock_9', qualityType: null };

export const mockCustomerAssets: PartsResponse = {
  content: [mock_1, mock_2, mock_3, mock_4, mock_5],
  page: 0,
  pageCount: 1,
  pageSize: 10,
  totalItems: 5,
};

export const mockSupplierAssets: PartsResponse = {
  content: [mock_6, mock_7, mock_8, mock_9],
  page: 0,
  pageCount: 1,
  pageSize: 10,
  totalItems: 5,
};
