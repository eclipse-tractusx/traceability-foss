import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { APP_INITIALIZER, NgModule } from '@angular/core';
import { MAT_DATE_LOCALE } from '@angular/material/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { SvgIconsModule } from '@ngneat/svg-icon';
import { KeycloakAngularModule, KeycloakService } from 'keycloak-angular';
import { environment } from '@env';
import { AboutModule } from '@page/about/about.module';
import { DashboardModule } from '@page/dashboard/dashboard.module';
import { PageNotFoundModule } from '@page/page-not-found/page-not-found.module';
import { NotificationService } from '@shared/components/notifications/notification.service';
import { icons } from '@shared/shared-icons.module';
import { ApiInterceptor } from './api/api.interceptor';
import { ApiService } from './api/api.service';
import { HttpErrorInterceptor } from './api/http-error.interceptor';
import { AppComponent } from './app/app.component';
import { AuthService } from './auth/auth.service';
import { KeycloakHelper } from './auth/keycloak.helper';
import { MockedKeycloakService } from './auth/mocked-keycloak.service';
import { CoreRoutingModule } from './core.routing';
import { LayoutModule } from '@layout/layout.module';
import { CanDeactivateGuard } from './user/can-deactivate.guard';
import { UserService } from './user/user.service';

@NgModule({
  declarations: [AppComponent],
  imports: [
    CoreRoutingModule,
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
  ],
  providers: [
    ApiService,
    AuthService,
    UserService,
    CanDeactivateGuard,
    {
      provide: KeycloakService,
      useClass: environment.production ? KeycloakService : MockedKeycloakService,
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
  ],
  bootstrap: [AppComponent],
})
export class CoreModule {}
