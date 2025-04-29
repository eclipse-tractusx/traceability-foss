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
import { of, throwError } from 'rxjs';
import { DigitalTwinPartFacade } from './digital-twin-part.facade';
import { MatDialog } from '@angular/material/dialog';
import { DigitalTwinPartService } from '@shared/service/digitalTwinPart.service';
import { ConfigurationService } from '@shared/service/configuration.service';
import { DigitalTwinPartState } from './digital-twin-part.state';
import { ConfigurationDialogComponent } from '../presentation/configuration-dialog/configuration-dialog.component';
import { View } from '@shared/model/view.model';
import { Pagination } from '@core/model/pagination.model';
import { DigitalTwinPartResponse, DigitalTwinPartDetailResponse } from '@page/digital-twin-part/model/digitalTwinPart.model';
import { OrderConfigurationResponse } from '@page/digital-twin-part/presentation/configuration-dialog/model/configuration.model';
import { DigitalTwinType } from '@page/digital-twin-part/model/digitalTwinType.enum';

describe('DigitalTwinPartFacade', () => {
  let facade: DigitalTwinPartFacade;
  let mockService: jasmine.SpyObj<DigitalTwinPartService>;
  let mockConfigService: jasmine.SpyObj<ConfigurationService>;
  let mockDialog: jasmine.SpyObj<MatDialog>;
  let mockState: DigitalTwinPartState;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        DigitalTwinPartFacade,
        { provide: DigitalTwinPartService, useValue: jasmine.createSpyObj('DigitalTwinPartService', ['getDigitalTwinParts', 'getDigitalTwinPartDetail']) },
        { provide: ConfigurationService, useValue: jasmine.createSpyObj('ConfigurationService', ['getLatestOrderConfiguration']) },
        { provide: MatDialog, useValue: jasmine.createSpyObj('MatDialog', ['open']) },
        {
          provide: DigitalTwinPartState,
          useValue: {
            digitalTwinParts$: of(null),
            digitalTwinParts: {} as View<Pagination<DigitalTwinPartResponse>>
          }
        }
      ]
    });

    facade = TestBed.inject(DigitalTwinPartFacade);
    mockService = TestBed.inject(DigitalTwinPartService) as jasmine.SpyObj<DigitalTwinPartService>;
    mockConfigService = TestBed.inject(ConfigurationService) as jasmine.SpyObj<ConfigurationService>;
    mockDialog = TestBed.inject(MatDialog) as jasmine.SpyObj<MatDialog>;
    mockState = TestBed.inject(DigitalTwinPartState);
  });

  it('should call service and set state on success in setDigitalTwinParts', () => {
    const mockResponse: Pagination<DigitalTwinPartResponse> = {
      page: 0,
      pageCount: 1,
      pageSize: 50,
      totalItems: 1,
      content: []
    };

    mockService.getDigitalTwinParts.and.returnValue(of(mockResponse));

    let stateWasSet = false;

    Object.defineProperty(mockState, 'digitalTwinParts', {
      set: (value: View<Pagination<DigitalTwinPartResponse>>) => {
        expect(value).toBeDefined();
        expect(value.data).toBeDefined();
        expect(value.data?.page).toBe(0);
        expect(value.data?.content).toEqual([]);
        stateWasSet = true;
      },
      get: () => ({ data: mockResponse })
    });

    facade.setDigitalTwinParts(0, 50, [['aasId', 'asc']]);

    expect(mockService.getDigitalTwinParts).toHaveBeenCalled();
    expect(stateWasSet).toBeTrue();
  });

  it('should update state with error on failure in setDigitalTwinParts', () => {
    const error = new Error('failed');
    mockService.getDigitalTwinParts.and.returnValue(throwError(() => error));

    Object.defineProperty(mockState, 'digitalTwinParts', {
      set: (value: View<Pagination<DigitalTwinPartResponse>>) => {
        expect(value.error).toBe(error);
      },
      get: () => ({})
    });

    facade.setDigitalTwinParts(0, 50, [['aasId', 'asc']]);
    expect(mockService.getDigitalTwinParts).toHaveBeenCalled();
  });

  it('should delegate getDigitalTwinPartDetail to service', () => {
    const mockResult = of({
        aasId: '123',
        globalAssetId: 'GAID123',
        bpn: 'BPNL12345',
        digitalTwinType: DigitalTwinType.PART_INSTANCE,
        aasExpirationDate: new Date(2025, 3, 15, 9, 0, 0),
        assetExpirationDate: new Date(2025, 3, 20, 8, 0, 0),
        aasTTL: 100,
        nextLookup: new Date(2025, 3, 16, 9, 0, 0),
        assetTTL: 120,
        nextSync: new Date(2025, 3, 17, 9, 0, 0),
        actor: 'test-actor'
      } as DigitalTwinPartDetailResponse);

    mockService.getDigitalTwinPartDetail.and.returnValue(mockResult);

    const result$ = facade.getDigitalTwinPartDetail('123');

    expect(mockService.getDigitalTwinPartDetail).toHaveBeenCalledWith('123');
    result$.subscribe(res => {
      expect(res.aasId).toBe('123');
    });
  });

  it('should open dialog with latest order config in openConfigurationDialog', () => {
    const config: OrderConfigurationResponse = {
      id: 1,
      batchSize: 100,
      timeoutMs: 3000,
      jobTimeoutMs: 10000
    };

    mockConfigService.getLatestOrderConfiguration.and.returnValue(of(config));

    facade.openConfigurationDialog();

    expect(mockConfigService.getLatestOrderConfiguration).toHaveBeenCalled();
    expect(mockDialog.open).toHaveBeenCalledWith(ConfigurationDialogComponent, jasmine.objectContaining({
      data: { order: config },
      width: '700px',
      height: '500px'
    }));
  });
});
