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
import { ConfigurationService } from './configuration.service';
import { ApiService } from '@core/api/api.service';
import { ToastService } from '@shared/components/toasts/toast.service';
import { of, throwError } from 'rxjs';
import { OrderConfigurationRequest, TriggerConfigurationRequest } from '@page/digital-twin-part/presentation/configuration-dialog/model/configuration.model';

describe('ConfigurationService', () => {
  let service: ConfigurationService;
  let apiServiceSpy: jasmine.SpyObj<ApiService>;
  let toastServiceSpy: jasmine.SpyObj<ToastService>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ConfigurationService,
        { provide: ApiService, useValue: jasmine.createSpyObj('ApiService', ['post', 'get']) },
        { provide: ToastService, useValue: jasmine.createSpyObj('ToastService', ['success', 'error']) }
      ]
    });

    service = TestBed.inject(ConfigurationService);
    apiServiceSpy = TestBed.inject(ApiService) as jasmine.SpyObj<ApiService>;
    toastServiceSpy = TestBed.inject(ToastService) as jasmine.SpyObj<ToastService>;
  });

  describe('postOrderConfiguration', () => {
    it('should call ApiService.post and show success toast on success', (done) => {
      apiServiceSpy.post.and.returnValue(of(undefined));

      const request: OrderConfigurationRequest = { /* mock as needed */ } as any;

      service.postOrderConfiguration(request).subscribe(() => {
        expect(apiServiceSpy.post).toHaveBeenCalled();
        expect(toastServiceSpy.success).toHaveBeenCalledWith('orderConfiguration.success');
        done();
      });
    });

    it('should call ToastService.error on post failure', (done) => {
      apiServiceSpy.post.and.returnValue(throwError(() => new Error('Post failed')));

      const request: OrderConfigurationRequest = { /* mock as needed */ } as any;

      service.postOrderConfiguration(request).subscribe({
        next: () => {},
        error: () => {
          expect(toastServiceSpy.error).toHaveBeenCalledWith('orderConfiguration.failure');
          done();
        }
      });
    });
  });

  describe('getLatestOrderConfiguration', () => {
    it('should call ToastService.error on get failure', (done) => {
      apiServiceSpy.get.and.returnValue(throwError(() => new Error('Get failed')));

      service.getLatestOrderConfiguration().subscribe({
        next: () => {},
        error: () => {
          expect(toastServiceSpy.error).toHaveBeenCalledWith('orderConfiguration.unableToFetch');
          done();
        }
      });
    });
  });

  describe('postTriggerConfiguration', () => {
    it('should call ToastService.success on successful post', (done) => {
      apiServiceSpy.post.and.returnValue(of(undefined));

      const request: TriggerConfigurationRequest = { /* mock as needed */ } as any;

      service.postTriggerConfiguration(request).subscribe(() => {
        expect(toastServiceSpy.success).toHaveBeenCalledWith('triggerConfiguration.success');
        done();
      });
    });

    it('should call ToastService.error on post failure', (done) => {
      apiServiceSpy.post.and.returnValue(throwError(() => new Error('Post failed')));

      const request: TriggerConfigurationRequest = { /* mock as needed */ } as any;

      service.postTriggerConfiguration(request).subscribe({
        next: () => {},
        error: () => {
          expect(toastServiceSpy.error).toHaveBeenCalledWith('triggerConfiguration.failure');
          done();
        }
      });
    });
  });

  describe('getLatestTriggerConfiguration', () => {
    it('should call ToastService.error on get failure', (done) => {
      apiServiceSpy.get.and.returnValue(throwError(() => new Error('Get failed')));

      service.getLatestTriggerConfiguration().subscribe({
        next: () => {},
        error: () => {
          expect(toastServiceSpy.error).toHaveBeenCalledWith('triggerConfiguration.unableToFetch');
          done();
        }
      });
    });
  });
});
