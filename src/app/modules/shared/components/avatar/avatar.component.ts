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

import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-avatar',
  templateUrl: './avatar.component.html',
  styleUrls: ['./avatar.component.scss'],
})
export class AvatarComponent {
  @Input() size: string;
  @Input() initialsTopSize: string;
  @Input() name = '';
  @Input() color: string;
  @Input() imageUrl: string;

  public circleStyle(): {
    'background-color': string;
    height: string;
    width: string;
  } {
    return {
      'background-color': this.color,
      height: this.size,
      width: this.size,
    };
  }

  public spanStyle(): {
    'font-size': string;
    'line-height': string;
    top: string;
  } {
    return {
      'font-size': `calc(${this.size} / 2)`,
      'line-height': `calc(${this.size} / 2)`,
      top: this.initialsTopSize,
    };
  }
}
