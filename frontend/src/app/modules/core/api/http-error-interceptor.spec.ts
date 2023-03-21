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

import { AfterViewInit, Component, Input } from '@angular/core';
import { ApiService } from '@core/api/api.service';
import { HttpErrorInterceptor } from '@core/api/http-error.interceptor';
import { CoreModule } from '@core/core.module';
import { environment } from '@env';
import { SharedModule } from '@shared/shared.module';
import { screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { of } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Component({ selector: 'app-interceptor-test', template: '<app-toast-container></app-toast-container>' })
export class ErrorTestComponent implements AfterViewInit {
  @Input() statusCode = '404';

  constructor(private readonly apiService: ApiService) {}

  public ngAfterViewInit() {
    this.apiService
      .get(environment.apiUrl + '/error/' + this.statusCode)
      .pipe(catchError(_ => of(null)))
      .subscribe();
  }
}

describe('HttpErrorInterceptor', () => {
  const initErrorInterceptor = async (statusCode: string) => {
    await renderComponent(ErrorTestComponent, {
      declarations: [ErrorTestComponent],
      imports: [SharedModule, CoreModule],
      componentProperties: { statusCode },
    });
  };

  it('should intercept', async () => {
    await initErrorInterceptor('403');
    expect(
      await waitFor(() => screen.getByText('Backend returned code 403: Permission denied error message')),
    ).toBeInTheDocument();
  });
});
