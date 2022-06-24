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

import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthService } from '../auth/auth.service';

@Injectable({
  providedIn: 'root',
})
export class ApiService {
  constructor(private httpClient: HttpClient, private authService: AuthService) {}

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
    const urlWithParams = params ? `${url}${params}` : url;
    return this.httpClient.post<T>(urlWithParams, ApiService.stringifyBody(body), {
      headers: headers ? headers : this.buildHeaders(),
      responseType,
      withCredentials,
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

  public delete<T>(url: string, withCredentials = false, headers?: HttpHeaders): Observable<T> {
    return this.httpClient.delete<T>(url, {
      headers: headers ? headers : this.buildHeaders(),
      withCredentials,
    });
  }

  private buildHeaders(): HttpHeaders {
    return new HttpHeaders({
      Access: 'application/json',
      'Content-Type': 'application/json',
      Authorization: this.authService.getBearerToken(),
    });
  }
}
