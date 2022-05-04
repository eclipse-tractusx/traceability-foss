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

@Component({
  selector: 'app-avatar',
  templateUrl: './avatar.component.html',
})
export class AvatarComponent {
  /**
   * Avatar size
   *
   * @type {string}
   * @memberof AvatarComponent
   */
  @Input() size: string;

  /**
   * Initials size
   *
   * @type {string}
   * @memberof AvatarComponent
   */
  @Input() initialsTopSize: string;

  /**
   * Avatar name
   *
   * @type {string}
   * @memberof AvatarComponent
   */
  @Input() name: string;

  /**
   * Avatar color
   *
   * @type {string}
   * @memberof AvatarComponent
   */
  @Input() color: string;

  /**
   * Avatar image
   *
   * @type {string}
   * @memberof AvatarComponent
   */
  @Input() imageUrl: string;

  /**
   * Circle scss style
   *
   * @return {*}  {{
   *     'background-color': string;
   *     'border-radius': string;
   *     height: string;
   *     'text-align': string;
   *     width: string;
   *     overflow: string;
   *   }}
   * @memberof AvatarComponent
   */
  public circleStyle(): {
    'background-color': string;
    'border-radius': string;
    height: string;
    'text-align': string;
    width: string;
    overflow: string;
  } {
    return {
      'background-color': this.color,
      'border-radius': '50%',
      height: this.size,
      'text-align': 'center',
      width: this.size,
      overflow: 'hidden',
    };
  }

  /**
   * Span css style
   *
   * @return {*}  {{
   *     'font-size': string;
   *     'line-height': string;
   *     position: string;
   *     top: string;
   *     color: string;
   *   }}
   * @memberof AvatarComponent
   */
  public spanStyle(): {
    'font-size': string;
    'line-height': string;
    position: string;
    top: string;
    color: string;
  } {
    return {
      'font-size': 'calc(this.size / 2)',
      'line-height': 'calc(this.size / 2)',
      position: 'relative',
      top: this.initialsTopSize,
      color: '#fff',
    };
  }
}
