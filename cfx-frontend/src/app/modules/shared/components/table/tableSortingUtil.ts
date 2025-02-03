import { TableHeaderSort } from '@shared/components/table/table.model';

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
export class TableSortingUtil {

  static setTableSortingList(sorting: TableHeaderSort, tableSortList: TableHeaderSort[], ctrlKeyState: boolean) {
    if (!sorting && tableSortList) {
      tableSortList.length = 0;
      return;
    }

    // If CTRL is not pressed, just set the sorting list with one entry
    if (!ctrlKeyState) {
      tableSortList.length = 0;
      tableSortList.push(sorting);
      return;
    }

    const [columnName] = sorting;
    const index = tableSortList.findIndex(([itemColumnName]) => itemColumnName === columnName);

    if (index !== -1) {
      // Replace the existing entry
      tableSortList[index] = sorting;
    } else {
      // Add the new entry if it doesn't exist
      tableSortList.push(sorting);
    }
  }

  static isSorted(tableSortList: TableHeaderSort[], column: string): boolean {
    if (tableSortList?.length > 0) {
      return tableSortList.find(sort => sort[0] === column) !== undefined;
    }
    return false;
  }

}
