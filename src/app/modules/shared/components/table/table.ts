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
