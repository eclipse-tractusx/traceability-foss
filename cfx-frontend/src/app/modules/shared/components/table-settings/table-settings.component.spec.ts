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
import { APP_INITIALIZER } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TableSettingsService } from '@core/user/table-settings.service';
import { TableType } from '@shared/components/multi-select-autocomplete/table-type.model';
import { SharedModule } from '@shared/shared.module';
import { I18NEXT_SERVICE, I18NextModule, ITranslationService } from 'angular-i18next';
import { TableSettingsComponent } from './table-settings.component';

describe('TableSettingsComponent', () => {
  let component: TableSettingsComponent;
  let fixture: ComponentFixture<TableSettingsComponent>;
  let tableSettingsService: TableSettingsService;

  beforeEach(async () => {
    const tableSettingsServiceSpy = jasmine.createSpyObj('TableSettingsService', [
      'getStoredTableSettings',
      'storeTableSettings',
      'emitChangeEvent',
      'getEvent',
    ]);
    tableSettingsServiceSpy.getStoredTableSettings.and.callFake(() => {
      return {
        [TableType.AS_BUILT_OWN]: {
          columnSettingsOptions: new Map<string, boolean>(),
          columnsForDialog: [ 'column1', 'column2' ],
          columnsForTable: [ 'column1' ],
          filterColumnsForTable: [ 'filtercolumn1' ],
        },
      };
    });

    tableSettingsServiceSpy.storeTableSettings.and.callFake((partTableType, tableSettingsList) => {
      return;
    });

    TestBed.configureTestingModule({
      declarations: [ TableSettingsComponent ],
      imports: [
        SharedModule,
        I18NextModule.forRoot(),
      ],
      providers: [
        {
          provide: MatDialogRef,
          useValue: {
            close: () => {
            },
          },
        },
        {
          provide: MAT_DIALOG_DATA,
          useValue: {
            title: 'Test Title',
            panelClass: 'test-dialog',
            tableType: TableType.AS_BUILT_OWN,
            defaultColumns: [ 'column1', 'column2' ],
            defaultFilterColumns: [ 'filtercolumn1', 'filtercolumn2' ],
          },
        },
        {
          provide: TableSettingsService,
          useValue: tableSettingsServiceSpy,
        },
        {
          provide: APP_INITIALIZER,
          useFactory: (i18next: ITranslationService) => {
            return () =>
              i18next.init({
                lng: 'en',
                supportedLngs: [ 'en', 'de' ],
                resources: {},
              });
          },
          deps: [ I18NEXT_SERVICE ],
          multi: true,
        },
      ],
    });

    await TestBed.compileComponents();
    fixture = TestBed.createComponent(TableSettingsComponent);
    component = fixture.componentInstance;
    tableSettingsService = TestBed.inject(TableSettingsService);
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize the component properties', () => {
    // Assert that component properties are correctly initialized based on MAT_DIALOG_DATA
    expect(component.title).toEqual('Test Title');
    expect(component.panelClass).toEqual('test-dialog');
    expect(component.tableType).toEqual(TableType.AS_BUILT_OWN);
    expect(component.defaultColumns).toEqual([ 'column1', 'column2' ]);
    expect(component.defaultFilterColumns).toEqual([ 'filtercolumn1', 'filtercolumn2' ]);
  });

  it('should call save method and update tableSettingsService', () => {
    const columnOptions = new Map<string, boolean>();
    columnOptions.set('column1', true);

    component.handleCheckBoxChange('column1', true);
    component.save();

    // Check that setColumnVisibilitySettings was called with the updated settings
    expect(tableSettingsService.storeTableSettings).toHaveBeenCalledWith({
      [TableType.AS_BUILT_OWN]: {
        columnSettingsOptions: columnOptions,
        columnsForDialog: [ 'column1', 'column2' ],
        columnsForTable: [ 'column1' ],
        filterColumnsForTable: [ 'filtercolumn1' ],
      },
    });
  });

  it('should handle sort list item correctly', () => {
    component.dialogColumns = [ 'column1', 'column2', 'column3' ];
    component.selectedColumn = 'column3';

    component.handleSortListItem('up');

    expect(component.dialogColumns).toEqual([ 'column1', 'column3', 'column2' ]);
  });

  it('should reset columns', () => {
    component.dialogColumns = [ 'column1', 'column2', 'column3' ];

    component.resetColumns();

    expect(component.dialogColumns).toEqual([ 'column1', 'column2' ]);
    expect(component.selectAllSelected).toBe(true);
  });

  it('should close the dialog', () => {
    spyOn(component.dialogRef, 'close');
    component.dialogRef.close();
    expect(component.dialogRef.close).toHaveBeenCalled();
  });
});
