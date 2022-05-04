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

import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, Subject } from 'rxjs';

/**
 * https://indepth.dev/posts/1297/building-a-reusable-menu-component
 *
 * @export
 * @class MenuStateService
 */
@Injectable({
  providedIn: 'root',
})
export class MenuStateService {
  /**
   * Menu state observable
   *
   * @type {Observable<void>}
   * @memberof MenuStateService
   */
  public state$: Observable<void>;

  /**
   * Menu state subject
   *
   * @private
   * @type {Subject<void>}
   * @memberof MenuStateService
   */
  private state = new Subject<void>();

  /**
   * Menu state id
   *
   * @private
   * @type {BehaviorSubject<string>}
   * @memberof MenuStateService
   */
  public menuId = new BehaviorSubject<string>(null);

  /**
   * @constructor MenuStateService
   * @memberof MenuStateService
   */
  constructor() {
    this.state$ = this.state.asObservable();
  }

  /**
   * Clear menu
   *
   * @returns {void}
   * @memberof MenuStateService
   */
  public clearMenu(): void {
    this.state.next();
  }
}
