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
  'id': 'MOCK_part_5',
  'idShort': '--',
  'semanticModelId': 'NO-613963493493659233961306',
  'businessPartner': 'BPNL00000003CML1',
  'manufacturerName': 'BMW AG',
  nameAtManufacturer: 'M3 Modell',
  manufacturerPartId: 'ManuPartID',
  'owner': Owner.OWN,
  'childRelations': [],
  'parentRelations': [],
  'qualityType': QualityType.Ok,
  'van': '--',
  'semanticDataModel': SemanticDataModel.SERIALPART,
  'classification': 'component',
  'detailAspectModels': [
    {
      'type': DetailAspectType.AS_BUILT,
      'data': {
        'partId': '95657762-59',
        'customerPartId': '95657762-59',
        'nameAtCustomer': 'Door Key',
        'manufacturingCountry': 'DEU',
        'manufacturingDate': '2022-02-04T13:48:54',
      },
    },
  ],
  'sentQualityAlertIdsInStatusActive': [],
  'receivedQualityAlertIdsInStatusActive': [],
  'sentQualityInvestigationIdsInStatusActive': [],
  'receivedQualityInvestigationIdsInStatusActive': [],
};

export const MOCK_part_4 = {
  'id': 'MOCK_part_4',
  'idShort': '--',
  'semanticModelId': '12345678ABC',
  'businessPartner': 'BPNL00000003CML1',
  'manufacturerName': 'Mercedes',
  nameAtManufacturer: 'C-Klasse',
  manufacturerPartId: 'ManuPartID',
  'owner': Owner.OWN,
  'childRelations': [],
  'parentRelations': [
    {
      id: 'MOCK_part_2',
      idShort: '--',
    },
  ],
  'qualityType': QualityType.Ok,
  'van': '--',
  'semanticDataModel': SemanticDataModel.JUSTINSEQUENCE,
  'classification': 'product',
  'detailAspectModels': [
    {
      'type': DetailAspectType.AS_BUILT,
      'data': {
        'partId': '123-0.740-3434-A',
        'customerPartId': 'PRT-12345',
        'nameAtCustomer': 'side element A',
        'manufacturingCountry': 'HUR',
        'manufacturingDate': '2022-02-04T13:48:54',
      },
    },
  ],
  'sentQualityAlertIdsInStatusActive': [],
  'receivedQualityAlertIdsInStatusActive': [],
  'sentQualityInvestigationIdsInStatusActive': [],
  'receivedQualityInvestigationIdsInStatusActive': [],
};

export const MOCK_part_3 = {
  id: 'MOCK_part_3',
  'idShort': '--',
  'semanticModelId': 'NO-246880451848384868750731',
  'businessPartner': 'BPNL00000003CML1',
  'manufacturerName': 'Mercedes',
  nameAtManufacturer: 'B-Klasse',
  manufacturerPartId: 'ManuPartID',
  'owner': Owner.OWN,
  'childRelations': [
    {
      id: 'MOCK_part_1',
      idShort: '--',

    },
  ],
  'parentRelations': [],
  'qualityType': QualityType.Ok,
  'van': '--',
  'semanticDataModel': SemanticDataModel.SERIALPART,
  'classification': 'component',
  'detailAspectModels': [
    {
      'type': DetailAspectType.AS_BUILT,
      'data': {
        'partId': '95657762-59',
        'customerPartId': '95657762-59',
        'nameAtCustomer': 'Door Key',
        'manufacturingCountry': 'DEU',
        'manufacturingDate': '2022-02-04T13:48:54',
      },
    },
  ],
  'sentQualityAlertIdsInStatusActive': [],
  'receivedQualityAlertIdsInStatusActive': [],
  'sentQualityInvestigationIdsInStatusActive': [],
  'receivedQualityInvestigationIdsInStatusActive': [],
};

export const MOCK_part_2 = {
  id: 'MOCK_part_2',
  'idShort': '--',
  'semanticModelId': 'NO-341449848714937445621543',
  'businessPartner': 'BPNL00000003CML1',
  'manufacturerName': 'BMW AG',
  nameAtManufacturer: 'MyAsBuiltPartName',
  manufacturerPartId: 'ManuPartID',
  'owner': Owner.OWN,
  'childRelations': [
    {
      id: 'MOCK_part_4',
      idShort: '--',
    },
  ],
  'parentRelations': [
    {
      'id': 'MOCK_part_1',
      'idShort': '--',
    },
  ],
  'qualityType': QualityType.Ok,
  'van': '--',
  'semanticDataModel': SemanticDataModel.BATCH,
  'classification': 'component',
  'detailAspectModels': [
    {
      'type': DetailAspectType.AS_BUILT,
      'data': {
        'partId': '95657762-59',
        'customerPartId': '95657762-59',
        'nameAtCustomer': 'Door Key',
        'manufacturingCountry': 'DEU',
        'manufacturingDate': '2022-02-04T13:48:54',
      },
    },
  ],
  'sentQualityAlertIdsInStatusActive': [],
  'receivedQualityAlertIdsInStatusActive': [],
  'sentQualityInvestigationIdsInStatusActive': [],
  'receivedQualityInvestigationIdsInStatusActive': [],
};

export const MOCK_part_1 = {
  id: 'MOCK_part_1',
  'idShort': '--',
  'semanticModelId': 'NO-341449848714937445621543',
  'businessPartner': 'BPNL00000003CML1',
  'manufacturerName': 'BMW AG',
  nameAtManufacturer: 'Modell Z4',
  manufacturerPartId: 'ManuPartID',
  'owner': Owner.OWN,
  'childRelations': [ {
    id: MOCK_part_2.id,
    idShort: '--',
  },
  ],
  'parentRelations': [ {
    id: MOCK_part_3.id,
    idShort: '--',
  } ],
  'qualityType': QualityType.Ok,
  'van': '--',
  'semanticDataModel': SemanticDataModel.BATCH,
  'classification': 'component',
  'detailAspectModels': [
    {
      type: DetailAspectType.AS_BUILT,
      data: {
        'partId': '95657762-59',
        'customerPartId': '95657762-59',
        'nameAtCustomer': 'Door Key',
        'manufacturingCountry': 'DEU',
        'manufacturingDate': '2022-02-04T13:48:54',
      },
    },
  ],
  'sentQualityAlertIdsInStatusActive': [],
  'receivedQualityAlertIdsInStatusActive': [],
  'sentQualityInvestigationIdsInStatusActive': [],
  'receivedQualityInvestigationIdsInStatusActive': [],
};

export const mockAssets: PartsResponse = {
  content: [ MOCK_part_1, MOCK_part_2, MOCK_part_3, MOCK_part_4, MOCK_part_5 ],
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
