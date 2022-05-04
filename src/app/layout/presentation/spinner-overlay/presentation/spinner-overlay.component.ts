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
import { Observable } from 'rxjs';
import { SpinnerOverlayService } from '../core/spinner-overlay.service';

/**
 *
 *
 * @export
 * @class SpinnerOverlayComponent
 */
@Component({
  selector: 'app-spinner-overlay',
  templateUrl: './spinner-overlay.component.html',
  styleUrls: ['./spinner-overlay.component.scss'],
})
export class SpinnerOverlayComponent {
  /**
   * Is loading state active
   *
   * @type {boolean}
   * @memberof SpinnerOverlayComponent
   */
  public isOverlayLoading$: Observable<boolean>;

  /**
   * @constructor SpinnerOverlayComponent
   * @param {SpinnerOverlayService} spinnerOverlyService
   * @memberof SpinnerOverlayComponent
   */
  constructor(private spinnerOverlyService: SpinnerOverlayService) {
    this.isOverlayLoading$ = this.spinnerOverlyService.getIsOverlayShowing$;
  }
}
