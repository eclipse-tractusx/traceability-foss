/********************************************************************************
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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

import { TestBed } from '@angular/core/testing';
import { ToastService } from '@shared/components/toasts/toast.service';
import { CsvFilterService } from '@shared/service/csv-filter.service';
import * as Papa from 'papaparse';
import { firstValueFrom } from 'rxjs';

describe('CsvFilterService', () => {
  let service: CsvFilterService;
  let toastServiceSpy: jasmine.SpyObj<ToastService>;

  beforeEach(() => {
    toastServiceSpy = jasmine.createSpyObj('ToastService', ['error']);

    TestBed.configureTestingModule({
      providers: [
        CsvFilterService,
        { provide: ToastService, useValue: toastServiceSpy }
      ]
    });

    service = TestBed.inject(CsvFilterService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call error toast if CSV file is invalid', () => {
    const invalidFile = new File(['invalid'], 'test.txt', { type: 'text/plain' });
    expect(service["isValidCsv"](invalidFile)).toBeFalse();
    expect(toastServiceSpy.error).toHaveBeenCalledWith('errorMessage.invalidCsv');
  });

  it('should parse a valid CSV file and update filter values', async () => {
    const csvContent = 'id,idShort\n123,abc\n456,def';
    const validFile = new File([csvContent], 'test.csv', { type: 'text/csv' });

    spyOn(Papa, 'parse').and.callFake((file, options: any) => {
      return options.complete({
        data: [
          { id: '123', idShort: 'abc' },
          { id: '456', idShort: 'def' },
        ],
      });
    });

    service.parseCsvFile(validFile);

    const values = await firstValueFrom(service.searchValues$);

    expect(values).toEqual({
      id: ['123', '456'],
      idShort: ['abc', 'def'],
      semanticModelId: [],
      manufacturerPartId: [],
      customerPartId: [],
      businessPartner: []
    });
  });

  it('should call error toast if CSV content is invalid', () => {
    spyOn(Papa, 'parse').and.callFake((file, options: any) => {
      return options.complete({ data: [] });
    });

    const validFile = new File([''], 'test.csv', { type: 'text/csv' });
    service.parseCsvFile(validFile);

    expect(toastServiceSpy.error).toHaveBeenCalledWith('errorMessage.invalidCsvContent');
  });
});
