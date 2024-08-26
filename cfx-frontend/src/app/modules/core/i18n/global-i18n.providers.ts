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

import { APP_INITIALIZER, LOCALE_ID } from '@angular/core';
import { I18NEXT_SERVICE, ITranslationService } from 'angular-i18next';
import LanguageDetector from 'i18next-browser-languagedetector';
import HttpApi from 'i18next-http-backend';

// language labels shouldn't be translated, because when user want to switch the languge
// user may not know selected languages
export const ALL_KNOWN_LOCALES = {
  en: 'English',
  de: 'Deutsch',
};
export type KnownLocale = keyof typeof ALL_KNOWN_LOCALES;

export function appInit(i18next: ITranslationService) {
  return () =>
    i18next
      .use(LanguageDetector)
      .use(HttpApi)
      .init({
        supportedLngs: Object.keys(ALL_KNOWN_LOCALES),
        fallbackLng: 'en',
        backend: {
          loadPath: '/assets/locales/{{lng}}/{{ns}}.json',
        },
        detection: {
          // order and from where user language should be detected
          order: [ 'querystring', 'localStorage', 'navigator', 'htmlTag' ],

          // keys or params to lookup language from
          lookupQuerystring: 'lng',
          lookupLocalStorage: 'i18nextLng',

          caches: [ 'localStorage' ],
        },
        ns: [ 'common' ],
      });
}

export function localeIdFactory(i18next: ITranslationService) {
  return i18next.language;
}

export const I18N_PROVIDERS = [
  {
    provide: APP_INITIALIZER,
    useFactory: appInit,
    deps: [ I18NEXT_SERVICE ],
    multi: true,
  },
  {
    provide: LOCALE_ID,
    deps: [ I18NEXT_SERVICE ],
    useFactory: localeIdFactory,
  },
];
