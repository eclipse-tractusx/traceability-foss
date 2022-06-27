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

import { CalendarDateModel } from '@core/model/calendar-date.model';
import { Pagination } from '@core/model/pagination.model';
import { PartsAssembler } from '@page/parts/core/parts.assembler';
import { Part, QualityType } from '@page/parts/model/parts.model';
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
      expect(PartsAssembler.assembleParts(null)).toBe(null);
      expect(PartsAssembler.assembleParts(page([]))).toBe(null);
    });

    it('should format the object correctly', () => {
      const testData = [];
      const expected = [];
      for (let i = 0; i < 3; i++) {
        const id = 'id_' + i;
        const idShort = 'idShort_' + i;
        const nameAtManufacturer = 'nameAtManufacturer';
        const manufacturerPartId = 'manufacturerPartId';
        const manufacturerId = 'manufacturerId';
        const manufacturerName = 'manufacturerName';
        const nameAtCustomer = 'nameAtCustomer';
        const customerPartId = 'customerPartId';
        const manufacturingDate = 'manufacturingDate';
        const manufacturingCountry = 'manufacturingCountry';
        const specificAssetIds = { id_1: 'id_1' };
        const childDescriptions = [{ id: 'id', idShort: 'idShort' }];

        testData.push({
          id,
          idShort,
          nameAtManufacturer,
          manufacturerPartId,
          manufacturerId,
          manufacturerName,
          nameAtCustomer,
          customerPartId,
          manufacturingDate,
          manufacturingCountry,
          specificAssetIds,
          childDescriptions,
        });

        expected.push({
          id,
          name: nameAtManufacturer,
          manufacturer: manufacturerName,
          serialNumber: manufacturerPartId,
          partNumber: customerPartId,
          productionCountry: manufacturingCountry,
          nameAtCustomer: nameAtCustomer,
          customerPartId: customerPartId,
          qualityType: QualityType.Ok,
          productionDate: new CalendarDateModel(manufacturingDate),
          children: childDescriptions.map(child => child.id),
        });
      }

      expect(JSON.stringify(PartsAssembler.assembleParts(page(testData)).content)).toStrictEqual(
        JSON.stringify(expected),
      );
    });
  });

  describe('filterPartForView', () => {
    const productionDate = 'productionDate';
    const qualityType = 'qualityType';
    const serialNumber = 'serialNumber';

    it('should clean up data for part view', () => {
      const data = { productionDate, qualityType, serialNumber, test: '' } as unknown as Part;
      expect(PartsAssembler.filterPartForView({ data })).toEqual({
        data: { productionDate, qualityType, serialNumber },
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
    const partNumber = 'partNumber';
    const name = 'name';
    const serialNumber = 'serialNumber';

    it('should clean up data for manufacturer view', done => {
      const data = { manufacturer, partNumber, name, serialNumber, test: '' } as unknown as Part;
      of({ data })
        .pipe(PartsAssembler.mapPartForManufacturerView())
        .subscribe(result => {
          expect(result).toEqual({ data: { manufacturer, partNumber, serialNumber } });
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
        .pipe(PartsAssembler.mapPartForCustomerView())
        .subscribe(result => {
          expect(result).toEqual({ data: { customerPartId, nameAtCustomer } });
          done();
        });
    });

    it('should return view if data is not set', done => {
      const viewData = {};
      of(viewData)
        .pipe(PartsAssembler.mapPartForCustomerView())
        .subscribe(result => {
          expect(result).toEqual(viewData);
          done();
        });
    });
  });
});
