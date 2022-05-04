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

import { Component, Input } from '@angular/core';

/**
 *
 *
 * @export
 * @class ResizerComponent
 */
@Component({
  selector: 'app-resizer',
  templateUrl: './resizer.component.html',
  styleUrls: ['./resizer.component.scss'],
})
export class ResizerComponent {
  /**
   * is expanded
   *
   * @type {boolean}
   * @memberof ResizerComponent
   */
  @Input() expanded: boolean;

  /**
   * Icon name
   *
   * @readonly
   * @type {string}
   * @memberof ResizerComponent
   */
  get icon(): string {
    return this.expanded ? 'arrow-left-s-fill' : 'arrow-right-s-fill';
  }
}
