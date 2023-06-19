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

import { PartResponse, PartsResponse, QualityType, SemanticDataModel } from '@page/parts/model/parts.model';
import { Owner } from '@page/parts/model/semanticModel.model';

export const MOCK_part_5 = {
  id: 'MOCK_part_5',
  idShort: 'MOCK_part_5',
  semanticModelId: 'mysemanticModelId5',
  manufacturerId: '3',
  manufacturerName: 'Mercedes-Benz',
  semanticModel: {
    manufacturingDate: '1990-01-13T12:34:12',
    manufacturingCountry: 'DEU',
    manufacturerPartId: 'JF1ZNAA12E8706066',
    customerPartId: '33333',
    nameAtManufacturer: 'A 180 Limousine',
    nameAtCustomer: 'A 180 Limousine',
  },
  owner: Owner.OWN,
  childRelations: [],
  parentRelations: [],
  activeAlert: false,
  underInvestigation: false,
  qualityType: QualityType.LifeThreatening,
  van: 'myvan5',
  semanticDataModel: SemanticDataModel.SERIALPARTTYPIZATION,
};

export const MOCK_part_4 = {
  id: 'MOCK_part_4',
  idShort: 'MOCK_part_4',
  semanticModelId: 'mysemanticModelId4',
  manufacturerId: '3',
  manufacturerName: 'Mercedes-Benz',
  semanticModel: {
    manufacturingDate: '1990-01-13T12:34:12',
    manufacturingCountry: 'DEU',
    manufacturerPartId: 'JF1ZNAA12E8706066',
    customerPartId: '33333',
    nameAtManufacturer: 'A 180 Limousine',
    nameAtCustomer: 'A 180 Limousine',
  },
  owner: Owner.OWN,
  childRelations: [],
  parentRelations: [],
  activeAlert: false,
  underInvestigation: false,
  qualityType: QualityType.Critical,
  van: 'myvan4',
  semanticDataModel: SemanticDataModel.SERIALPARTTYPIZATION,
};

export const MOCK_part_3 = {
  id: 'MOCK_part_3',
  idShort: 'MOCK_part_3',
  semanticModelId: 'mysemanticModelId3',
  manufacturerId: '3',
  manufacturerName: 'Mercedes-Benz',
  semanticModel: {
    manufacturingDate: '1990-01-13T12:34:12',
    manufacturingCountry: 'DEU',
    manufacturerPartId: 'JF1ZNAA12E8706066',
    customerPartId: '33333',
    nameAtManufacturer: 'A 180 Limousine',
    nameAtCustomer: 'A 180 Limousine',
  },
  owner: Owner.OWN,
  childRelations: [{ id: MOCK_part_5.id, idShort: MOCK_part_5.idShort }],
  parentRelations: [],
  activeAlert: false,
  underInvestigation: false,
  qualityType: QualityType.Major,
  van: 'myvan3',
  semanticDataModel: SemanticDataModel.SERIALPARTTYPIZATION,
};

export const MOCK_part_2 = {
  id: 'MOCK_part_2',
  idShort: 'MOCK_part_2',
  semanticModelId: 'mysemanticmodelId2',
  manufacturerId: '2',
  manufacturerName: 'BMW',
  semanticModel: {
    manufacturingDate: '2020-10-23T12:34:12',
    manufacturingCountry: 'POL',
    manufacturerPartId: '3N1CE2CPXFL392065',
    customerPartId: '22222',
    nameAtManufacturer: 'BMW 520d Touring',
    nameAtCustomer: 'BMW 520d Touring',
  },
  owner: Owner.OWN,
  childRelations: [{ id: MOCK_part_4.id, idShort: MOCK_part_4.idShort }],
  parentRelations: [],
  activeAlert: false,
  underInvestigation: false,
  qualityType: QualityType.Minor,
  van: 'myvan2',
  semanticDataModel: SemanticDataModel.SERIALPARTTYPIZATION
};

export const MOCK_part_1 = {
  id: 'MOCK_part_1',
  idShort: 'MOCK_part_1',
  semanticModelId: 'mysemanticModelId1',
  manufacturerId: '1',
  manufacturerName: 'Audi',
  semanticModel: {
    manufacturingDate: '1997-05-30T12:34:12',
    manufacturingCountry: 'POL',
    manufacturerPartId: '5XXGM4A77CG032209',
    customerPartId: '11111',
    nameAtManufacturer: 'Audi A1 Sportback',
    nameAtCustomer: 'Audi A1 Sportback Customer',
  },
  owner: Owner.OWN,
  childRelations: [
    { id: MOCK_part_2.id, idShort: MOCK_part_2.idShort },
    { id: MOCK_part_3.id, idShort: MOCK_part_3.idShort },
  ],
  parentRelations: [{ id: MOCK_part_4.id, idShort: MOCK_part_4.idShort }],
  activeAlert: false,
  underInvestigation: false,
  qualityType: QualityType.Ok,
  van: 'myvan1',
  semanticDataModel: SemanticDataModel.BATCH
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
