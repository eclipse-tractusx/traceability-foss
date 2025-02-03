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

import { DetailAspectType } from '@page/parts/model/detailAspectModel.model';
import { Owner } from '@page/parts/model/owner.enum';
import { PartResponse, PartsResponse, QualityType, SemanticDataModel } from '@page/parts/model/parts.model';

export const MOCK_part_5 = {
  id: 'MOCK_part_5',
  idShort: 'MOCK_part_5',
  businessPartner: 'BPNCML1000001',
  manufacturerName: 'Mercedes Benz',
  nameAtManufacturer: 'K-130',
  manufacturerPartId: 'ManuPartID',
  classification: 'A-Level',
  semanticModelId: 'semanticID',
  detailAspectModels: [{
    type: DetailAspectType.AS_PLANNED,
    data: {
      validityPeriodFrom: '01.01.2023',
      validityPeriodTo: '01.02.2023',
    },
  },
  {
    type: DetailAspectType.PART_SITE_INFORMATION_AS_PLANNED,
    data: {
      functionValidUntil: 'Sat Feb 08 03:30:48 GMT 2025',
      function: 'production',
      functionValidFrom: 'Wed Aug 21 00:10:36 GMT 2019',
      catenaXSiteId: 'urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4da01',
    },
  },
  ],
  owner: Owner.OWN,
  childRelations: [],
  parentRelations: [],
  qualityType: QualityType.LifeThreatening,
  van: 'myvan5',
  semanticDataModel: SemanticDataModel.SERIALPART,
  sentQualityAlertIdsInStatusActive: [],
  receivedQualityAlertIdsInStatusActive: [],
  sentQualityInvestigationIdsInStatusActive: [],
  receivedQualityInvestigationIdsInStatusActive: [],
};

export const MOCK_part_4 = {
  id: 'MOCK_part_4',
  idShort: 'MOCK_part_4',
  businessPartner: 'BPNCML000001',
  manufacturerName: 'Daimler',
  nameAtManufacturer: 'F-Klasse',
  manufacturerPartId: 'ManuPartID',
  semanticModelId: 'semanticID',
  classification: 'B-Level',
  detailAspectModels: [{
    type: DetailAspectType.AS_PLANNED,
    data: {
      validityPeriodFrom: '01.01.2023',
      validityPeriodTo: '01.02.2023',
    },
  },
  {
    type: DetailAspectType.PART_SITE_INFORMATION_AS_PLANNED,
    data: {
      functionValidUntil: 'Sat Feb 08 03:30:48 GMT 2025',
      function: 'production',
      functionValidFrom: 'Wed Aug 21 00:10:36 GMT 2019',
      catenaXSiteId: 'urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4da01',
    },
  },
  ],
  owner: Owner.OWN,
  childRelations: [],
  parentRelations: [],
  qualityType: QualityType.Critical,
  van: 'myvan4',
  semanticDataModel: SemanticDataModel.SERIALPART,
  sentQualityAlertIdsInStatusActive: [],
  receivedQualityAlertIdsInStatusActive: [],
  sentQualityInvestigationIdsInStatusActive: [],
  receivedQualityInvestigationIdsInStatusActive: [],
};

export const MOCK_part_3 = {
  id: 'MOCK_part_3',
  idShort: 'MOCK_part_3',
  businessPartner: 'Mercedes-Benz',
  semanticModelId: 'semanticID',
  manufacturerName: 'BWM AG',
  nameAtManufacturer: 'Back Door Left',
  manufacturerPartId: 'ManuPartID',
  classification: 'C-Level',
  detailAspectModels: [{
    type: DetailAspectType.AS_PLANNED,
    data: {
      validityPeriodFrom: '01.01.2022',
      validityPeriodTo: '01.02.2022',
    },
  },
  {
    type: DetailAspectType.PART_SITE_INFORMATION_AS_PLANNED,
    data: {
      functionValidUntil: 'Sat Feb 08 03:30:48 GMT 2025',
      function: 'production',
      functionValidFrom: 'Wed Aug 21 00:10:36 GMT 2019',
      catenaXSiteId: 'urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4da01',
    },
  },
  ],
  owner: Owner.OWN,
  childRelations: [{ id: MOCK_part_5.id, idShort: MOCK_part_5.idShort }],
  parentRelations: [],
  qualityType: QualityType.Major,
  van: 'myvan3',
  semanticDataModel: SemanticDataModel.PARTASPLANNED,
  sentQualityAlertIdsInStatusActive: [],
  receivedQualityAlertIdsInStatusActive: [],
  sentQualityInvestigationIdsInStatusActive: [],
  receivedQualityInvestigationIdsInStatusActive: [],
};

