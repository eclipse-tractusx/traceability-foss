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

import { Location } from '@angular/common';
import { Component, Inject, OnDestroy } from '@angular/core';
import { ALL_KNOWN_LOCALES, KnownLocale } from '@core/i18n/global-i18n.providers';
import { I18NEXT_SERVICE, ITranslationService } from 'angular-i18next';
import { Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';

interface LocaleEntry {
  locale: KnownLocale;
  label: string;
}

@Component({
  selector: 'app-language-selector',
  templateUrl: './language-selector.component.html',
  styleUrls: ['./language-selector.component.scss'],
})
export class LanguageSelectorComponent implements OnDestroy {
  public readonly locales: LocaleEntry[];
  public readonly languageChangedSubscription: Subscription;

  public currentLocale: KnownLocale;

  constructor(@Inject(I18NEXT_SERVICE) private readonly i18NextService: ITranslationService, private readonly location: Location) {
    const supportedLanguages = this.i18NextService.options.supportedLngs || [];

    this.locales = (Object.entries(ALL_KNOWN_LOCALES) as [KnownLocale, string][])
      .filter(([locale]) => supportedLanguages.includes(locale))
      .map(([locale, label]) => ({
        locale,
        label,
      }));

    this.currentLocale = this.i18NextService.resolvedLanguage as KnownLocale;
    this.languageChangedSubscription = this.i18NextService.events.languageChanged
      .pipe(filter(languages => !!languages))
      .subscribe((language: KnownLocale) => (this.currentLocale = language));
  }

  public ngOnDestroy(): void {
    this.languageChangedSubscription.unsubscribe();
  }

  public handleClick(localeId: KnownLocale): void {
    void this.i18NextService.changeLanguage(localeId);
    this.location.historyGo(0);
  }
}
