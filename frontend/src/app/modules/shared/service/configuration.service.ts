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

import { Injectable } from '@angular/core';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { ApiService } from '@core/api/api.service';
import { environment } from '@env';
import {
    OrderConfigurationRequest,
    OrderConfigurationResponse,
    TriggerConfigurationRequest,
    TriggerConfigurationResponse
} from '@page/digital-twin-part/presentation/configuration-dialog/model/configuration.model';
import { ToastService } from '@shared/components/toasts/toast.service';

@Injectable({
    providedIn: 'root'
})
export class ConfigurationService {
    private readonly baseUrl = `${environment.apiUrl}/orders/configuration`;

    constructor(private readonly apiService: ApiService, private readonly toastService: ToastService) { }

    postOrderConfiguration(config: OrderConfigurationRequest): Observable<void> {
        return this.apiService.post<void>(`${this.baseUrl}/batches`, config).pipe(
            tap({
                next: () => {
                    this.toastService.success('orderConfiguration.success');
                },
                error: (err) => {
                    this.toastService.error('orderConfiguration.failure');
                }
            })
        );
    }

    getLatestOrderConfiguration(): Observable<OrderConfigurationResponse> {
        return this.apiService.get<OrderConfigurationResponse>(`${this.baseUrl}/batches/active`).pipe(
          catchError(err => {
            this.toastService.error('orderConfiguration.unableToFetch');
            return throwError(() => err);
          })
        );
      }

    postTriggerConfiguration(config: TriggerConfigurationRequest): Observable<void> {
        return this.apiService.post<void>(`${this.baseUrl}/triggers`, config).pipe(
            tap({
                next: () => {
                    this.toastService.success('triggerConfiguration.success');
                },
                error: (err) => {
                    this.toastService.error('triggerConfiguration.failure');
                }
            })
        );
    }


    getLatestTriggerConfiguration(): Observable<TriggerConfigurationResponse> {
        return this.apiService.get<TriggerConfigurationResponse>(`${this.baseUrl}/triggers/active`).pipe(
          catchError(err => {
            this.toastService.error('triggerConfiguration.unableToFetch');
            return throwError(() => err);
          })
        );
      }
}