export const MOCK_part_2 = {
  id: 'MOCK_part_2',
  idShort: 'MOCK_part_2',
  businessPartner: 'BMW',
  manufacturerName: 'BMW AG',
  nameAtManufacturer: 'MyAsPlannedPartName',
  manufacturerPartId: 'ManuPartID',
  semanticModelId: 'semanticID',
  classification: 'A-Level',
  detailAspectModels: [{
    type: DetailAspectType.AS_PLANNED,
    data: {
      validityPeriodFrom: '01.01.2023',
      validityPeriodTo: '01.02.2023',
    },
  },
  {
    type: DetailAspectType.PART_SITE_INFORMATION_AS_PLANNED,
    data: {
      functionValidUntil: 'Sat Feb 08 03:30:48 GMT 2025',
      function: 'production',
      functionValidFrom: 'Wed Aug 21 00:10:36 GMT 2019',
      catenaXSiteId: 'urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4da01',
    },
  },
  ],
  owner: Owner.OWN,
  childRelations: [{ id: MOCK_part_4.id, idShort: MOCK_part_4.idShort }],
  parentRelations: [],
  qualityType: QualityType.Minor,
  van: 'myvan2',
  semanticDataModel: SemanticDataModel.SERIALPART,
  sentQualityAlertIdsInStatusActive: [],
  receivedQualityAlertIdsInStatusActive: [],
  sentQualityInvestigationIdsInStatusActive: [],
  receivedQualityInvestigationIdsInStatusActive: [],
};

export const MOCK_part_1 = {
  id: 'MOCK_part_1',
  idShort: 'MOCK_part_1',
  businessPartner: 'Audi',
  semanticModelId: 'mySemanticModelId',
  manufacturerName: 'Audi AG',
  nameAtManufacturer: 'MyAsPlannedPartName',
  manufacturerPartId: 'ManuPartID',
  classification: 'C-Level',
  detailAspectModels: [{
    type: DetailAspectType.AS_PLANNED,
    data: {
      validityPeriodFrom: '01.01.2023',
      validityPeriodTo: '01.02.2023',
    },
  },
  {
    type: DetailAspectType.PART_SITE_INFORMATION_AS_PLANNED,
    data: {
      functionValidUntil: 'Sat Feb 08 03:30:48 GMT 2025',
      function: 'production',
      functionValidFrom: 'Wed Aug 21 00:10:36 GMT 2019',
      catenaXSiteId: 'urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4da01',
    },
  },
  ],
  owner: Owner.OWN,
  childRelations: [
    { id: MOCK_part_2.id, idShort: MOCK_part_2.idShort },
    { id: MOCK_part_3.id, idShort: MOCK_part_3.idShort },
  ],
  parentRelations: [{ id: MOCK_part_4.id, idShort: MOCK_part_4.idShort }],
  qualityType: QualityType.Ok,
  van: 'myvan1',
  semanticDataModel: SemanticDataModel.SERIALPART,
  sentQualityAlertIdsInStatusActive: [],
  receivedQualityAlertIdsInStatusActive: [],
  sentQualityInvestigationIdsInStatusActive: [],
  receivedQualityInvestigationIdsInStatusActive: [],
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
