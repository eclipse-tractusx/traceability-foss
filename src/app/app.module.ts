import { BrowserModule } from '@angular/platform-browser';
import { APP_INITIALIZER, NgModule } from '@angular/core';
import { environment } from '../environments/environment';

import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { keycloakInit } from './keycloak/keycloak-init';
import { KeycloakAngularModule, KeycloakService } from 'keycloak-angular';

import { MAT_DATE_LOCALE } from '@angular/material/core';
import { AppRoutingModule } from './app.routing';
import { CoreModule } from './core/core.module';
import { DashboardModule } from './page/dashboard/dashboard.module';
import { SvgIconsModule } from '@ngneat/svg-icon';
import { icons } from './shared/shared-icons.module';
import { PageNotFoundModule } from './page/page-not-found/page-not-found.module';
import { AboutModule } from './page/about/about.module';
import { LayoutModule } from './layout/layout.module';
import { HttpErrorInterceptor } from './core/api/http-error.interceptor';
import { NotificationService } from './shared/components/notifications/notification.service';
import { ApiInterceptor } from './core/api/api.interceptor';
import { MockedKeycloakService } from './keycloak/mocked-keycloak.service';

@NgModule({
  declarations: [AppComponent],
  imports: [
    AppRoutingModule,
    BrowserAnimationsModule,
    BrowserModule,
    HttpClientModule,
    KeycloakAngularModule,
    SvgIconsModule.forRoot({
      defaultSize: 'sm',
      sizes: {
        xs: '18px',
        sm: '24px',
        md: '36px',
        lg: '48px',
        xl: '64px',
        xxl: '128px',
      },
    }),
    SvgIconsModule.forChild(icons),
    LayoutModule,
    PageNotFoundModule,
    AboutModule,
    DashboardModule,
    CoreModule,
  ],
  providers: [
    {
      provide: KeycloakService,
      useClass: environment.production ? KeycloakService : MockedKeycloakService,
    },
    {
      provide: APP_INITIALIZER,
      useFactory: keycloakInit,
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
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
