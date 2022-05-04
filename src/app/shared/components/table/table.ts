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
import { TableConfig } from './table-config';

/**
 *
 *
 * @export
 * @class Table
 */
export class Table {
  /**
   * Column definitions
   *
   * @private
   * @type {Array<ColumnConfig>}
   * @memberof Table
   */
  private columnDef: Array<ColumnConfig>;

  /**
   * Table configurations
   *
   * @private
   * @type {TableConfig}
   * @memberof Table
   */
  private readonly tableConf: TableConfig;

  private readonly type: string;

  /**
   * @constructor Creates an instance of Table.
   * @param {Array<ColumnConfig>} [columnDefinition]
   * @param {TableConfig} [tableConfig]
   * @param {string} tableType
   * @memberof Table
   */
  constructor(columnDefinition?: Array<ColumnConfig>, tableConfig?: TableConfig, tableType?: string) {
    if (columnDefinition) {
      this.columnDef = columnDefinition;
    }
    if (tableConfig) {
      this.tableConf = tableConfig;
    }

    if (tableType) {
      this.type = tableType;
    }
  }

  /**
   * Column definition getter
   *
   * @readonly
   * @type {Array<ColumnConfig>}
   * @memberof Table
   */
  get columnDefinition(): Array<ColumnConfig> {
    return this.columnDef;
  }
  /**
   * Column definition setter
   *
   * @param {Array<ColumnConfig>} value
   * @memberof Table
   */
  set columnDefinition(value: Array<ColumnConfig>) {
    this.columnDef = value;
  }

  /**
   * Table configuration getter
   *
   * @type {TableConfig}
   * @memberof Table
   */
  get tableConfig(): TableConfig {
    return this.tableConf;
  }

  get tableType(): string {
    return this.type;
  }
}
