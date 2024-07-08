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

import { Pagination } from '@core/model/pagination.model';
import { AsBuiltAspectModel } from '@page/parts/model/aspectModels.model';
import { DetailAspectType } from '@page/parts/model/detailAspectModel.model';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { Part, QualityType, SemanticDataModel } from '@page/parts/model/parts.model';
import { PartsAssembler } from '@shared/assembler/parts.assembler';
import { of } from 'rxjs';

describe('PartsAssembler', () => {
  const page = <T>(content: T[]): Pagination<T> => ({
    content,
    pageCount: 1,
    totalItems: content.length,
    page: 0,
    pageSize: content.length,
  });

  describe('assembleParts', () => {
    it('should return null if array is empty or undefined', () => {
      const emptyPage = { content: [], page: 0, pageCount: 0, pageSize: 50, totalItems: 0 };
      expect(PartsAssembler.assembleParts(null, MainAspectType.AS_BUILT)).toEqual(emptyPage);
      expect(PartsAssembler.assembleParts(page([]), MainAspectType.AS_BUILT)).toEqual(emptyPage);
    });

    it('should format the object correctly', () => {
      const testData = [];
      const expected = [];

      const id = 'id_0';
      const idShort = 'idShort_0';
      const semanticModelId = 'semanticModelId';
      const manufacturerPartId = 'manufacturerPartId';
      const businessPartner = 'businesspartner';
      const manufacturerName = 'manufacturerName';
      const nameAtManufacturer = 'nameAtManufacturer';
      const owner = 'OWN';
      const childRelations = [];
      const parentRelations = [];
      const qualityType = 'Ok';
      const van = 'van';
      const semanticDataModel = 'BATCH';
      const classification = 'component';
      const semanticModel = {
        'partId': 'partId',
        'customerPartId': 'customerPartId',
        'nameAtCustomer': 'nameAtCustomer',
        'manufacturingDate': 'testdate',
        'manufacturingCountry': 'manufacturingCountry',
      };
      const detailAspectModels = [ {
        type: DetailAspectType.AS_BUILT,
        data: {
          partId: 'partId',
          customerPartId: 'customerPartId',
          nameAtCustomer: 'nameAtCustomer',
          manufacturingDate: 'testdate',
          manufacturingCountry: 'manufacturingCountry',
        },
      },
      ];

      const mainAspectType = 'as_built';

      testData.push({
        id,
        idShort,
        semanticModelId,
        manufacturerPartId,
        businessPartner,
        manufacturerName,
        nameAtManufacturer,
        owner,
        childRelations,
        parentRelations,
        qualityType,
        van,
        semanticDataModel,
        classification,
        semanticModel,
        detailAspectModels,
        mainAspectType,
      });

      const partId = (detailAspectModels[0].data as AsBuiltAspectModel)?.partId;
      const customerPartId = (detailAspectModels[0].data as AsBuiltAspectModel)?.customerPartId;
      const nameAtCustomer = (detailAspectModels[0].data as AsBuiltAspectModel)?.nameAtCustomer;
      const manufacturingDate = 'testdate';
      const manufacturingCountry = (detailAspectModels[0].data as AsBuiltAspectModel)?.manufacturingCountry;

      expected.push({
        id,
        idShort: idShort,
        semanticModelId: semanticModelId,
        manufacturerName: manufacturerName,
        manufacturerPartId: manufacturerPartId,
        nameAtManufacturer: nameAtManufacturer,
        owner: owner,
        businessPartner: businessPartner,
        children: [],
        parents: [],
        qualityType: QualityType.Ok,
        van: 'van',
        semanticDataModel: SemanticDataModel.BATCH,
        classification: classification,
        semanticModel: semanticModel,
        mainAspectType: mainAspectType,
        partId: partId, // is partInstance, BatchId, jisNumber
        customerPartId: customerPartId,
        nameAtCustomer: nameAtCustomer,
        manufacturingDate: manufacturingDate,
        manufacturingCountry: manufacturingCountry,
        tombStoneErrorDetail: null,

      });

      expect(JSON.stringify(PartsAssembler.assembleParts(page(testData), MainAspectType.AS_BUILT).content)).toEqual(JSON.stringify(expected));
    });
  });

  describe('filterPartForView', () => {
    const semanticModelId = 'semanticModelId';
    const semanticDataModel = 'semanticDataModel';
    const manufacturingDate = 'manufacturingDate';
    const manufacturingCountry = 'manufacturingCountry';
    const classification = 'classification';


    it('should clean up data for part view', () => {
      const data = {
        semanticDataModel,
        semanticModelId,
        manufacturingDate,
        manufacturingCountry,
        classification,
        test: '',
      } as unknown as Part;
      expect(PartsAssembler.filterPartForView({ data })).toEqual({
        data: {
          manufacturingDate,
          semanticModelId,
          semanticDataModel,
          manufacturingCountry,
          classification,
        } as unknown as Part,
      });
    });

    it('should return view if data is not set', () => {
      const viewData = {};
      expect(PartsAssembler.filterPartForView(viewData)).toEqual(viewData);
      expect(PartsAssembler.filterPartForView(undefined)).toEqual(undefined);
    });
  });

  describe('mapPartForManufacturerView', () => {
    const manufacturerName = 'manufacturerName';
    const manufacturerPartId = 'manufacturerPartId';
    const nameAtManufacturer = 'nameAtManufacturer';
    const businessPartner = 'businessPartner';
    const van = 'van';
    const mainAspectType = MainAspectType.AS_BUILT;

    it('should clean up data for manufacturer view', done => {
      const data = {
        manufacturerName,
        manufacturerPartId,
        nameAtManufacturer,
        businessPartner,
        test: '',
        van,
        mainAspectType,
      } as unknown as Part;
      of({ data })
        .pipe(PartsAssembler.mapPartForManufacturerView())
        .subscribe(result => {
          expect(result).toEqual({
            data: { manufacturerName, manufacturerPartId, nameAtManufacturer, businessPartner, van } as unknown as Part,
          });
          done();
        });
    });

    it('should return view if data is not set', done => {
      const viewData = {};
      of(viewData)
        .pipe(PartsAssembler.mapPartForManufacturerView())
        .subscribe(result => {
          expect(result).toEqual(viewData);
          done();
        });
    });
  });

  describe('mapPartForCustomerView', () => {
    const customerPartId = 'customerPartId';
    const nameAtCustomer = 'nameAtCustomer';

    it('should clean up data for customer view', done => {
      const data = { customerPartId, nameAtCustomer, test: '' } as unknown as Part;
      of({ data })
        .pipe(PartsAssembler.mapPartForCustomerOrPartSiteView())
        .subscribe(result => {
          expect(result).toEqual({ data: { customerPartId, nameAtCustomer } as unknown as Part });
          done();
        });
    });

    it('should return view if data is not set', done => {
      const viewData = {};
      of(viewData)
        .pipe(PartsAssembler.mapPartForCustomerOrPartSiteView())
        .subscribe(result => {
          expect(result).toEqual(undefined);
          done();
        });
    });
  });

  describe('mapForTractionBatteryCodeDetailsView', () => {
    const tractionBatteryCode = 'tractionBatteryCode';
    const productType = 'productType';
    it('should clean up data for traction battery details view', done => {
      const data = { tractionBatteryCode, productType, test: '' } as unknown as Part;
      of({ data })
        .pipe(PartsAssembler.mapPartForTractionBatteryCodeDetailsView())
        .subscribe(result => {
          expect(result).toEqual({ data: { tractionBatteryCode, productType } as unknown as Part });
          done();
        });
    });
    it('should return nothing when there is no viewData', done => {
      const data = undefined as unknown as Part;
      of({ data })
        .pipe(PartsAssembler.mapPartForTractionBatteryCodeDetailsView())
        .subscribe(result => {
          expect(result).toEqual(undefined);
          done();
        });
    });
  });

  describe('mapForTractionBatterySubComponentsView', () => {
    const tractionBatteryCode = 'tractionBatteryCode';
    const productType = 'productType';
    const subcomponents = 'subcomponents';
    it('should clean up data for traction battery subcomponents view', done => {
      const data = { tractionBatteryCode, productType, subcomponents, test: '' } as unknown as Part;
      of({ data })
        .pipe(PartsAssembler.mapPartForTractionBatteryCodeSubComponentsView())
        .subscribe(result => {
          expect(result).toEqual({ data: { tractionBatteryCode, productType, subcomponents } as unknown as Part });
          done();
        });
    });
    it('should return nothing when there is no viewData', done => {
      const data = undefined as unknown as Part;
      of({ data })
        .pipe(PartsAssembler.mapPartForTractionBatteryCodeSubComponentsView())
        .subscribe(result => {
          expect(result).toEqual(undefined);
          done();
        });
    });
    it('should return nothing when there is no tractionbattery', done => {
      const data = { productType, subcomponents, test: '' } as unknown as Part;
      of({ data })
        .pipe(PartsAssembler.mapPartForTractionBatteryCodeSubComponentsView())
        .subscribe(result => {
          expect(result).toEqual(undefined);
          done();
        });
    });
  });

  describe('mapForAssetStateView', () => {
    const importState = 'importState';
    const importNote = 'importNote';
    const tombStoneErrorDetail = 'Error';
    it('should clean up data for asset state view', done => {
      const data = { importState, importNote, test: '' } as unknown as Part;
      of({ data })
        .pipe(PartsAssembler.mapPartForAssetStateDetailsView())
        .subscribe(result => {
          expect(result).toEqual({ data: { importState, importNote } as unknown as Part });
          done();
        });
    });
    it('should return nothing when there is no viewData', done => {
      const data = undefined as unknown as Part;
      of({ data })
        .pipe(PartsAssembler.mapPartForAssetStateDetailsView())
        .subscribe(result => {
          expect(result).toEqual(undefined);
          done();
        });
    });
    it('should clean up data for asset state view with tombStoneErrorDetail', done => {
      const data = { importState, importNote, tombStoneErrorDetail } as unknown as Part;
      of({ data })
        .pipe(PartsAssembler.mapPartForAssetStateDetailsView())
        .subscribe(result => {
          expect(result).toEqual({ data: { importState, importNote, tombStoneErrorDetail } as unknown as Part });
          done();
        });
    });
  });



});
