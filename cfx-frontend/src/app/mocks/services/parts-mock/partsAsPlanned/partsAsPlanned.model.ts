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

export const mockBmwAsPlannedAssets = [
  {
    'id': 'urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fa01',
    'idShort': '--',
    'semanticModelId': 'NO-341449848714937445621543',
    'businessPartner': 'BPNL00000003CML1',
    'manufacturerName': 'MyManufacturerName',
    nameAtManufacturer: 'MyAsPlannedPartName',
    manufacturerPartId: 'ManuPartID',
    'owner': 'OWN',
    'childRelations': [],
    'parentRelations': [],
    'qualityType': 'Ok',
    'van': '--',
    'semanticDataModel': 'BATCH',
    'classification': 'component',
    'detailAspectModels': [
      {
        'type': 'AS_PLANNED',
        'data': {
          validityPeriodFrom: '01.01.2020',
          functionValidUntil: '01.02.2020',
        },
      },
      {
        'type': 'PART_SITE_INFORMATION_AS_PLANNED',
        'data': {
          'functionValidUntil': 'Sat Feb 08 03:30:48 GMT 2025',
          'function': 'production',
          'functionValidFrom': 'Wed Aug 21 00:10:36 GMT 2019',
          'catenaXSiteId': 'urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4da01',
        },
      },
    ],
    'importState': 'PERSISTENT',
    'importNote': 'This is a test import note.'
  },
  {
    'id': 'urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fa03',
    'idShort': '--',
    'semanticModelId': 'NO-341449848714937445621543',
    'businessPartner': 'BPNL00000003CML1',
    'manufacturerName': 'MyManufacturerName',
    nameAtManufacturer: 'MyAsPlannedPartName',
    manufacturerPartId: 'ManuPartID',
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
        'type': 'AS_PLANNED',
        'data': {
          validityPeriodFrom: '01.01.2020',
          functionValidUntil: '01.02.2020',
        },
      },
      {
        'type': 'PART_SITE_INFORMATION_AS_PLANNED',
        'data': {
          'functionValidUntil': 'Sat Feb 08 03:30:48 GMT 2025',
          'function': 'production',
          'functionValidFrom': 'Wed Aug 21 00:10:36 GMT 2019',
          'catenaXSiteId': 'urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4da01',
        },
      },
    ],
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
    'id': 'urn:uuid:4a5e9ff6-2d5c-4510-a90e-d55af3ba502f',
    'idShort': '--',
    'semanticModelId': 'NO-246880451848384868750731',
    'businessPartner': 'BPNL00000003CML1',
    'manufacturerName': 'MyManufacturerName',
    nameAtManufacturer: 'MyAsPlannedPartName',
    manufacturerPartId: 'ManuPartID',
    'owner': 'OWN',
    'childRelations': [],
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
        'type': 'AS_PLANNED',
        'data': {
          validityPeriodFrom: '01.01.2020',
          functionValidUntil: '01.02.2020',
        },
      },
      {
        'type': 'PART_SITE_INFORMATION_AS_PLANNED',
        'data': {
          'functionValidUntil': 'Sat Feb 08 03:30:48 GMT 2025',
          'function': 'production',
          'functionValidFrom': 'Wed Aug 21 00:10:36 GMT 2019',
          'catenaXSiteId': 'urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4da01',
        },
      },
    ],
    'importState': 'PERSISTENT',
    'importNote': 'This is a test import note.'
  },
  {
    'id': 'urn:uuid:0733946c-59c6-41ae-9570-cb43a6e43842',
    'idShort': '--',
    'semanticModelId': '12345678ABC',
    'businessPartner': 'BPNL00000003CML1',
    'manufacturerName': 'MyManufacturerName',
    nameAtManufacturer: 'MyAsPlannedPartName',
    manufacturerPartId: 'ManuPartID',
    'owner': 'OWN',
    'childRelations': [],
    'parentRelations': [],
    'qualityType': 'Ok',
    'van': '--',
    'semanticDataModel': 'JUSTINSEQUENCE',
    'classification': 'product',
    'detailAspectModels': [
      {
        'type': 'AS_PLANNED',
        'data': {
          validityPeriodFrom: '01.01.2023',
          validityPeriodTo: '01.02.2023',
        },
      },
      {
        'type': 'PART_SITE_INFORMATION_AS_PLANNED',
        'data': {
          'functionValidUntil': 'Sat Feb 08 03:30:48 GMT 2025',
          'function': 'production',
          'functionValidFrom': 'Wed Aug 21 00:10:36 GMT 2019',
          'catenaXSiteId': 'urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4da01',
        },
      },
    ],
    'importState': 'PERSISTENT',
    'importNote': 'This is a test import note.'
  },
  {
    'id': 'urn:uuid:6ec3f1db-2798-454b-a73f-0d21a8966c74',
    'idShort': '--',
    'semanticModelId': 'NO-613963493493659233961306',
    'businessPartner': 'BPNL00000003CML1',
    'manufacturerName': 'MyManufacturerName',
    nameAtManufacturer: 'MyAsPlannedPartName',
    manufacturerPartId: 'ManuPartID',
    'owner': 'OWN',
    'childRelations': [ {
      'id': 'urn:uuid:c47b9f8b-48d0-4ef4-8f0b-e965a225cb8d',
      'idShort': null,
    } ],
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
        'type': 'AS_PLANNED',
        'data': {
          'validityPeriodFrom': '01.01.2023',
          'validityPeriodTo': '01.02.2023',
        },
      },
      {
        'type': 'PART_SITE_INFORMATION_AS_PLANNED',
        'data': {
          'functionValidUntil': 'Sat Feb 08 03:30:48 GMT 2025',
          'function': 'production',
          'functionValidFrom': 'Wed Aug 21 00:10:36 GMT 2019',
          'catenaXSiteId': 'urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4da01',
        },
      },
    ],
    'importState': 'PERSISTENT',
    'importNote': 'This is a test import note.'
  },

  {
    'id': 'urn:uuid:0733946c-59c6-41ae-9570-cb43a6e43842',
    'idShort': '--',
    'semanticModelId': '12345678ABC',
    'businessPartner': 'BPNL00000003CML1',
    'manufacturerName': 'MyManufacturerName',
    nameAtManufacturer: 'MyAsPlannedPartName',
    manufacturerPartId: 'ManuPartID',
    'owner': 'OWN',
    'childRelations': [],
    'parentRelations': [],
    'qualityType': 'Ok',
    'van': '--',
    'semanticDataModel': 'JUSTINSEQUENCE',
    'classification': 'product',
    'detailAspectModels': [
      {
        'type': 'AS_PLANNED',
        'data': {
          validityPeriodFrom: '01.01.2023',
          validityPeriodTo: '01.02.2023',
        },
      },
      {
        'type': 'PART_SITE_INFORMATION_AS_PLANNED',
        'data': {
          'functionValidUntil': 'Sat Feb 08 03:30:48 GMT 2025',
          'function': 'production',
          'functionValidFrom': 'Wed Aug 21 00:10:36 GMT 2019',
          'catenaXSiteId': 'urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4da01',
        },
      },
    ],
    'importState': 'PERSISTENT',
    'importNote': 'This is a test import note.'
  },

  {
    'id': 'urn:uuid:d8030bbf-a874-49fb-b2e1-7610f0ccad12',
    'idShort': '--',
    'semanticModelId': 'OMAYSKEITUGNVHKKX',
    'businessPartner': 'BPNL00000003CML1',
    'manufacturerName': 'MyManufacturerName',
    nameAtManufacturer: 'MyAsPlannedPartName',
    manufacturerPartId: 'ManuPartID',
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
        'type': 'AS_PLANNED',
        'data': {
          validityPeriodFrom: '01.01.2023',
          validityPeriodTo: '01.02.2023',
        },
      },
      {
        'type': 'PART_SITE_INFORMATION_AS_PLANNED',
        'data': {
          'functionValidUntil': 'Sat Feb 08 03:30:48 GMT 2025',
          'function': 'production',
          'functionValidFrom': 'Wed Aug 21 00:10:36 GMT 2019',
          'catenaXSiteId': 'urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4da01',
        },
      },
    ],
    'importState': 'PERSISTENT',
    'importNote': 'This is a test import note.'
  },

  {
    'id': 'urn:uuid:d8030bbf-a874-49fb-b2e1-7610f0ccvf54',
    'idShort': '--',
    'semanticModelId': 'OMAYSKEITUGNVHKKX',
    'businessPartner': 'BPNL00000003CML1',
    'manufacturerName': 'MyManufacturerName',
    nameAtManufacturer: 'MyAsPlannedPartName',
    manufacturerPartId: 'ManuPartID',
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
        'type': 'AS_PLANNED',
        'data': {
          validityPeriodFrom: '01.01.2023',
          validityPeriodTo: '01.02.2023',
        },
      },
      {
        'type': 'PART_SITE_INFORMATION_AS_PLANNED',
        'data': {
          'functionValidUntil': 'Sat Feb 08 03:30:48 GMT 2025',
          'function': 'production',
          'functionValidFrom': 'Wed Aug 21 00:10:36 GMT 2019',
          'catenaXSiteId': 'urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4da01',
        },
      },
    ],
    'importState': 'PERSISTENT',
    'importNote': 'This is a test import note.'
  },
  {
    'id': 'urn:uuid:d8030bbf-a874-49fb-b2e1-7610f0ccaf88',
    'idShort': '--',
    'semanticModelId': 'OMAYSKEITUGNVHKKX',
    'businessPartner': 'BPNL00000003CML1',
    'manufacturerName': 'MyManufacturerName',
    nameAtManufacturer: 'MyAsPlannedPartName',
    manufacturerPartId: 'ManuPartID',
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
        'type': 'AS_PLANNED',
        'data': {
          validityPeriodFrom: '01.01.2023',
          validityPeriodTo: '01.02.2023',
        },
      },
      {
        'type': 'PART_SITE_INFORMATION_AS_PLANNED',
        'data': {
          'functionValidUntil': 'Sat Feb 08 03:30:48 GMT 2025',
          'function': 'production',
          'functionValidFrom': 'Wed Aug 21 00:10:36 GMT 2019',
          'catenaXSiteId': 'urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4da01',
        },
      },
    ],
    'importState': 'PERSISTENT',
    'importNote': 'This is a test import note.'
  },
  {
    'id': 'urn:uuid:d8030bbf-a874-49fb-b2e1-7610f0ccav85',
    'idShort': 'myShortId',
    'semanticModelId': 'OMAYSKEITUGNVHKKX',
    'businessPartner': 'BPNL00000003CML1',
    'manufacturerName': 'MyManufacturerName',
    nameAtManufacturer: 'MyAsPlannedPartName',
    manufacturerPartId: 'ManuPartID',
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
        'type': 'AS_PLANNED',
        'data': {
          validityPeriodFrom: '01.01.2023',
          validityPeriodTo: '01.02.2023',
        },
      },
      {
        'type': 'PART_SITE_INFORMATION_AS_PLANNED',
        'data': {
          'functionValidUntil': 'Sat Feb 08 03:30:48 GMT 2025',
          'function': 'production',
          'functionValidFrom': 'Wed Aug 21 00:10:36 GMT 2019',
          'catenaXSiteId': 'urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4da01',
        },
      },
    ],
    'importState': 'PERSISTENT',
    'importNote': 'This is a test import note.'
  },
  {
    'id': 'urn:uuid:d8030bbf-a874-49fb-b2e1-7610f0ccag25',
    'idShort': '--',
    'semanticModelId': 'OMAYSKEITUGNVHKKX',
    'businessPartner': 'BPNL00000003CML1',
    'manufacturerName': 'MyManufacturerName',
    nameAtManufacturer: 'MyAsPlannedPartName',
    manufacturerPartId: 'ManuPartID',
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
        'type': 'AS_PLANNED',
        'data': {
          validityPeriodFrom: '01.01.2023',
          validityPeriodTo: '01.02.2023',
        },
      },
      {
        'type': 'PART_SITE_INFORMATION_AS_PLANNED',
        'data': {
          'functionValidUntil': 'Sat Feb 08 03:30:48 GMT 2025',
          'function': 'production',
          'functionValidFrom': 'Wed Aug 21 00:10:36 GMT 2019',
          'catenaXSiteId': 'urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4da01',
        },
      },
    ],
    'importState': 'PERSISTENT',
    'importNote': 'This is a test import note.'
  },
] as PartResponse[];

