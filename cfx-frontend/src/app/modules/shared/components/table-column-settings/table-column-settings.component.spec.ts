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
import { SharedModule } from '@shared/shared.module';
import { I18NEXT_SERVICE, I18NextModule, ITranslationService } from 'angular-i18next';
import { TableColumnSettingsComponent } from './table-column-settings.component';
import { TableType } from '../multi-select-autocomplete/table-type.model';

describe('TableSettingsComponent', () => {
  let component: TableColumnSettingsComponent;
  let fixture: ComponentFixture<TableColumnSettingsComponent>;
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
          columnsForDialog: ['column1', 'column2'],
          columnsForTable: ['column1'],
          filterColumnsForTable: ['filterColumn1'],
        },
      };
    });

    tableSettingsServiceSpy.storeTableSettings.and.callFake((TableType, tableSettingsList) => {
      return;
    });

    TestBed.configureTestingModule({
      declarations: [TableColumnSettingsComponent],
      imports: [
        SharedModule,
        I18NextModule.forRoot(),
      ],
      providers: [
        {
          provide: MatDialogRef,
          useValue: {
            close: () => { },
          },
        },
        {
          provide: MAT_DIALOG_DATA,
          useValue: {
            title: 'Test Title',
            panelClass: 'test-dialog',
            tableType: TableType.AS_BUILT_OWN,
            defaultColumns: ['column1', 'column2'],
            defaultFilterColumns: ['filterColumn1', 'filterColumn2'],
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
                supportedLngs: ['en', 'de'],
                resources: {},
              });
          },
          deps: [I18NEXT_SERVICE],
          multi: true,
        },
      ],
    });

    await TestBed.compileComponents();
    fixture = TestBed.createComponent(TableColumnSettingsComponent);
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
    expect(component.defaultColumns).toEqual(['column1', 'column2']);
    expect(component.defaultFilterColumns).toEqual(['filterColumn1', 'filterColumn2']);
    expect(component.isCustomerTable).toEqual(false);
  });

  it('should call save method and update tableSettingsService', async () => {
    const columnOptions = new Map<string, boolean>();
    columnOptions.set('column1', true);

    component.handleCheckBoxChange('column1', true);
    await component.save();

    // Check that setColumnVisibilitySettings was called with the updated settings
    expect(tableSettingsService.storeTableSettings).toHaveBeenCalledWith({
      [TableType.AS_BUILT_OWN]: {
        columnSettingsOptions: columnOptions,
        columnsForDialog: ['column1', 'column2'],
        columnsForTable: ['column1'],
        filterColumnsForTable: ['filterColumn1'],
      },
    });
  });

  it('should reset columns', () => {
    component.dialogColumns = ['column1', 'column2', 'column3'];

    component.resetColumns();

    expect(component.dialogColumns).toEqual(['column1', 'column2']);
    expect(component.selectAllSelected).toBe(true);
  });

  it('should close the dialog', () => {
    spyOn(component.dialogRef, 'close');
    component.dialogRef.close()
    expect(component.dialogRef.close).toHaveBeenCalled();
  });
});
