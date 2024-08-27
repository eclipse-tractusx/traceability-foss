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

import { Component, EventEmitter, Inject, Output, } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TableViewSettings } from '@core/user/table-settings.model';
import { TableSettingsService } from '@core/user/table-settings.service';
import {
  CdkDragDrop,
  moveItemInArray,
  transferArrayItem,
} from '@angular/cdk/drag-drop';
import { TableType } from '../multi-select-autocomplete/table-type.model';

@Component({
  selector: 'app-table-column-settings',
  templateUrl: 'table-column-settings.component.html',
  styleUrls: ['table-column-settings.component.scss'],
})
export class TableColumnSettingsComponent {

  @Output() changeSettingsEvent = new EventEmitter<void>();

  title: string;
  panelClass: string;

  tableType: TableType;
  defaultColumns: string[];
  defaultFilterColumns: string[];

  columnOptions: Map<string, boolean>;
  dialogColumns: string[];
  tableColumns: string[];
  filterColumns: string[];

  selectAllSelected: boolean;
  selectedColumn: string = null;

  isCustomerTable: boolean;

  dragActive = false;

  constructor(public dialogRef: MatDialogRef<TableColumnSettingsComponent>, @Inject(MAT_DIALOG_DATA) public data: any, public readonly tableSettingsService: TableSettingsService) {
    // Layout
    this.title = data.title;
    this.panelClass = data.panelClass;
    this.isCustomerTable = data.tableType === TableType.AS_BUILT_CUSTOMER || data.tableType === TableType.AS_PLANNED_CUSTOMER;
    // Passed Data
    this.tableType = data.tableType;
    this.defaultColumns = data.defaultColumns;
    this.defaultFilterColumns = data.defaultFilterColumns;

    this.loadSettings(tableSettingsService, data);

    if (dialogRef?.afterClosed)
      dialogRef.afterClosed().subscribe(() => {
        this.save();
      });
  }

  loadSettings(tableSettingsService: TableSettingsService, data: any): void {
    const storedSettings = tableSettingsService.getStoredTableSettings();
    const tableSettings = storedSettings?.[this.tableType];

    if (tableSettings) {
      // Storage Data
      this.columnOptions = tableSettings.columnSettingsOptions;
      this.dialogColumns = tableSettings.columnsForDialog;
      this.tableColumns = tableSettings.columnsForTable;
      this.filterColumns = tableSettings.filterColumnsForTable;
    } else {
      this.dialogColumns = data.defaultColumns;
      this.tableColumns = data.defaultColumns;
      this.filterColumns = data.defaultColumns;
      this.columnOptions = new Map<string, boolean>();
      this.selectAll(true);
    }

    this.selectAllSelected = this.dialogColumns.length === this.tableColumns.length;
  }

  save() {
    // build new tableColumns how they should be displayed
    const newTableColumns: string[] = [];
    const newTableFilterColumns: string[] = [];
    // iterate over dialogColumns
    for (const column of this.dialogColumns) {
      // if item in dialogColumns is true in columnOptions --> add to new tableColumns
      if (this.columnOptions.get(column) || column === 'select' || column === 'menu' || column === 'settings') {
        newTableColumns.push(column);
        // ignore select column in customertable
        if ((column === 'select') && !this.isCustomerTable) {
          newTableFilterColumns.push('Filter');
        } else {
          newTableFilterColumns.push('filter' + column.charAt(0).toUpperCase() + column.slice(1));
        }
      }
    }

    // get Settingslist
    const tableSettingsList = this.tableSettingsService.getStoredTableSettings();

    // set this tableType Settings from SettingsList to the new one
    tableSettingsList[this.tableType] = {
      columnSettingsOptions: this.columnOptions,
      columnsForDialog: this.dialogColumns,
      columnsForTable: newTableColumns,
      filterColumnsForTable: newTableFilterColumns
    } as TableViewSettings;

    // save all values back to localstorage
    this.tableSettingsService.storeTableSettings(tableSettingsList);

    // trigger action that table will refresh
    this.tableSettingsService.emitChangeEvent();
  }

  handleCheckBoxChange(item: string, isChecked: boolean) {
    this.columnOptions.set(item, isChecked);
  }

  handleListItemClick(event: MouseEvent, item: string) {
    const element = event.target as HTMLElement;

    if (element.tagName !== 'INPUT') {
      this.selectedColumn = item;
      element.classList.toggle('selected-item');
    }
  }

  dragging(state: boolean) {
    this.dragActive = state;
  }

  selectAll(isChecked: boolean) {
    for (const column of this.dialogColumns) {
      if (column === 'select' || column === 'menu' || column === 'settings') {
        continue;
      }
      this.columnOptions.set(column, isChecked);
    }
    this.selectAllSelected = true;
  }

  resetColumns() {
    this.dialogColumns = [...this.defaultColumns];
    this.selectAll(true);
  }

  drop(event: CdkDragDrop<string[]>) {
    const offset = event.container.data.includes('select') ? 1 : 0;
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex + offset, event.currentIndex + offset);
    } else {
      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex,
      );
    }
  }
}
