/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023, 2024 Contributors to the Eclipse Foundation
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
import { ImportState, PartResponse, QualityType, SemanticDataModel } from '@page/parts/model/parts.model';
import { otherPartsAssets } from '../../otherParts-mock/otherParts.model';

export const mockBmwAssets = [
  {
    'id': 'urn:uuid:6ec3f1db-2798-454b-a73f-0d21a8966c74',
    'idShort': '--',
    'semanticModelId': 'NO-613963493493659233961306',
    'businessPartner': 'BPNL00000003CML1',
    'manufacturerName': 'BMW AG',
    'nameAtManufacturer': 'MyAsBuiltPartName',
    'manufacturerPartId': 'ManuPartID',
    'owner': 'OWN',
    'childRelations': [
      {
        'id': 'urn:uuid:c47b9f8b-48d0-4ef4-8f0b-e965a225cb8d',
        'idShort': null,
      },
    ],
    'parentRelations': [
      {
        'id': 'urn:uuid:c47b9f8b-48d0-4ef4-8f0b-e965a225cb8d',
        'idShort': null,
      },
    ],
    'qualityType': 'Ok',
    'van': '--',
    'semanticDataModel': 'SERIALPART',
    'classification': 'component',
    'detailAspectModels': [
      {
        'type': 'AS_BUILT',
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
    'importState': 'PUBLISHED_TO_CORE_SERVICES',
    'importNote': 'This is a test import note.'
  },
  {
    'id': 'urn:uuid:0733946c-59c6-41ae-9570-cb43a6e43842',
    'idShort': '--',
    'semanticModelId': '12345678ABC',
    'businessPartner': 'BPNL00000003CML1',
    'manufacturerName': 'BMW AG',
    'nameAtManufacturer': 'MyAsBuiltPartName',
    'manufacturerPartId': 'ManuPartID',
    'owner': 'OWN',
    'childRelations': [],
    'parentRelations': [],
    'qualityType': 'Ok',
    'van': '--',
    'semanticDataModel': 'JUSTINSEQUENCE',
    'classification': 'product',
    'detailAspectModels': [
      {
        'type': 'AS_BUILT',
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
    'importState': 'TRANSIENT',
    'importNote': 'This is a test import note.'
  },
  {
    'id': 'urn:uuid:4a5e9ff6-2d5c-4510-a90e-d55af3ba502f',
    'idShort': '--',
    'semanticModelId': 'NO-246880451848384868750731',
    'businessPartner': 'BPNL00000003CML1',
    'manufacturerName': 'BMW AG',
    'nameAtManufacturer': 'MyAsBuiltPartName',
    'manufacturerPartId': 'ManuPartID',
    'owner': 'OWN',
    'childRelations': [ {
      'id': 'urn:uuid:c47b9f8b-48d0-4ef4-8f0b-e965a225cb8d',
      'idShort': null,
    } ],
    'parentRelations': [
      {
        'id': 'urn:uuid:f11ddc62-3bd5-468f-b7b0-110fe13ed0cd',
        'idShort': null,
      },
    ],
    'qualityType': 'Ok',
    'van': '--',
    'semanticDataModel': 'SERIALPART',
    'classification': 'component',
    'detailAspectModels': [
      {
        'type': 'AS_BUILT',
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
    'importState': 'TRANSIENT',
    'importNote': 'This is a test import note.'
  },
  {
    'id': 'urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fa03',
    'idShort': '--',
    'semanticModelId': 'NO-341449848714937445621543',
    'businessPartner': 'BPNL00000003CML1',
    'manufacturerName': 'BMW AG',
    'nameAtManufacturer': 'MyAsBuiltPartName',
    'manufacturerPartId': 'ManuPartID',
    'owner': 'OWN',
    'childRelations': [],
    'parentRelations': [
      {
        'id': 'urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fb02',
        'idShort': null,
      },
    ],
    'qualityType': 'Ok',
    'van': '--',
    'semanticDataModel': 'BATCH',
    'classification': 'component',
    'detailAspectModels': [
      {
        'type': 'AS_BUILT',
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
    'importState': 'PERSISTENT',
    'importNote': 'This is a test import note.',
    'tombstone': `\t\t{
\t\t\t"catenaXId": "urn:uuid:68c9b1bf-b2c1-456a-883c-2aac5f5cb5f4",
\t\t\t"endpointURL": null,
\t\t\t"processingError": {
\t\t\t\t"processStep": "BpdmRequest",
\t\t\t\t"errorDetail": "Cannot find ManufacturerId for CatenaXId: urn:uuid:68c9b1bf-b2c1-456a-883c-2aac5f5cb5f4",
\t\t\t\t"lastAttempt": "2022-11-08T08:37:18.724609316Z",
\t\t\t\t"retryCounter": 0
\t\t\t}
\t\t}`
  },
  {
    'id': 'urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fa01',
    'idShort': 'MyAsBuiltPart',
    'semanticModelId': 'NO-341449848714937445621543',
    'businessPartner': 'BPNL00000003CML1',
    'manufacturerName': 'BMW AG',
    'nameAtManufacturer': 'MyAsBuiltPartName',
    'manufacturerPartId': 'ManuPartID',
    'owner': 'OWN',
    'childRelations': [],
    'parentRelations': [],
    'qualityType': 'Ok',
    'van': '--',
    'semanticDataModel': 'BATCH',
    'classification': 'component',
    'detailAspectModels': [
      {
        'type': 'AS_BUILT',
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
    'importState': 'PERSISTENT',
    'importNote': 'This is a test import note.',
    'tombstone': `\t\t{
\t\t\t"catenaXId": "urn:uuid:68c9b1bf-b2c1-456a-883c-2aac5f5cb5f4",
\t\t\t"endpointURL": null,
\t\t\t"processingError": {
\t\t\t\t"processStep": "BpdmRequest",
\t\t\t\t"errorDetail": "Cannot find ManufacturerId for CatenaXId: urn:uuid:68c9b1bf-b2c1-456a-883c-2aac5f5cb5f4",
\t\t\t\t"lastAttempt": "2022-11-08T08:37:18.724609316Z",
\t\t\t\t"retryCounter": 0
\t\t\t}
\t\t}`
  },
  {
    'id': 'urn:uuid:d8030bbf-a874-49fb-b2e1-7610f0ccad12',
    'idShort': '--',
    'semanticModelId': 'OMAYSKEITUGNVHKKX',
    'businessPartner': 'BPNL00000003CML1',
    'manufacturerName': 'BMW AG',
    'nameAtManufacturer': 'MyAsBuiltPartName',
    'manufacturerPartId': 'ManuPartID',
    'owner': 'OWN',
    'childRelations': [
      {
        'id': 'urn:uuid:5205f736-8fc2-4585-b869-6bf36842369a',
        'idShort': null,
      },
    ],
    'parentRelations': [],
    'qualityType': 'Ok',
    'van': 'OMAYSKEITUGNVHKKX',
    'semanticDataModel': 'SERIALPART',
    'classification': 'product',
    'detailAspectModels': [
      {
        'type': 'AS_BUILT',
        'data': {
          'partId': 'JQ-87',
          'customerPartId': '--',
          'nameAtCustomer': '--',
          'manufacturingCountry': 'DEU',
          'manufacturingDate': '2015-03-07T18:38:12',
        },
      },
    ],
    'sentQualityAlertIdsInStatusActive': [],
    'receivedQualityAlertIdsInStatusActive': [],
    'sentQualityInvestigationIdsInStatusActive': [],
    'receivedQualityInvestigationIdsInStatusActive': [],
    'importState': 'IN_SYNCHRONIZATION',
    'importNote': 'This is a test import note.'
  },
  {
    'id': 'urn:uuid:5205f736-8fc2-4585-b869-6bf36842369a',
    'idShort': '--',
    'semanticModelId': 'NO-989134870198932317923938',
    'businessPartner': 'BPNL00000003CNKC',
    'manufacturerName': 'BMW AG',
    'nameAtManufacturer': 'MyAsBuiltPartName',
    'manufacturerPartId': 'ManuPartID',
    'owner': 'OWN',
    'childRelations': [],
    'parentRelations': [],
    'qualityType': 'Ok',
    'van': '--',
    'semanticDataModel': 'SERIALPART',
    'classification': 'component',
    'detailAspectModels': [
      {
        'type': 'AS_BUILT',
        'data': {
          'partId': '22782277-50',
          'customerPartId': '22782277-50',
          'nameAtCustomer': 'Door front-left',
          'manufacturingCountry': 'DEU',
          'manufacturingDate': '2022-02-04T13:48:54',
        },
      },
    ],
    'sentQualityAlertIdsInStatusActive': [],
    'receivedQualityAlertIdsInStatusActive': [],
    'sentQualityInvestigationIdsInStatusActive': [],
    'receivedQualityInvestigationIdsInStatusActive': [],
    'importState': 'ERROR',
    'importNote': 'This is a test import note.'
  },
  {
    'id': 'urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fa02',
    'idShort': '--',
    'semanticModelId': 'NO-341449848714937445621543',
    'businessPartner': 'BPNL00000003CML1',
    'manufacturerName': 'BMW AG',
    'nameAtManufacturer': 'MyAsBuiltPartName',
    'manufacturerPartId': 'ManuPartID',
    'owner': 'OWN',
    'childRelations': [
      {
        'id': 'urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fb01',
        'idShort': null,
      },
    ],
    'parentRelations': [],
    'qualityType': 'Ok',
    'van': '--',
    'semanticDataModel': 'BATCH',
    'classification': 'component',
    'detailAspectModels': [
      {
        'type': 'AS_BUILT',
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
    'importState': 'UNSET',
    'importNote': 'This is a test import note.'
  },
  {
    'id': 'urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fb01',
    'idShort': '--',
    'semanticModelId': 'NO-341449848714937445621543',
    'businessPartner': 'BPNL00000003CNKC',
    'manufacturerName': 'TEST_BPN_IRS_1',
    'nameAtManufacturer': 'MyAsBuiltPartName',
    'manufacturerPartId': 'ManuPartID',
    'owner': 'OWN',
    'childRelations': [],
    'parentRelations': [],
    'qualityType': 'Ok',
    'van': '--',
    'semanticDataModel': 'BATCH',
    'classification': 'component',
    'detailAspectModels': [
      {
        'type': 'AS_BUILT',
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
    'importState': 'PERSISTENT',
    'importNote': 'This is a test import note.'
  },
  {
    'id': 'urn:uuid:6b2296cc-26c0-4f38-8a22-092338c36e22',
    'idShort': '--',
    'semanticModelId': 'OMAOYGBDTSRCMYSCX',
    'businessPartner': 'BPNL00000003CML1',
    'manufacturerName': 'MyAsBuiltPart',
    'nameAtManufacturer': 'MyAsBuiltPartName',
    'manufacturerPartId': 'ManuPartID',
    'owner': 'OWN',
    'childRelations': [
      {
        'id': 'urn:uuid:7eeeac86-7b69-444d-81e6-655d0f1513bd',
        'idShort': null,
      },
    ],
    'parentRelations': [],
    'qualityType': 'Ok',
    'van': 'OMAOYGBDTSRCMYSCX',
    'semanticDataModel': 'SERIALPART',
    'classification': 'product',
    'detailAspectModels': [
      {
        'type': 'AS_BUILT',
        'data': {
          'partId': 'IF-53',
          'customerPartId': '--',
          'nameAtCustomer': '--',
          'manufacturingCountry': 'DEU',
          'manufacturingDate': '2018-09-28T02:15:57',
        },
      },
    ],
    'sentQualityAlertIdsInStatusActive': [],
    'receivedQualityAlertIdsInStatusActive': [],
    'sentQualityInvestigationIdsInStatusActive': [],
    'receivedQualityInvestigationIdsInStatusActive': [],
    'importState': 'PERSISTENT',
    'importNote': 'This is a test import note.'
  },
  {
    'id': 'urn:uuid:c47b9f8b-48d0-4ef4-8f0b-e965a225cb8d',
    'idShort': '--',
    'semanticModelId': 'NO-477013846751358222215326',
    'businessPartner': 'BPNL00000003CNKC',
    'manufacturerName': 'TEST_BPN_IRS_1',
    'nameAtManufacturer': 'MyAsBuiltPartName',
    'manufacturerPartId': 'ManuPartID',
    'owner': 'OWN',
    'childRelations': [],
    'parentRelations': [],
    'qualityType': 'Ok',
    'van': '--',
    'semanticDataModel': 'SERIALPART',
    'classification': 'component',
    'detailAspectModels': [
      {
        'type': 'AS_BUILT',
        'data': {
          'partId': '22782277-50',
          'customerPartId': '22782277-50',
          'nameAtCustomer': 'Door front-left',
          'manufacturingCountry': 'DEU',
          'manufacturingDate': '2022-02-04T13:48:54',
        },
      },
    ],
    'sentQualityAlertIdsInStatusActive': [],
    'receivedQualityAlertIdsInStatusActive': [],
    'sentQualityInvestigationIdsInStatusActive': [],
    'receivedQualityInvestigationIdsInStatusActive': [],
    'importState': 'PERSISTENT',
    'importNote': 'This is a test import note.'
  },
  {
    'id': 'urn:uuid:f11ddc62-3bd5-468f-b7b0-110fe13ed0cd',
    'idShort': '--',
    'semanticModelId': 'NO-004314332935115065980115',
    'businessPartner': 'BPNL00000003CNKC',
    'manufacturerName': 'TEST_BPN_IRS_1',
    'nameAtManufacturer': 'MyAsBuiltPartName',
    'manufacturerPartId': 'ManuPartID',
    'owner': 'OWN',
    'childRelations': [],
    'parentRelations': [],
    'qualityType': 'Ok',
    'van': '--',
    'semanticDataModel': 'SERIALPART',
    'classification': 'component',
    'detailAspectModels': [
      {
        'type': 'AS_BUILT',
        'data': {
          'partId': '22782277-50',
          'customerPartId': '22782277-50',
          'nameAtCustomer': 'Door front-left',
          'manufacturingCountry': 'DEU',
          'manufacturingDate': '2022-02-04T13:48:54',
        },
      },
    ],
    'sentQualityAlertIdsInStatusActive': [],
    'receivedQualityAlertIdsInStatusActive': [],
    'sentQualityInvestigationIdsInStatusActive': [],
    'receivedQualityInvestigationIdsInStatusActive': [],
    'importState': 'PERSISTENT',
    'importNote': 'This is a test import note.'
  },
  {
    'id': 'urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fb02',
    'idShort': '--',
    'semanticModelId': 'NO-341449848714937445621543',
    'businessPartner': 'BPNL00000003CNKC',
    'manufacturerName': 'TEST_BPN_IRS_1',
    'nameAtManufacturer': 'MyAsBuiltPartName',
    'manufacturerPartId': 'ManuPartID',
    'owner': 'OWN',
    'childRelations': [],
    'parentRelations': [],
    'qualityType': 'Ok',
    'van': '--',
    'semanticDataModel': 'BATCH',
    'classification': 'component',
    'detailAspectModels': [
      {
        'type': 'AS_BUILT',
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
    'importState': 'PERSISTENT',
    'importNote': 'This is a test import note.'
  },
  {
    'id': 'urn:uuid:7eeeac86-7b69-444d-81e6-655d0f1513bd',
    'idShort': '--',
    'semanticModelId': 'NO-313869652971440618042264',
    'businessPartner': 'BPNL00000003CNKC',
    'manufacturerName': 'TEST_BPN_IRS_1',
    'nameAtManufacturer': 'MyAsBuiltPartName',
    'manufacturerPartId': 'ManuPartID',
    'owner': 'OWN',
    'childRelations': [],
    'parentRelations': [],
    'qualityType': 'Ok',
    'van': '--',
    'semanticDataModel': 'SERIALPART',
    'classification': 'component',
    'detailAspectModels': [
      {
        'type': 'AS_BUILT',
        'data': {
          'partId': '22782277-50',
          'customerPartId': '22782277-50',
          'nameAtCustomer': 'Door front-left',
          'manufacturingCountry': 'DEU',
          'manufacturingDate': '2022-02-04T13:48:54',
        },
      },
    ],
    'sentQualityAlertIdsInStatusActive': [],
    'receivedQualityAlertIdsInStatusActive': [],
    'sentQualityInvestigationIdsInStatusActive': [],
    'receivedQualityInvestigationIdsInStatusActive': [],
    'importState': 'PERSISTENT',
    'importNote': 'This is a test import note.'
  },
] as PartResponse[];

const MockEmptyPart: PartResponse = {
  id: 'urn:uuid:a000a0aa-00a0-0000-000a-0a0000a0a000',
  idShort: '--',
  semanticModelId: 'mySemanticModelId',
  businessPartner: 'OEM A',
  manufacturerName: 'BMW M8',
  nameAtManufacturer: 'MyAsBuiltPartName',
  manufacturerPartId: 'ManuDefaultPartID',
  classification: 'B-Level',
  detailAspectModels: [ {
    type: DetailAspectType.AS_BUILT,
    data: {
      partId: 'TV-65',
      nameAtCustomer: '--',
      customerPartId: '--',
      manufacturingDate: '2019-11-27T10:53:12Z',
      manufacturingCountry: 'KWT',
    },

  } ],
  owner: Owner.OWN,
  childRelations: [],
  parentRelations: [],
  qualityType: QualityType.Ok,
  van: null,
  semanticDataModel: SemanticDataModel.SERIALPART,
  sentQualityAlertIdsInStatusActive: [],
  receivedQualityAlertIdsInStatusActive: [],
  sentQualityInvestigationIdsInStatusActive: [],
  receivedQualityInvestigationIdsInStatusActive: [],
  importState: ImportState.TRANSIENT,
  importNote: 'This is a test import note.'
};


export const getAssetById = (id: string) => {
  return [ ...mockBmwAssets, ...otherPartsAssets ].find(asset => asset.id === id) || { ...MockEmptyPart, id };
};

export const getRandomAsset = () => {
  const parts = [ ...mockBmwAssets, ...otherPartsAssets ];
  return parts[Math.floor(Math.random() * parts.length)];
};

