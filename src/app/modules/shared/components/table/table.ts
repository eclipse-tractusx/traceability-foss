/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { ColumnConfig } from './column-config';
import { TableConfig } from './table-config';

export class Table {
  private columnDef: Array<ColumnConfig>;
  private readonly tableConf: TableConfig;
  private readonly type: string;

  constructor(columnDefinition?: Array<ColumnConfig>, tableConfig?: TableConfig, tableType?: string) {
    this.columnDef = columnDefinition || this.columnDef;
    this.tableConf = tableConfig || this.tableConf;
    this.type = tableType || this.type;
  }

  get columnDefinition(): Array<ColumnConfig> {
    return this.columnDef;
  }

  set columnDefinition(value: Array<ColumnConfig>) {
    this.columnDef = value;
  }

  get tableConfig(): TableConfig {
    return this.tableConf;
  }

  get tableType(): string {
    return this.type;
  }
}
