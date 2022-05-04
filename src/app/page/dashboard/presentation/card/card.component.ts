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

import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

/**
 *
 *
 * @export
 * @class CardComponent
 */
@Component({
  selector: 'app-card',
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CardComponent {
  /**
   * Card id
   *
   * @type {number}
   * @memberof CardComponent
   */
  @Input() id: number;

  /**
   * Card label
   *
   * @type {string}
   * @memberof CardComponent
   */
  @Input() label: string;

  /**
   * Card stats
   *
   * @type {View<Stats>}
   * @memberof CardComponent
   */
  @Input() stats: number | string;

  /**
   * Card icon
   *
   * @type {string}
   * @memberof CardComponent
   */
  @Input() icon: string;
}
