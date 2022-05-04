/*
 * Copyright 2021 The PartChain Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { ColumnConfig } from './column-config';
import { Table } from './table';
import { TableConfig } from './table-config';

/**
 *
 *
 * @export
 * @class TableFactory
 */
export class TableFactory {
  /**
   * Table builder
   *
   * @static
   * @param {(Array<ColumnConfig> | undefined)} columnsConfig
   * @param {TableConfig} [tableOptions]
   * @param {string} tableType
   * @return {Table}
   * @memberof TableFactory
   */
  static buildTable(
    columnsConfig: Array<ColumnConfig> | undefined,
    tableOptions?: TableConfig,
    tableType?: string,
  ): Table {
    if (!tableOptions) {
      tableOptions = new TableConfig(false);
    }

    return new Table(columnsConfig, tableOptions, tableType);
  }
}
