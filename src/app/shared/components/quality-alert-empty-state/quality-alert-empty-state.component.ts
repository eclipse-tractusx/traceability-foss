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
 * @class QualityAlertEmptyStateComponent
 */
@Component({
  selector: 'app-quality-alert-empty-state',
  templateUrl: './quality-alert-empty-state.component.html',
  styleUrls: ['./quality-alert-empty-state.component.scss'],
})
export class QualityAlertEmptyStateComponent {
  /**
   * Empty state title
   *
   * @type {string}
   * @memberof InvestigationsEmptyStateComponent
   */
  @Input() title: string;

  /**
   * Empty state message
   *
   * @type {string}
   * @memberof InvestigationsEmptyStateComponent
   */
  @Input() message: string;

  @Input() imageUrl: string;
}
