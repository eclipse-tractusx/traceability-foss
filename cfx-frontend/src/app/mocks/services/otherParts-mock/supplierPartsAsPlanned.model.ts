export const supplierPartsAsPlannedAssets = [
  {
    'id': 'urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fa01',
    'idShort': '--',
    'semanticModelId': 'NO-341449848714937445621543',
    'businessPartner': 'BPNL00000003CML1',
    'manufacturerName': 'MyManufacturerName',
    nameAtManufacturer: 'SupplierAsPlannedPartName',
    manufacturerPartId: 'ManuPartID',
    'owner': 'SUPPLIER',
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
  },
  {
    'id': 'urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fa03',
    'idShort': '--',
    'semanticModelId': 'NO-341449848714937445621543',
    'businessPartner': 'BPNL00000003CML1',
    'manufacturerName': 'MyManufacturerName',
    nameAtManufacturer: 'SupplierAsPlannedPartName',
    manufacturerPartId: 'ManuPartID',
    'owner': 'SUPPLIER',
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
  },
  {
    'id': 'urn:uuid:4a5e9ff6-2d5c-4510-a90e-d55af3ba502f',
    'idShort': '--',
    'semanticModelId': 'NO-246880451848384868750731',
    'businessPartner': 'BPNL00000003CML1',
    'manufacturerName': 'MyManufacturerName',
    nameAtManufacturer: 'SupplierAsPlannedPartName',
    manufacturerPartId: 'ManuPartID',
    'owner': 'SUPPLIER',
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
  },
  {
    'id': 'urn:uuid:0733946c-59c6-41ae-9570-cb43a6e43842',
    'idShort': '--',
    'semanticModelId': '12345678ABC',
    'businessPartner': 'BPNL00000003CML1',
    'manufacturerName': 'MyManufacturerName',
    nameAtManufacturer: 'SupplierAsPlannedPartName',
    manufacturerPartId: 'ManuPartID',
    'owner': 'SUPPLIER',
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
  },

];
