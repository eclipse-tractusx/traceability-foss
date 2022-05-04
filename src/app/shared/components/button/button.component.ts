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

import { Component, EventEmitter, Input, Output } from '@angular/core';

/**
 *
 *
 * @export
 * @class ButtonComponent
 */
@Component({
  selector: 'app-button',
  templateUrl: './button.component.html',
  styleUrls: ['./button.component.scss'],
})
export class ButtonComponent {
  /**
   * Button css type (primary / secondary)
   *
   * @type {string}
   * @memberof ButtonComponent
   */
  @Input() button: string;

  /**
   * Button icon
   *
   * @type {string}
   * @memberof ButtonComponent
   */
  @Input() icon: string;

  /**
   * Control types of button ['button', 'submit', 'reset']
   *
   * @type {string}
   * @memberof ButtonComponent
   */
  @Input() type: string;

  /**
   * Is button disable
   *
   * @type {boolean}
   * @memberof ButtonComponent
   */
  @Input() disable: boolean;

  /**
   * Button click event
   *
   * @type {EventEmitter<ButtonComponent>}
   * @memberof ButtonComponent
   */
  @Output() clickEvent: EventEmitter<MouseEvent> = new EventEmitter<MouseEvent>();
}
