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

import { TestBed } from '@angular/core/testing';
import { TableSettingsService } from '@core/user/table-settings.service';
import { TableType } from '@shared/components/multi-select-autocomplete/table-type.model';
import { TableViewConfig } from '@shared/components/parts-table/table-view-config.model';

describe('TableSettingsService', () => {
  let service: TableSettingsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [],
      providers: [ TableSettingsService ],
    });
    service = TestBed.inject(TableSettingsService);
  });


  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('should correctly initialize the table view settings', () => {

    it('should return PartsAsPlannedConfigurationModel for AS_PLANNED_OWN', () => {
      const result: TableViewConfig = service.initializeTableViewSettings(TableType.AS_PLANNED_OWN);
      expect(result.displayedColumns.length).toBe(21);
    });

    it('should return PartsAsBuiltConfigurationModel for AS_BUILT_OWN', () => {
      const result: TableViewConfig = service.initializeTableViewSettings(TableType.AS_BUILT_OWN);
      expect(result.displayedColumns.length).toBe(23);
    });

    it('should return NotificationsSentConfigurationModel for SENT_NOTIFICATION', () => {
      const result: TableViewConfig = service.initializeTableViewSettings(TableType.SENT_NOTIFICATION);
      expect(result.displayedColumns.length).toBe(10);
    });

    it('should return NotificationsReceivedConfigurationModel for RECEIVED_NOTIFICATION', () => {
      const result: TableViewConfig = service.initializeTableViewSettings(TableType.RECEIVED_NOTIFICATION);
      expect(result.displayedColumns.length).toBe(10);
    });
  });

  describe('YourService', () => {

    it('should return false if storage is empty', () => {
      spyOn(service, 'getStoredTableSettings').and.returnValue(null);
      const tableViewConfig: TableViewConfig = {
        displayedColumns: [],
        filterFormGroup: null,
        sortableColumns: {},
        displayFilterColumnMappings: [],
        filterColumns: [],
      };
      const result = service.storedTableSettingsInvalid(tableViewConfig, TableType.AS_PLANNED_OWN);
      expect(result).toBe(false);
    });

    it('should return false if stored columns match displayed columns', () => {
      const storage = {
        [TableType.AS_PLANNED_OWN]: {
          columnsForDialog: [ 'col1', 'col2', 'menu' ], // Sample stored columns
        },
      };
      spyOn(service, 'getStoredTableSettings').and.returnValue(storage);
      const tableViewConfig: TableViewConfig = {
        displayedColumns: [],
        filterFormGroup: null,
        sortableColumns: {},
        displayFilterColumnMappings: [],
        filterColumns: [],
      };
      tableViewConfig.displayedColumns = [ 'col1', 'col2', 'menu' ]; // Sample displayed columns
      const result = service.storedTableSettingsInvalid(tableViewConfig, TableType.AS_PLANNED_OWN);
      expect(result).toBe(false);
    });

    it('should return true if stored columns do not match displayed columns', () => {
      const storage = {
        [TableType.AS_PLANNED_OWN]: {
          columnsForDialog: [ 'col1', 'col2', 'menu' ], // Sample stored columns
        },
      };
      spyOn(service, 'getStoredTableSettings').and.returnValue(storage);
      const tableViewConfig: TableViewConfig = {
        displayedColumns: [],
        filterFormGroup: null,
        sortableColumns: {},
        displayFilterColumnMappings: [],
        filterColumns: [],
      };
      tableViewConfig.displayedColumns = [ 'col1', 'col3', 'menu' ]; // Different displayed columns
      const result = service.storedTableSettingsInvalid(tableViewConfig, TableType.AS_PLANNED_OWN);
      expect(result).toBe(true);
    });

    it('should show warning toast and remove storage if settings are invalid', () => {
      const storage = {
        [TableType.AS_PLANNED_OWN]: {
          columnsForDialog: [ 'col1', 'col2', 'menu' ], // Sample stored columns
        },
      };
      spyOn(service, 'getStoredTableSettings').and.returnValue(storage);
      spyOn(service['toastService'], 'warning');
      spyOn(localStorage, 'removeItem');

      const tableViewConfig: TableViewConfig = {
        displayedColumns: [],
        filterFormGroup: null,
        sortableColumns: {},
        displayFilterColumnMappings: [],
        filterColumns: [],
      };
      tableViewConfig.displayedColumns = [ 'col1', 'col3', 'menu' ]; // Different displayed columns
      service.storedTableSettingsInvalid(tableViewConfig, TableType.AS_PLANNED_OWN);

      expect(service['toastService'].warning).toHaveBeenCalledWith('table.tableSettings.invalid', 10000);
      expect(localStorage.removeItem).toHaveBeenCalled();
    });
  });



});
