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
 * @class ShortenPipe
 * @implements {PipeTransform}
 */
@Pipe({
  name: 'shortenSerialNumber',
})
export class ShortenPipe implements PipeTransform {
  /**
   * Pipe transform method
   *
   * @param {string} serialNumber
   * @return {string}
   * @memberof ShortenPipe
   */
  transform(serialNumber: string): string {
    if (serialNumber) {
      return serialNumber.length > 23
        ? `${serialNumber.substring(0, 10)} ... ${serialNumber.substring(serialNumber.length - 10)}`
        : serialNumber;
    }
  }
}
