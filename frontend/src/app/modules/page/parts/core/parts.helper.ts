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
import { PartsTableComponent } from '@shared/components/parts-table/parts-table.component';

export function resetMultiSelectionAutoCompleteComponent(partsTableComponents: QueryList<PartsTableComponent>, oneFilterSet: boolean): boolean {
  for (const partsTableComponent of partsTableComponents) {
    for (const multiSelectAutocompleteComponent of partsTableComponent.multiSelectAutocompleteComponents) {
      multiSelectAutocompleteComponent.searchElement = null;
      multiSelectAutocompleteComponent.clickClear();
      multiSelectAutocompleteComponent.formControl.reset();
      if (partsTableComponent.filterFormGroup.dirty && !oneFilterSet) {
        oneFilterSet = true;
      }
    }
  }
  return oneFilterSet;
}

export function provideDataObject(data: Pagination<any>) {
  let usedData: Pagination<any>;
  if (!data || !data.content?.length) {
    usedData = {
      content: [],
      page: 0,
      pageCount: 0,
      pageSize: 0,
      totalItems: 0,
    };

  } else {
    usedData = data;
  }
  return usedData;
}