const MockEmptyPart: PartResponse = {
  id: 'urn:uuid:a000a0aa-00a0-0000-000a-0a0000a0a000',
  idShort: '--',
  businessPartner: 'OEM A',
  semanticModelId: 'emptySematicModelId',
  manufacturerName: 'MyDefaultManufacturerName',
  nameAtManufacturer: 'MyDefaultAsPlannedPartName',
  manufacturerPartId: 'ManuDefaultPartID',
  classification: 'B-Level',
  detailAspectModels: [ {
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
    } ],
  owner: Owner.OWN,
  childRelations: [],
  parentRelations: [],
  qualityType: QualityType.Ok,
  van: null,
  semanticDataModel: SemanticDataModel.PARTASPLANNED,
  sentQualityAlertIdsInStatusActive: [],
  receivedQualityAlertIdsInStatusActive: [],
  sentQualityInvestigationIdsInStatusActive: [],
  receivedQualityInvestigationIdsInStatusActive: [],
  importState: ImportState.TRANSIENT,
  importNote: 'This is a test import note.'
};


export const getAssetAsPlannedById = (id: string) => {
  return [ ...mockBmwAsPlannedAssets, ...otherPartsAssets ].find(asset => asset.id === id) || { ...MockEmptyPart, id };
};

export const getRandomAsset = () => {
  const parts = [ ...mockBmwAsPlannedAssets, ...otherPartsAssets ];
  return parts[Math.floor(Math.random() * parts.length)];
};

