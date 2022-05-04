import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../auth/auth.service';

/**
 *
 *
 * @export
 * @class ApiService
 */
@Injectable({
  providedIn: 'root',
})
export class ApiService {
  constructor(private httpClient: HttpClient, private authService: AuthService) {}

  private static stringifyBody<T>(body: T | null): string {
    return JSON.stringify(body === null ? {} : body);
  }

  public getBy<T>(url: string, params?: HttpParams, withCredentials = false, headers?: HttpHeaders): Observable<T> {
    return this.httpClient.get<T>(url + params, {
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
