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

import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { APP_INITIALIZER, NgModule } from '@angular/core';
import { MAT_DATE_LOCALE } from '@angular/material/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { environment } from '@env';
import { LayoutModule } from '@layout/layout.module';
import { AboutModule } from '@page/about/about.module';
import { AdminModule } from '@page/admin/admin.module';
import { DashboardModule } from '@page/dashboard/dashboard.module';
import { OtherPartsModule } from '@page/other-parts/other-parts.module';
import { PartsModule } from '@page/parts/parts.module';
import { ToastService } from '@shared/components/toasts/toast.service';
import { I18NextModule } from 'angular-i18next';
import { KeycloakAngularModule, KeycloakService } from 'keycloak-angular';
import { ApiInterceptor } from './api/api.interceptor';
import { ApiService } from './api/api.service';
import { HttpErrorInterceptor } from './api/http-error.interceptor';
import { AppComponent } from './app/app.component';
import { AuthService } from './auth/auth.service';
import { KeycloakHelper } from './auth/keycloak.helper';
import { MockedKeycloakService } from './auth/mocked-keycloak.service';
import { CoreRoutingModule } from './core.routing';
import { I18N_PROVIDERS } from './i18n/global-i18n.providers';
import { UserService } from './user/user.service';
import { ErrorPageModule } from '@page/error-page/error-page.module';

@NgModule({
  declarations: [AppComponent],
  imports: [
    CoreRoutingModule,
    BrowserAnimationsModule,
    BrowserModule,
    HttpClientModule,
    KeycloakAngularModule,
    LayoutModule,
    ErrorPageModule,
    AboutModule,
    DashboardModule,
    PartsModule,
    OtherPartsModule,
    AdminModule,
    I18NextModule.forRoot(),
  ],
  providers: [
    ApiService,
    AuthService,
    UserService,
    {
      provide: KeycloakService,
      useClass: environment.authDisabled ? MockedKeycloakService : KeycloakService,
    },
    {
      provide: APP_INITIALIZER,
      useFactory: KeycloakHelper,
      multi: true,
      deps: [KeycloakService],
    },
    {
      provide: MAT_DATE_LOCALE,
      useValue: 'en-GB',
    },
    {
      provide: MAT_DATE_LOCALE,
      useValue: 'de-DE',
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpErrorInterceptor,
      multi: true,
      deps: [ToastService],
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: ApiInterceptor,
      multi: true,
    },
    ...I18N_PROVIDERS,
  ],
  bootstrap: [AppComponent],
})
export class CoreModule { }
