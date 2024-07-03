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
 *********************************************************************************/

import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthService } from '../auth/auth.service';

@Injectable({
  providedIn: 'root',
})
export class ApiService {
  lastRequest: { execute?: () => Observable<void>, context?: any };
  constructor(private readonly httpClient: HttpClient, private readonly authService: AuthService) {
  }

  private static stringifyBody<T>(body: T | null): string {
    return JSON.stringify(body === null ? {} : body);
  }

  public getBy<T>(url: string, params?: HttpParams, withCredentials = false, headers?: HttpHeaders): Observable<T> {
    return this.httpClient.get<T>(url, {
      params,
      headers: headers ? headers : this.buildHeaders(),
      withCredentials,
    });
  }

  public get<T>(url: string, withCredentials = false, headers?: HttpHeaders): Observable<T> {
    return this.httpClient.get<T>(url, {
      headers: headers ? headers : this.buildHeaders(),
      withCredentials,
    });
  }

  public post<T>(
    url: string,
    body?: Record<string, unknown> | unknown,
    responseType?: 'json',
    withCredentials = false,
    headers?: HttpHeaders,
    params?: HttpParams,
  ): Observable<T> {
    const urlWithParams = params ? `${ url }${ params }` : url;
    return this.httpClient.post<T>(urlWithParams, ApiService.stringifyBody(body), {
      headers: headers ? headers : this.buildHeaders(),
      responseType,
      withCredentials,
    });
  }

  public postFile<T>(
    url: string,
    file: FormData,
  ): Observable<T> {
    return this.httpClient.post<T>(url, file, {
      headers: this.buildHeadersForFile(),
    });
  }

  public put<T>(
    url: string,
    body?: Record<string, unknown> | unknown,
    responseType?: 'json',
    withCredentials = false,
    headers?: HttpHeaders,
  ): Observable<T> {
    return this.httpClient.put<T>(url, ApiService.stringifyBody(body), {
      headers: headers ? headers : this.buildHeaders(),
      responseType,
      withCredentials,
    });
  }

  public patch<T>(
    url: string,
    body?: Record<string, unknown> | unknown,
    responseType?: 'json',
    withCredentials = false,
    headers?: HttpHeaders,
  ): Observable<T> {
    return this.httpClient.patch<T>(url, ApiService.stringifyBody(body), {
      headers: headers ? headers : this.buildHeaders(),
      responseType,
      withCredentials,
    });
  }

  public delete<T>(url: string, params?: HttpParams, withCredentials = false, headers?: HttpHeaders): Observable<T> {
    return this.httpClient.delete<T>(url, {
      params,
      headers: headers ? headers : this.buildHeaders(),
      withCredentials,
    });
  }

  /**
   * set the public class property 'lastRequest' from where you made the request
   * before retrying
   */
  public retryLastRequest(): Observable<any> | null {
    if (this.lastRequest.execute) {
      return this.lastRequest.execute();
    }
  }

  private buildHeaders(): HttpHeaders {
    return new HttpHeaders({
      Access: 'application/json',
      'Content-Type': 'application/json',
      Authorization: this.authService.getBearerToken(),
    });
  }


  private buildHeadersForFile(): HttpHeaders {
    return new HttpHeaders({
      Authorization: this.authService.getBearerToken(),
    });
  }
}
