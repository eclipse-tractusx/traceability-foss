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
import { PartResponse, PartsResponse } from '@page/parts/model/parts.model';

export const MOCK_part_5 = {
  id: 'MOCK_part_5',
  idShort: 'MOCK_part_5',
  nameAtManufacturer: 'A 180 Limousine',
  nameAtCustomer: 'A 180 Limousine',
  manufacturerId: '3',
  manufacturerName: 'Mercedes-Benz',
  customerPartId: '33333',
  manufacturerPartId: 'JF1ZNAA12E8706066',
  manufacturingDate: '1990-01-13T12:34:12',
  manufacturingCountry: 'DE',
  specificAssetIds: { key: 'value' },
  childDescriptions: [],
};
export const MOCK_part_4 = {
  id: 'MOCK_part_4',
  idShort: 'MOCK_part_4',
  nameAtManufacturer: 'A 180 Limousine',
  nameAtCustomer: 'A 180 Limousine',
  manufacturerId: '3',
  manufacturerName: 'Mercedes-Benz',
  customerPartId: '33333',
  manufacturerPartId: 'JF1ZNAA12E8706066',
  manufacturingDate: '1990-01-13T12:34:12',
  manufacturingCountry: 'DE',
  specificAssetIds: { key: 'value' },
  childDescriptions: [],
};
export const MOCK_part_3 = {
  id: 'MOCK_part_3',
  idShort: 'MOCK_part_3',
  nameAtManufacturer: 'A 180 Limousine',
  nameAtCustomer: 'A 180 Limousine',
  manufacturerId: '3',
  manufacturerName: 'Mercedes-Benz',
  customerPartId: '33333',
  manufacturerPartId: 'JF1ZNAA12E8706066',
  manufacturingDate: '1990-01-13T12:34:12',
  manufacturingCountry: 'DE',
  specificAssetIds: { key: 'value' },
  childDescriptions: [{ id: MOCK_part_5.id, idShort: MOCK_part_5.idShort }],
};
export const MOCK_part_2 = {
  id: 'MOCK_part_2',
  idShort: 'MOCK_part_2',
  nameAtManufacturer: 'BMW 520d Touring',
  nameAtCustomer: 'BMW 520d Touring',
  manufacturerId: '2',
  manufacturerName: 'BMW',
  customerPartId: '22222',
  manufacturerPartId: '3N1CE2CPXFL392065',
  manufacturingDate: '2020-10-23T12:34:12',
  manufacturingCountry: 'AT',
  specificAssetIds: { key: 'value' },
  childDescriptions: [{ id: MOCK_part_4.id, idShort: MOCK_part_4.idShort }],
};
export const MOCK_part_1 = {
  id: 'MOCK_part_1',
  idShort: 'MOCK_part_1',
  nameAtManufacturer: 'Audi A1 Sportback',
  nameAtCustomer: 'Audi A1 Sportback',
  manufacturerId: '1',
  manufacturerName: 'Audi',
  customerPartId: '11111',
  manufacturerPartId: '5XXGM4A77CG032209',
  manufacturingDate: '1997-05-30T12:34:12',
  manufacturingCountry: 'DE',
  specificAssetIds: { key: 'value' },
  childDescriptions: [
    { id: MOCK_part_2.id, idShort: MOCK_part_2.idShort },
    { id: MOCK_part_3.id, idShort: MOCK_part_3.idShort },
  ],
};

export const mockAssets: PartsResponse = {
  content: [MOCK_part_1, MOCK_part_2, MOCK_part_3, MOCK_part_4, MOCK_part_5],
  page: 0,
  pageCount: 1,
  pageSize: 10,
  totalItems: 5,
};
export const mockAssetList: Record<string, PartResponse> = {
  [MOCK_part_1.id]: MOCK_part_1,
  [MOCK_part_2.id]: MOCK_part_2,
  [MOCK_part_3.id]: MOCK_part_3,
  [MOCK_part_4.id]: MOCK_part_4,
  [MOCK_part_5.id]: MOCK_part_5,
};
