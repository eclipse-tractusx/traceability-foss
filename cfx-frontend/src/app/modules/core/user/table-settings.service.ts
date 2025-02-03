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
import { TableType } from '@shared/components/multi-select-autocomplete/table-type.model';
import { TableViewConfig } from '@shared/components/parts-table/table-view-config.model';
import { Subject } from 'rxjs';
import { UserService } from './user.service';

@Injectable({
  providedIn: 'root',
})
export class TableSettingsService {
  private settingsKey = 'TableViewSettings';
  private changeEvent = new Subject<void>();

  constructor(private userService: UserService) { }

  storeTableSettings(tableSettingsList: any): void {
    const { username } = this.userService;

    // before setting anything, all maps in new tableSettingList should be stringified
    Object.keys(tableSettingsList).forEach(tableSetting => {
      const newMap = tableSettingsList[tableSetting].columnSettingsOptions;
      tableSettingsList[tableSetting].columnSettingsOptions = JSON.stringify(Array.from(newMap.entries()));
    });

    localStorage.setItem(`${this.settingsKey}_${username}`, JSON.stringify(tableSettingsList));
  }

  // this returns whole settings whether empty / not for part / etc.
  getStoredTableSettings(): any {
    const { username } = this.userService;

    const settingsJson = localStorage.getItem(`${this.settingsKey}_${username}`);
    const settingsObject = settingsJson ? JSON.parse(settingsJson) : null;
    if (!settingsObject) {
      return;
    }

    // iterate through all tabletypes and parse columnSettingsOption to a map
    Object.keys(settingsObject).forEach(tableSetting => {
      settingsObject[tableSetting].columnSettingsOptions = new Map(JSON.parse(settingsObject[tableSetting].columnSettingsOptions));
    });

    return settingsObject;
  }

  storedTableSettingsInvalid(tableViewConfig: TableViewConfig, tableType: TableType): boolean {
    let isInvalid = false;

    const storage = this.getStoredTableSettings();

    if (!storage?.[tableType]) {
      return false;
    }
    const storageElement = storage[tableType];

    if (!storageElement?.columnsForDialog) {
      return false;
    }

    if (tableViewConfig.displayedColumns.length !== storageElement.columnsForDialog.length) {
      isInvalid = true;
    }
    for (const col of tableViewConfig.displayedColumns.values()) {
      if (col !== 'menu' && !storageElement.columnsForDialog.includes(col)) {
        isInvalid = true;
      }
    }
    for (const col of storageElement.columnsForDialog) {
      if (col !== 'menu' && !tableViewConfig.displayedColumns.includes(col)) {
        isInvalid = true;
      }
    }
    if (isInvalid) {
      localStorage.removeItem(this.settingsKey);
    }
    return isInvalid;
  }

  emitChangeEvent() {
    this.changeEvent.next();
  }

  getEvent() {
    return this.changeEvent.asObservable();
  }
}
