/*
 * Copyright 2021 The PartChain Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Pipe, PipeTransform } from '@angular/core';

/**
 *
 *
 * @export
 * @class DatePipe
 * @implements {PipeTransform}
 */
@Pipe({ name: 'appAssetDate' })
export class AssetDatePipe implements PipeTransform {
  /**
   * Remove time from date
   *
   * @param {string} timestamp
   * @return {string}
   * @memberof DatePipe
   */
  transform(timestamp: string): string {
    if (!timestamp) {
      return '';
    }
    const date: string = timestamp.split('T')[0];
    const time: string = timestamp.split('T')[1].split('.')[0];
    const splittedDate = date.split('-');
    return `${splittedDate[2]}/${splittedDate[1]}/${splittedDate[0]}, ${time}`;
  }
}
