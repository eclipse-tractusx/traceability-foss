/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

import { Injectable } from '@angular/core';
import { BomLifecycleSize } from '@shared/components/bom-lifecycle-activator/bom-lifecycle-activator.model';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class BomLifecycleSettingsService {
  private readonly DEFAULT: BomLifecycleSize = {
    asBuiltSize: 50,
    asPlannedSize: 50,
  };
  private PART_TABLE_KEY = 'PART';

  private _settingsSubject = new BehaviorSubject<BomLifecycleSize>(this.DEFAULT);

  public settings$ = this._settingsSubject.asObservable();

  constructor() {
    const settingsJson = localStorage.getItem(this.PART_TABLE_KEY);
    if (settingsJson) {
      this._settingsSubject.next(JSON.parse(settingsJson));
    }
  }

  getUserSettings(): BomLifecycleSize {
    return this._settingsSubject.getValue();
  };

  setUserSettings(settings: BomLifecycleSize): void {
    localStorage.setItem(this.PART_TABLE_KEY, JSON.stringify(settings));
    this._settingsSubject.next(settings);
  }

  clearUserSettings(): void {
    localStorage.removeItem(this.PART_TABLE_KEY);
    this._settingsSubject.next(this.DEFAULT);
  }
}
