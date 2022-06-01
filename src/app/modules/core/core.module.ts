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

import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { APP_INITIALIZER, NgModule } from '@angular/core';
import { MAT_DATE_LOCALE } from '@angular/material/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { I18NextModule } from 'angular-i18next';
import { environment } from '@env';
import { LayoutModule } from '@layout/layout.module';
import { AboutModule } from '@page/about/about.module';
import { DashboardModule } from '@page/dashboard/dashboard.module';
import { PageNotFoundModule } from '@page/page-not-found/page-not-found.module';
import { PartsModule } from '@page/parts/parts.module';
import { NotificationService } from '@shared/components/notifications/notification.service';
import { KeycloakAngularModule, KeycloakService } from 'keycloak-angular';
import { ApiInterceptor } from './api/api.interceptor';
import { ApiService } from './api/api.service';
import { HttpErrorInterceptor } from './api/http-error.interceptor';
import { AppComponent } from './app/app.component';
import { AuthService } from './auth/auth.service';
import { KeycloakHelper } from './auth/keycloak.helper';
import { MockedKeycloakService } from './auth/mocked-keycloak.service';
import { CoreRoutingModule } from './core.routing';
import { CanDeactivateGuard } from './user/can-deactivate.guard';
import { UserService } from './user/user.service';
import { I18N_PROVIDERS } from './i18n/global-i18n.providers';

@NgModule({
  declarations: [AppComponent],
  imports: [
    CoreRoutingModule,
    BrowserAnimationsModule,
    BrowserModule,
    HttpClientModule,
    KeycloakAngularModule,
    LayoutModule,
    PageNotFoundModule,
    AboutModule,
    DashboardModule,
    PartsModule,
    I18NextModule.forRoot(),
  ],
  providers: [
    ApiService,
    AuthService,
    UserService,
    CanDeactivateGuard,
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
      provide: HTTP_INTERCEPTORS,
      useClass: HttpErrorInterceptor,
      multi: true,
      deps: [NotificationService],
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
export class CoreModule {}
