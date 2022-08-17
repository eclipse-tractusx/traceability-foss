/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
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

import { Pipe, PipeTransform } from '@angular/core';
import { I18nMessage } from '@shared/model/i18n-message';
import { I18NextPipe, PipeOptions } from 'angular-i18next';

@Pipe({ name: 'i18n', pure: false })
export class I18nPipe implements PipeTransform {
  constructor(private readonly i18NextPipe: I18NextPipe) {}

  public transform(key: I18nMessage, options?: PipeOptions): string {
    if (!key) {
      return '';
    }

    if (typeof key !== 'string') {
      options = key.values;
      key = key.id;
    }

    return this.i18NextPipe.transform(key, options);
  }
}
