import { ColumnType } from './column-type';
import { Table } from './table';

export interface ColumnConfig {
  fieldName: string;
  childFieldName?: string;
  label?: string;
  editable?: boolean;
  sortable?: boolean;
  status?: string;
  required?: boolean;
  hide?: boolean;
  width?: number;
  order?: number;
  type?: ColumnType;
  showInDetail?: boolean;
  detailTable?: Table;
  capitalize?: boolean;
}
