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

import { Component, Inject, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { KnownLocale } from '@core/i18n/global-i18n.providers';
import { BaseInputComponent } from '@shared/abstraction/baseInput/baseInput.component';
import { StaticIdService } from '@shared/service/staticId.service';
import { I18NEXT_SERVICE, ITranslationService } from 'angular-i18next';
import { Observable } from 'rxjs';
import { distinctUntilChanged, map } from 'rxjs/operators';

@Component({
  selector: 'app-dateTime',
  templateUrl: './dateTime.component.html',
})
export class DateTimeComponent extends BaseInputComponent {
  public language$: Observable<'en-GB' | 'de-DE'>;

  constructor(
    @Self() ngControl: NgControl,
    staticIdService: StaticIdService,
    @Inject(I18NEXT_SERVICE) i18NextService: ITranslationService,
  ) {
    super(ngControl, staticIdService);
    this.language$ = i18NextService.events.languageChanged.pipe(
      distinctUntilChanged(),
      map((language: KnownLocale) => (language === 'en' ? 'en-GB' : 'de-DE')),
    );
  }
}
