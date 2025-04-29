/********************************************************************************
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DigitalTwinPartComponent } from './digital-twin-part.component';
import { DigitalTwinPartFacade } from '../core/digital-twin-part.facade';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { View } from '@shared/model/view.model';
import { Pagination } from '@core/model/pagination.model';
import { DigitalTwinPartResponse } from '@page/digital-twin-part/model/digitalTwinPart.model';
import { ReactiveFormsModule } from '@angular/forms';
import { TableEventConfig } from '@shared/components/table/table.model';
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'i18n' })
class MockI18nPipe implements PipeTransform {
  transform(value: string): string {
    return value;
  }
}

describe('DigitalTwinPartComponent', () => {
  let component: DigitalTwinPartComponent;
  let fixture: ComponentFixture<DigitalTwinPartComponent>;
  let facade: jasmine.SpyObj<DigitalTwinPartFacade>;
  let router: jasmine.SpyObj<Router>;

  const mockView: View<Pagination<DigitalTwinPartResponse>> = {
    data: {
      page: 0,
      pageSize: 50,
      pageCount: 1,
      totalItems: 1,
      content: []
    }
  };

  beforeEach(async () => {
    const facadeMock = jasmine.createSpyObj('DigitalTwinPartFacade', [
      'setDigitalTwinParts',
      'openConfigurationDialog'
    ]);

    (facadeMock as any).digitalTwinParts$ = of(mockView);

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [DigitalTwinPartComponent, MockI18nPipe],
      providers: [
        { provide: DigitalTwinPartFacade, useValue: facadeMock },
        { provide: Router, useValue: jasmine.createSpyObj('Router', ['navigate']) }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DigitalTwinPartComponent);
    component = fixture.componentInstance;
    facade = TestBed.inject(DigitalTwinPartFacade) as jasmine.SpyObj<DigitalTwinPartFacade>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize and load digital twin parts', () => {
    expect(component.tableConfig).toBeDefined();
    expect(facade.setDigitalTwinParts).toHaveBeenCalled();
  });

  it('should navigate to detail view on onViewDetails()', () => {
    const mockPart: DigitalTwinPartResponse = {
      aasId: 'aas-001',
      globalAssetId: 'gaid-001',
      bpn: 'BPN001',
      digitalTwinType: 'PART_INSTANCE' as any,
      aasExpirationDate: new Date(2025, 3, 15, 10, 0, 0), // Month is 0-indexed
      assetExpirationDate: new Date(2025, 3, 20, 8, 0, 0) // Month is 0-indexed
    };

    component.onViewDetails(mockPart as unknown as Record<string, unknown>);
    expect(router.navigate).toHaveBeenCalledWith(['/admin/digital-twin-part/detail', 'aas-001']);
  });

  it('should trigger loadParts on page change', () => {
    spyOn(component, 'loadParts');
    component.onPageChange(2);
    expect(component.currentPage).toBe(2);
    expect(component.loadParts).toHaveBeenCalled();
  });

  it('should trigger loadParts on sort change', () => {
    const config: TableEventConfig = {
      page: 1,
      pageSize: 25,
      sorting: ['aasId', 'asc']
    };

    spyOn(component, 'loadParts');
    component.onSortChange(config);
    expect(component.currentPage).toBe(1);
    expect(component.pageSize).toBe(25);
    expect(component.currentSort).toEqual([['aasId', 'asc']]);
    expect(component.loadParts).toHaveBeenCalled();
  });

  it('should trigger openConfigurationDialog', () => {
    component.onOpenConfigurationDialog();
    expect(facade.openConfigurationDialog).toHaveBeenCalled();
  });

  it('should clear input and reset filters', () => {
    component.searchControl.setValue('test');
    component.chipItems = ['test'];
    component.searchTerms = ['test'];

    spyOn(component, 'loadParts');
    component.clearInput();

    expect(component.chipItems).toEqual([]);
    expect(component.searchTerms).toEqual([]);
    expect(component.searchControl.value).toBe('');
    expect(component.loadParts).toHaveBeenCalled();
  });

  it('should remove a chip item correctly', () => {
    component.searchTerms = ['searchTerm123'];
    component.chipItems = [component.truncateText('searchTerm123')];

    spyOn(component, 'triggerSearch');
    spyOn(component, 'updateVisibleChips');

    component.remove(component.truncateText('searchTerm123'));

    expect(component.searchTerms).toEqual([]);
    expect(component.chipItems).toEqual([]);
    expect(component.triggerSearch).toHaveBeenCalled();
    expect(component.updateVisibleChips).toHaveBeenCalled();
  });

  it('should truncate text over maxLength', () => {
    const longText = 'a'.repeat(30);
    expect(component.truncateText(longText)).toBe('a'.repeat(20) + '...');
  });

  it('should keep text as-is if within maxLength', () => {
    const shortText = 'short text';
    expect(component.truncateText(shortText)).toBe(shortText);
  });
});
