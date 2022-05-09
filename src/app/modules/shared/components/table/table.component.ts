// ToDo: Complexity of this component is high
// ToDo: we may need to rewrite this component

import { SelectionModel } from '@angular/cdk/collections';
import {
  AfterViewInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges,
  ViewChild,
} from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { BehaviorSubject, Observable } from 'rxjs';
import { ColumnConfig } from './column-config';
import { ColumnType } from './column-type';
import { Table } from './table';
import { tableAnimation } from './table-animation';
import { TableFactory } from './table-factory';
import { TableActions } from './table.actions';
import { TableFacade } from './table.facade';

@Component({
  selector: 'app-table',
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  animations: [tableAnimation],
})
export class TableComponent implements OnInit, AfterViewInit, OnChanges {
  @Input()
  get dataSet(): MatTableDataSource<unknown> | Array<unknown> {
    return this.data;
  }

  set dataSet(data: MatTableDataSource<unknown> | Array<unknown>) {
    if (this.data !== data) {
      this.changeDetector.markForCheck();
    }
    this.data = data;
  }

  @ViewChild(MatSort) set matSort(matSort: MatSort) {
    this.sort = matSort;
  }

  @ViewChild(MatPaginator) set matPaginator(mp: MatPaginator) {
    if (this.dataSource && this.pageSizeOptions && this.pageSizeOptions.length > 0) {
      this.paginator = mp;
      this.dataSource.paginator = this.paginator;
    }
  }

  @ViewChild('gridTemplate', { static: true }) gridTemplate: ElementRef;

  @Input() singleChildRowDetail: boolean;
  @Input() removeSelection = false;
  @Input() clickedRow: string;
  @Input() multiColorRow: boolean;
  @Input() pageSizeOptions: number[];
  @Input() pageSize: number;
  @Input() clickableRows: boolean;
  @Input() isChildClickable: boolean;
  @Input() stickyHeader = false;
  @Input() tableConfiguration?: Table = new Table(undefined);

  @Output() clickedRowEmitter?: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output() clickEvent: EventEmitter<TableActions> = new EventEmitter<undefined>();
  @Output() linkEvent: EventEmitter<unknown> = new EventEmitter<undefined>();
  @Output() tableSelection: EventEmitter<SelectionModel<unknown>> = new EventEmitter<SelectionModel<unknown>>(
    undefined,
  );

  public dataSource: MatTableDataSource<unknown>;
  public columnsToShow: Array<string> = [];
  public dataColumns?: Array<ColumnConfig>;
  public detailData: Array<ColumnConfig> = [];
  public selection: SelectionModel<unknown>;
  public childSelection: SelectionModel<unknown>;
  public selectedAsset = '';
  public expandedRow = {};
  public tableActions: TableActions;
  public iconArrayLimit = 5;
  public rowIsTriggerState: BehaviorSubject<number> = new BehaviorSubject<number>(-1);
  public selectedRowState$: Observable<string>;
  public selectedRows: Array<unknown> = [];
  public removeChildSelection: boolean;

  private paginator: MatPaginator;
  private data: MatTableDataSource<unknown> | Array<unknown>;
  private dataArray: Array<unknown>;
  private tableReadOnly = true;
  private technicalColumnsBegin: Array<ColumnConfig>;
  private sort: MatSort;

  constructor(public changeDetector: ChangeDetectorRef, private tableFacade: TableFacade) {
    this.selectedAsset = undefined;
    this.selectedRowState$ = this.tableFacade.selectedAsset$;
    this.selection = new SelectionModel<unknown>(true, []);
    this.removeChildSelection = false;
  }

  static searchColumnDefinition<T>(dataObject: T): Array<ColumnConfig> {
    const keys: Array<ColumnConfig> = [];

    for (const name of Object.keys(dataObject)) {
      keys.push({ fieldName: name, label: name });
    }
    return keys;
  }

  ngOnInit(): void {
    this.initTable();
  }

