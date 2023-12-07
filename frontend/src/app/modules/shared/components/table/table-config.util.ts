import { FormControl } from '@angular/forms';
import { PartTableType } from '@shared/components/table/table.model';
import { NotificationType } from '@shared/model/notification.model';

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
export class TableConfigUtil {

  public static getNotificationTableType(notificationType: NotificationType, displayedColumns: any): PartTableType {

    console.log(notificationType, displayedColumns);
    if(notificationType === NotificationType.INVESTIGATION) {
      if(displayedColumns.includes('createdBy')) {
        return PartTableType.RECEIVED_INVESTIGATION;
      } else {
        console.log("RECEIVED INVS!")
        return PartTableType.CREATED_INVESTIGATION;
      }
    } else {
      if(displayedColumns.includes('createdBy')) {
        return PartTableType.RECEIVED_ALERT;
      } else {
        return PartTableType.CREATED_ALERT;
      }
    }
  }

  public static createFormGroup(displayedColumns: any): Record<string, FormControl> {
    const formGroup: Record<string, FormControl> = {};

    for (const column of displayedColumns) {
      if (column !== 'select' && column !== 'menu') {
        formGroup[column] = new FormControl([]);
      }

    }
    return formGroup;
  }

  public static createFilterColumns(displayedColumns: string[]): string[] {
    const array = displayedColumns.filter((column: string) => 'select' !== column && 'menu' !== column).map((column: string) => 'filter' + column);

    return [ 'Filter', ...array, 'Menu' ];


  }

  public static generateFilterColumnsMapping(sortableColumns: any, dateFields?: string[], singleSearchFields?: string[]): any[] {
    const filterColumnsMapping: any[] = [];

    for (const key in sortableColumns) {
      if (sortableColumns.hasOwnProperty(key)) {
        // This key goes to the backend rest api
        const filterKey = key;
        const headerKey = 'filter' + key;

        let columnMapping: { filterKey: string; headerKey: string; isDate?: boolean; singleSearch?: boolean; };
        if (dateFields?.includes(filterKey)) {
          columnMapping = { filterKey, headerKey, isDate: true };
        } else if (singleSearchFields?.includes(filterKey)) {
          columnMapping = { filterKey, headerKey, singleSearch: true };
        } else {
          columnMapping = { filterKey, headerKey };
        }

        filterColumnsMapping.push(columnMapping);
      }
    }
    return [ ...filterColumnsMapping ];

  }
}
