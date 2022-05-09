import { ColumnConfig } from './column-config';
import { Table } from './table';
import { TableConfig } from './table-config';

export class TableFactory {
  static buildTable(
    columnsConfig: Array<ColumnConfig> | undefined,
    tableOptions = new TableConfig(false),
    tableType?: string,
  ): Table {
    return new Table(columnsConfig, tableOptions, tableType);
  }
}
