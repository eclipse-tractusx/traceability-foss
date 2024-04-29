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


import { PartsTableConfigUtils } from '@shared/components/parts-table/parts-table-config.utils';
import { TableViewConfig } from '@shared/components/parts-table/table-view-config.model';


export class TableFilterConfiguration implements TableViewConfig {
  filterColumns: any;
  displayedColumns: any;
  displayFilterColumnMappings: any;
  filterFormGroup: any;
  sortableColumns: any;

  constructor(sortableColumns: any, dateFields?: any, singleSearchFields?: any, hasFilterColumn?: boolean) {
    this.displayedColumns = Object.keys(sortableColumns);
    this.filterFormGroup = PartsTableConfigUtils.createFormGroup(this.displayedColumns);
    this.filterColumns = PartsTableConfigUtils.createFilterColumns(this.displayedColumns, hasFilterColumn);
    this.sortableColumns = sortableColumns;
    this.displayFilterColumnMappings = PartsTableConfigUtils.generateFilterColumnsMapping(sortableColumns, dateFields, singleSearchFields, hasFilterColumn);

  }

  public filterConfiguration(): TableViewConfig {
    return {
      filterColumns: this.filterColumns,
      displayedColumns: this.displayedColumns,
      displayFilterColumnMappings: this.displayFilterColumnMappings,
      filterFormGroup: this.filterFormGroup,
      sortableColumns: this.sortableColumns,
    };
  }

}

