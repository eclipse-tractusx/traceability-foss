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

  constructor(@Inject(I18NEXT_SERVICE) private readonly i18NextService: ITranslationService) {
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
  }
}
