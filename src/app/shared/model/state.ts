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

import { BehaviorSubject, Observable } from 'rxjs';
import { distinctUntilChanged } from 'rxjs/operators';

/**
 *
 *
 * @export
 * @class State
 * @template T
 */
export class State<T> {
  /**
   * Generic store
   *
   * @private
   * @readonly
   * @type {BehaviorSubject<T>}
   * @memberof State
   */
  private readonly store$: BehaviorSubject<T>;

  /**
   * State initial value
   *
   * @private
   * @type {T}
   * @memberof State
   */
  private initialValue: T;

  /**
   * State value
   *
   * @private
   * @type {T}
   * @memberof State
   */
  private stateValue: T;

  /**
   * Creates an instance of State.
   * @param {T} initialValue
   * @memberof State
   */
  constructor(initialValue: T) {
    this.initialValue = initialValue;
    this.stateValue = initialValue;
    this.store$ = new BehaviorSubject<T>(initialValue);
  }

  /**
   * State observable
   *
   * @readonly
   * @type {Observable<T>}
   * @memberof State
   */
  get observable(): Observable<T> {
    return this.store$.asObservable().pipe(distinctUntilChanged());
  }

  /**
   * State snapshot
   *
   * @readonly
   * @type {T}
   * @memberof State
   */
  get snapshot(): T {
    return this.stateValue;
  }

  /**
   * State setter
   *
   * @param {T} value
   * @return {void}
   * @memberof State
   */
  public update(value: T): void {
    this.stateValue = value;
    this.store$.next(this.stateValue);
  }

  /**
   * Reset state
   *
   * @return {void}
   * @memberof State
   */
  public reset(): void {
    this.stateValue = this.initialValue;
    this.store$.next(this.stateValue);
  }
}
