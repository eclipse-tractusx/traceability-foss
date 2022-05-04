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

import { Injectable, OnDestroy } from '@angular/core';
import { Observable, Subject, Subscriber } from 'rxjs';

/**
 *
 *
 * @export
 * @class DestroyService
 * @extends {Observable<void>}
 * @implements {OnDestroy}
 */
@Injectable({
  providedIn: 'root',
})
export class DestroyService extends Observable<void> implements OnDestroy {
  /**
   * Component lifespan
   *
   * @private
   * @readonly
   * @type {Subject<void>}
   * @memberof DestroyService
   */
  private readonly lifeSpan$ = new Subject<void>();

  /**
   * @constructor DestroyService
   * @memberof DestroyService
   */
  constructor() {
    super((subscriber: Subscriber<void>) => this.lifeSpan$.subscribe(subscriber));
  }

  /**
   * Angular lifecycle method - Ng On Destroy
   *
   * @return {void}
   * @memberof DestroyService
   */
  ngOnDestroy(): void {
    this.lifeSpan$.next();
    this.lifeSpan$.complete();
  }
}
