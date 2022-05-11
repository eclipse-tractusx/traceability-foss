import { render } from '@testing-library/angular';
import { SvgIconsModule } from '@ngneat/svg-icon';
import { HttpClientModule } from '@angular/common/http';
import { KeycloakService } from 'keycloak-angular';
import { MockedKeycloakService } from '@core/auth/mocked-keycloak.service';

export const renderComponent: typeof render = (cmp, {
  imports = [],
  providers = [],
  ...restConfig
}) =>
  render(cmp, {
    imports: [...imports, SvgIconsModule.forRoot(), SvgIconsModule.forChild([]), HttpClientModule],
    providers: [
      ...providers,
      {
        provide: KeycloakService,
        useClass: MockedKeycloakService,
      }
    ],
    ...restConfig
  });
