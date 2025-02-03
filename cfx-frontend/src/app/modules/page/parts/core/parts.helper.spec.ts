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

import { QueryList } from '@angular/core';
import { Pagination } from '@core/model/pagination.model';
import { provideDataObject, resetMultiSelectionAutoCompleteComponent } from '@page/parts/core/parts.helper';
import { Part } from '@page/parts/model/parts.model';
import { PartsTableComponent } from '@shared/components/parts-table/parts-table.component';

describe('PartsHelper', () => {
  it('should reset multiSelectAutocompleteComponents and set oneFilterSet to true if filterFormGroup is dirty', () => {
    // Arrange

    const mockQueryList = <T>(items: T[]): QueryList<T> => {
      const queryList = new QueryList<T>();
      queryList.reset(items);
      return queryList;
    };

    const multiSelectAutoCompleteComponents = [
      {
        theSearchElement: 'test',
        clickClear: jasmine.createSpy('clickClear'),
        formControl: { reset: jasmine.createSpy('reset') },

      } ];
    const queryListMultiSelect = mockQueryList(multiSelectAutoCompleteComponents);

    const partsTableComponents: PartsTableComponent[] = [
      {
        // @ts-ignore
        multiSelectAutocompleteComponents: queryListMultiSelect,
        // @ts-ignore
        filterFormGroup: {
          dirty: true,
        },
      },
    ];

    const partsTableComponentsQueryList = mockQueryList(partsTableComponents);

    let oneFilterSet = false;

    // Act
    oneFilterSet = resetMultiSelectionAutoCompleteComponent(partsTableComponentsQueryList, oneFilterSet);

    // Assert
    expect(oneFilterSet).toBe(true);

    partsTableComponents.forEach((partsTableComponent) => {
      partsTableComponent.multiSelectAutocompleteComponents.forEach((multiSelectAutocompleteComponent) => {
        expect(multiSelectAutocompleteComponent.searchElement).toBeNull();
        expect(multiSelectAutocompleteComponent.clickClear).toHaveBeenCalled();
        expect(multiSelectAutocompleteComponent.formControl.reset).toHaveBeenCalled();
      });
    });
  });

  it('should handle default pagination', () => {

    let data: Pagination<Part> = {
      content: [],
      page: 0,
      pageCount: 0,
      pageSize: 0,
      totalItems: 0
    };

    const actual = provideDataObject(data);
    expect(actual).toEqual(data);
  })
});