  ngAfterViewInit(): void {
    this.initializeTechnicalColumns();
    if (this.dataSource && this.pageSizeOptions && this.pageSizeOptions.length > 0) {
      this.dataSource.paginator = this.paginator;
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.dataSet || changes.tableConfiguration) {
      this.initTable();
    }

    if (changes.removeSelection) {
      this.selection.clear();
    }
  }

  // eslint-disable-next-line @typescript-eslint/explicit-module-boundary-types
  public onRowClick(row, column: ColumnConfig): void {
    if (this.clickableRows && column.type !== 'TABLE' && row.partNumberManufacturer !== '') {
      this.tableFacade.setSelectedAsset(row.serialNumberCustomer);
      this.selectedAsset = row.serialNumberCustomer;
    }
  }

  public getTableEmptyState(): string {
    if (this.dataSource.filteredData.length === 0 && !!this.tableConfiguration.tableConfig.emptyStateReason) {
      return this.tableConfiguration.tableConfig.emptyStateReason;
    }
    return 'No data available';
  }

  public isAllSelected(): boolean {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  public masterToggle(): void {
    if (this.isAllSelected()) {
      this.selection.clear();
      this.selectedRows = [];
      this.tableSelection.emit(this.selection);
      return;
    }

    if (this.childSelection) {
      this.removeChildSelection = !this.removeChildSelection;
    }

    this.selection.select(...this.dataSource.data);
    this.setSelectionActions();
  }

  // eslint-disable-next-line @typescript-eslint/explicit-module-boundary-types
  public toggleCheckbox(row): void {
    if (this.childSelection) {
      this.removeChildSelection = !this.removeChildSelection;
    }
    this.selection.toggle(row);
    this.setSelectionActions();
  }

  // eslint-disable-next-line @typescript-eslint/explicit-module-boundary-types
  public checkboxLabel(row?): string {
    if (!row) {
      return `${this.isAllSelected() ? 'select' : 'deselect'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.serialNumberCustomer + 1}`;
  }

  public setSelectionActions(): void {
    this.selectedRows = this.selection.selected;
    this.tableSelection.emit(this.selection);
  }

  public getChildTableSelection(selection: SelectionModel<unknown>): void {
    if (selection) {
      this.selection.clear();
      this.childSelection = selection;
      this.tableSelection.emit(this.childSelection);
    }
  }

  public emitActions(row: unknown, name: string, disable: boolean): void {
    this.tableActions = {
      row,
      name,
      disable,
    };

    if (!disable) {
      this.clickEvent.emit(this.tableActions);
    }
  }

  // eslint-disable-next-line @typescript-eslint/explicit-module-boundary-types
  public emitLinkClick(row): void {
    this.linkEvent.emit(row);
  }

  public getStyle(cell: ColumnConfig): { flex: number } {
    if (cell && cell.width) {
      return { flex: cell.width };
    }

    return { flex: 1.1 };
  }

  public getCursor(): { cursor: string } {
    return this.clickableRows ? { cursor: 'pointer' } : { cursor: 'default' };
  }

  // eslint-disable-next-line @typescript-eslint/explicit-module-boundary-types
  public getIcon(row): string {
    return row.isExpanded ? 'arrow-up-s-line' : 'arrow-down-s-line';
  }

  // eslint-disable-next-line @typescript-eslint/explicit-module-boundary-types
  public getMultiColorRow(index: number): { 'background-color': string } {
    if (this.multiColorRow) {
      return index % 2 !== 0 ? { 'background-color': '#fff' } : { 'background-color': '#efefef' };
    }
  }

  public singleActionIcons(row: string[]): { 'padding-left': string } {
    return row.length === 1 ? { 'padding-left': '1.6rem' } : { 'padding-left': '0' };
  }

  public getClass(cell: ColumnConfig): string {
    if (ColumnType.FILE === cell.type) {
      return 'download';
    }
  }

  // eslint-disable-next-line @typescript-eslint/explicit-module-boundary-types
  public onDetailGrid(row, data: string): void {
    row.selectedTemplate = this.gridTemplate;
    if (typeof row.isExpanded === 'undefined') {
      row.isExpanded = false;
    }

    row.isExpanded = !row.isExpanded;
    row.selectedColumn = data;
    this.expandedRow = row;

    const selectedAsset = this.tableFacade.selectedAssetSnapshot;

    if (!row.isExpanded && selectedAsset !== row.serialNumberCustomer) {
      this.tableFacade.setSelectedAsset(undefined);
    }
  }

  public getDetailConfiguration(column: string): Table {
    if (!this.dataColumns) {
      return TableFactory.buildTable(undefined);
    }

    for (const col in this.dataColumns) {
      if (column === this.dataColumns[col].fieldName || column === this.dataColumns[col].childFieldName) {
        if (this.dataColumns[col].detailTable) {
          const table = this.dataColumns[col].detailTable;
          if (table) {
            return table;
          }
        } else {
          return TableFactory.buildTable(undefined);
        }
      }
    }

    return TableFactory.buildTable(undefined);
  }

  public initTable(): void {
    this.changeDetector.detach();
    if (this.data instanceof MatTableDataSource) {
      this.dataSource = this.data;
    } else if (this.data instanceof Array) {
      this.dataArray = this.data;
    }

    if (this.dataArray) {
      this.dataSource = new MatTableDataSource<unknown>(this.dataArray);
    }

    this.initializeTechnicalColumns();
    this.dataColumns = [];

    if (this.tableConfiguration && this.tableConfiguration.columnDefinition) {
      for (const item of this.tableConfiguration.columnDefinition) {
        this.dataColumns.push(item);
      }
      this.columnsToShow = this.setColumnsToShow(this.technicalColumnsBegin, this.dataColumns);
    } else {
      // we try to extract the definition from the dataset
      if (this.dataSource.data.length > 0) {
        this.dataColumns = TableComponent.searchColumnDefinition(this.dataSource.data[0]);
        if (this.columnsToShow.length === 0) {
          this.columnsToShow = this.setColumnsToShow(this.technicalColumnsBegin, this.dataColumns);
        }
      }
    }

    this.changeDetector.reattach();
  }

  // eslint-disable-next-line @typescript-eslint/explicit-module-boundary-types
  public expand(event: Event, row, index: number): void {
    if (event) {
      event.stopPropagation();
      if (typeof row.isTriggered === 'undefined') {
        row.isTriggered = false;
      }
      row.isTriggered = !row.isTriggered;
      this.rowIsTriggerState.next(index);
    }
  }

  public copyToClipboard(serialNumber: string): void {
    alert('Not Implemented.');
  }

  private initializeTechnicalColumns(): void {
    this.technicalColumnsBegin = [];
    this.tableReadOnly = !this.isTechnicalColumnsBeginVisible();

    // show the select checkbox only if record and table are editable
    if (!this.tableReadOnly) {
      this.technicalColumnsBegin.push({ fieldName: 'select', label: 'select' });
    }
  }

  private isTechnicalColumnsBeginVisible(): boolean {
    let visibility = false;

    if (this.tableConfiguration && this.tableConfiguration.tableConfig) {
      visibility = !this.tableConfiguration.tableConfig.isReadOnly;
    }

    return visibility;
  }

  private setColumnsToShow(standardColBegin: Array<ColumnConfig>, appColumns: Array<ColumnConfig>): Array<string> {
    const columns: Array<string> = [];

    if (typeof standardColBegin !== 'undefined') {
      for (const column of standardColBegin) {
        columns.push(column.fieldName);
      }
    }

    for (const column of appColumns) {
      if (!column.hide) {
        columns.push(column.fieldName);
      }

      if (column.hide && column.showInDetail) {
        this.detailData.push(column);
      }
    }

    return columns;
  }
}
