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

import { Pipe, PipeTransform } from '@angular/core';
import { I18nMessage } from '@shared/model/i18n-message';
import { I18NextPipe, PipeOptions } from 'angular-i18next';

// To make this pipe pure, reload the page after the language was changed.
// Keeping it "impure" leads to a lot of unnecessary renders

// Currently we use a workaround (in languageswitcher component) so that the whole page gets refreshed when the user
// is switching languages. This way we keep the performance and the pipe pure
@Pipe({ name: 'i18n', pure: true })
export class I18nPipe implements PipeTransform {
  constructor(private readonly i18NextPipe: I18NextPipe) {}

  public transform(key: I18nMessage, options?: PipeOptions): string {
    if (!key) return '';

    if (typeof key !== 'string') {
      options = key.values;
      key = key.id;
    }

    if (key.indexOf(':') !== -1) return key;

    return this.i18NextPipe.transform(key, options);
  }
}
