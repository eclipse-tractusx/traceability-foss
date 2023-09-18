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
import { MainAspectTypeModel } from '@page/parts/model/MainAspectType.model';
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
      const emptyPage = { content: [], page: 0, pageCount: 0, pageSize: 0, totalItems: 0 };
      expect(PartsAssembler.assembleParts(null, MainAspectTypeModel.AS_BUILT)).toEqual(emptyPage);
      expect(PartsAssembler.assembleParts(page([]), MainAspectTypeModel.AS_BUILT)).toEqual(emptyPage);
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
        const activeAlert = false;
        const underInvestigation = false;
        const qualityType = 'Ok';
        const van = 'van';
        const semanticDataModel = 'BATCH';
        const classification = 'component';
        const semanticModel = {
          "partId": "partId",
          "customerPartId": "customerPartId",
          "nameAtCustomer": "nameAtCustomer",
          "manufacturingDate": "testdate",
          "manufacturingCountry": "manufacturingCountry",
        }
        const detailAspectModels = [ {
          type: DetailAspectType.AS_BUILT,
          data: {
            partId: 'partId',
            customerPartId: 'customerPartId',
            nameAtCustomer: 'nameAtCustomer',
            manufacturingDate: "testdate",
            manufacturingCountry: 'manufacturingCountry'
          }
        }
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
          activeAlert,
          underInvestigation,
          qualityType,
          van,
          semanticDataModel,
          classification,
          semanticModel,
          detailAspectModels,
          mainAspectType
        });

        const partId = (detailAspectModels[0].data as AsBuiltAspectModel)?.partId;
        const customerPartId = (detailAspectModels[0].data as AsBuiltAspectModel)?.customerPartId;
        const nameAtCustomer = (detailAspectModels[0].data as AsBuiltAspectModel)?.nameAtCustomer;
        const manufacturingDate = "testdate"
        const manufacturingCountry = (detailAspectModels[0].data as AsBuiltAspectModel)?.manufacturingCountry;

        expected.push({
          id,
          idShort: idShort,
          semanticModelId: semanticModelId,

          manufacturer: manufacturerName,
          manufacturerPartId: manufacturerPartId,
          nameAtManufacturer: nameAtManufacturer,
          businessPartner: businessPartner,
          name: nameAtManufacturer,
          children: [],
          parents: [],
          activeAlert: false,
          activeInvestigation: false,
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

        });

      expect(JSON.stringify(PartsAssembler.assembleParts(page(testData), MainAspectTypeModel.AS_BUILT).content)).toEqual(JSON.stringify(expected));
    });
  });

  describe('filterPartForView', () => {
    const semanticModelId = 'semanticModelId';
    const semanticDataModel = 'semanticDataModel';
    const manufacturingDate = 'manufacturingDate';
    const manufacturingCountry = 'manufacturingCountry';
    const classification = 'classification';

    it('should clean up data for part view', () => {
      const data = { semanticDataModel, semanticModelId, manufacturingDate, manufacturingCountry, classification, test: '' } as unknown as Part;
      expect(PartsAssembler.filterPartForView({ data })).toEqual({
        data: { name: undefined, manufacturingDate, semanticModelId, semanticDataModel, manufacturingCountry, classification } as unknown as Part,
      });
    });

    it('should return view if data is not set', () => {
      const viewData = {};
      expect(PartsAssembler.filterPartForView(viewData)).toEqual(viewData);
      expect(PartsAssembler.filterPartForView(undefined)).toEqual(undefined);
    });
  });

  describe('mapPartForManufacturerView', () => {
    const manufacturer = 'manufacturer';
    const manufacturerPartId = 'manufacturerPartId';
    const nameAtManufacturer = 'nameAtManufacturer';
    const van = 'van';
    const mainAspectType = MainAspectTypeModel.AS_BUILT

    it('should clean up data for manufacturer view', done => {
      const data = { manufacturer, manufacturerPartId, nameAtManufacturer, test: '', van, mainAspectType } as unknown as Part;
      of({ data })
        .pipe(PartsAssembler.mapPartForManufacturerView())
        .subscribe(result => {
          expect(result).toEqual({
            data: { manufacturer, manufacturerPartId, nameAtManufacturer, van } as unknown as Part,
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
});
