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

import { HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ApiService } from '@core/api/api.service';
import { environment } from '@env';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Dashboard } from '../model/dashboard.model';

@Injectable()
export class DashboardService {
  constructor(private apiService: ApiService) {}

  public getStats(): Observable<Dashboard> {
    return this.apiService.post(`/kpi-stats`).pipe(map((payload: { data: Dashboard; status: number }) => payload.data));
  }

  public getGeolocationOfCountry(iso: string): Observable<any> {
    const httpParams = new HttpParams()
      .set('access_token', environment.mapBoxAccessToken)
      .set('language', 'en-EN')
      .set('types', 'country');
    return this.apiService.getBy(`https://api.mapbox.com/geocoding/v5/mapbox.places/${iso}.json`, httpParams);
  }
}
