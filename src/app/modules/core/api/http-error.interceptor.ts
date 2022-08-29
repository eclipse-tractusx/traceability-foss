/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
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
import { NotificationService } from 'src/app/modules/shared/components/notifications/notification.service';

export class HttpErrorInterceptor implements HttpInterceptor {
  constructor(private readonly notificationService: NotificationService) {}

  public intercept(
    request: HttpRequest<Record<string, unknown>>,
    next: HttpHandler,
  ): Observable<HttpEvent<Record<string, unknown>>> {
    return next.handle(request).pipe(
      retry(1),
      catchError((error: HttpErrorResponse) => {
        let errorMessage: string;
        if (Array.isArray(error.error.error)) {
          errorMessage = `An error occurred: ${error.error.message}`;
          error.error.error.forEach(message => (errorMessage = message.message));
        } else if (error.message) {
          errorMessage = error.message;
        } else {
          errorMessage = `Backend returned code ${error.status}: ${error.message}`;
        }
        this.notificationService.error(errorMessage);
        return throwError(() => error);
      }),
    );
  }
}
