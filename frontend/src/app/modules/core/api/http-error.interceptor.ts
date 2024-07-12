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

import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';
import { ToastService } from 'src/app/modules/shared/components/toasts/toast.service';

export class HttpErrorInterceptor implements HttpInterceptor {

  // List of request.url that should not automatically display a toast but are handled custom (Can be extended later by METHOD) and should not be retried
  private avoidList = [ '/api/notifications', '/api/notifications/*/approve', '/api/notifications/*/cancel', '/api/notifications/*/close', '/api/notifications/*/update' ];

  constructor(private readonly toastService: ToastService) {
  }

  public intercept(
    request: HttpRequest<Record<string, unknown>>,
    next: HttpHandler,
  ): Observable<HttpEvent<Record<string, unknown>>> {
    const requestUrl = this.stripBaseUrl(request.url);
    const isHandled = this.isOnAlreadyHandledUrlList(requestUrl);

    if (isHandled) {
      // Handle the request without retry if it matches the avoidList
      return next.handle(request).pipe(
        catchError((errorResponse: HttpErrorResponse) => this.handleError(request, errorResponse)),
      );
    } else {
      // Retry the request once if it does not match the avoidList
      return next.handle(request).pipe(
        retry(1),
        catchError((errorResponse: HttpErrorResponse) => this.handleError(request, errorResponse)),
      );
    }
  }

  private handleError(request: HttpRequest<any>, errorResponse: HttpErrorResponse): Observable<never> {
    const { error, message } = errorResponse;
    const errorMessage = !error.message ? message : `Backend returned code ${ error.status }: ${ error.message }`;

    // Check if the request URL matches any pattern in the avoidList
    if (!this.isOnAlreadyHandledUrlList(this.stripBaseUrl(request.url))) {
      this.toastService.error(errorMessage);
    }

    return throwError(() => errorResponse);
  }

  // Helper method to check if the URL matches any pattern in the avoidList
  private isOnAlreadyHandledUrlList(url: string): boolean {
    return this.avoidList.some(pattern => this.urlMatchesPattern(url, pattern));
  }

  // Helper method to check if the URL matches a wildcard pattern
  private urlMatchesPattern(url: string, pattern: string): boolean {
    // Convert wildcard pattern to regex
    const escapedPattern = pattern.split('*').map(escapeRegExp).join('.*');
    const regexPattern = `^${ escapedPattern }$`;
    const regex = new RegExp(regexPattern);
    const matches = regex.test(url);
    return matches;
  }

  // Helper method to strip base URL from the request URL
  private stripBaseUrl(url: string): string {
    const baseUrlPattern = /^(https?:\/\/[^\/]+)(\/.*)?$/;
    const match = baseUrlPattern.exec(url);
    return match && match[2] ? match[2] : url;
  }
}

// Helper function to escape regex special characters in a string
function escapeRegExp(string: string): string {
  return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}








