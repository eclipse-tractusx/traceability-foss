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

import { Component } from '@angular/core';

/**
 *
 *
 * @export
 * @class ReceivedAlertEmptyStateComponent
 */
@Component({
  selector: 'app-received-alert-empty-state',
  templateUrl: './received-alert-empty-state.component.html',
  styleUrls: ['./received-alert-empty-state.component.scss'],
})
export class ReceivedAlertEmptyStateComponent {
  /**
   * Empty state list
   *
   * @type {Array}
   * @memberof ReceivedAlertEmptyStateComponent
   */
  public listEmptyState = new Array(5).fill(null);
}
