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

import { APP_INITIALIZER, Type, ɵɵFactoryDeclaration, ɵɵComponentDeclaration } from '@angular/core';
import { render, RenderComponentOptions, RenderTemplateOptions, RenderResult } from '@testing-library/angular';
import { HttpClientModule } from '@angular/common/http';
import { I18NextModule, ITranslationService, I18NEXT_SERVICE } from 'angular-i18next';
import { KeycloakService } from 'keycloak-angular';
import { MockedKeycloakService } from '@core/auth/mocked-keycloak.service';
import { Role } from '@core/user/role';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

type RenderFnOptionsExtension = {
  translations?: string[];
  roles?: Role[];
};

// As we extending render function from the testing-library with more options
// this force us to redefine type completly. Following declaration is taken from
// tesgting-library source + usage of local RenderFnOptionsExtension
declare function ExtendedRenderFn<ComponentType>(
  component: Type<ComponentType>,
  renderOptions?: RenderComponentOptions<ComponentType> & RenderFnOptionsExtension,
): Promise<RenderResult<ComponentType, ComponentType>>;
declare function ExtendedRenderFn<WrapperType = WrapperComponent>(
  template: string,
  renderOptions?: RenderTemplateOptions<WrapperType> & RenderFnOptionsExtension,
): Promise<RenderResult<WrapperType>>;
declare class WrapperComponent {
  static ɵfac: ɵɵFactoryDeclaration<WrapperComponent, never>;
  static ɵcmp: ɵɵComponentDeclaration<WrapperComponent, 'atl-wrapper-component', never, {}, {}, never, never>;
}

export const renderComponent: typeof ExtendedRenderFn = (
  cmp,
  { imports = [], providers = [], translations = [], roles = ['user'], ...restConfig },
) =>
  render(cmp, {
    imports: [...imports, I18NextModule.forRoot(), HttpClientModule, NoopAnimationsModule],
    providers: [
      ...providers,
      {
        provide: 'mockedRoles',
        useValue: roles,
      },
      {
        provide: KeycloakService,
        useClass: MockedKeycloakService,
      },
      {
        provide: APP_INITIALIZER,
        useFactory: (i18next: ITranslationService) => {
          return () =>
            i18next.init({
              lng: 'en',
              supportedLngs: ['en', 'pl'],
              resources: {
                en: {
                  translation: translations.reduce(
                    (acc, translationFile) => ({ ...acc, ...require(`../assets/locales/en/${translationFile}.json`) }),
                    {
                      ...require('../assets/locales/en/common.json'),
                    },
                  ),
                },
              },
            });
        },
        deps: [I18NEXT_SERVICE],
        multi: true,
      },
    ],
    ...restConfig,
  });
