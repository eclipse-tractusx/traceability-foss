import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatDialogConfig, MatDialog } from '@angular/material/dialog';
import { TableColumnSettingsComponent } from '../table-column-settings/table-column-settings.component';
import { TableType } from '../multi-select-autocomplete/table-type.model';

@Component({
  selector: 'app-table-settings',
  templateUrl: './table-settings.component.html',
  styleUrls: ['./table-settings.component.scss']
})
export class TableSettingsComponent {

  @Input() tableType: TableType;
  @Input() defaultColumns: string[];
  @Input() defaultFilterColumns: string[];
  @Input() hideResetButton = false;

  @Output() filtersReset = new EventEmitter<void>();

  constructor(private dialog: MatDialog) { }

  resetFilters(): void {
    this.filtersReset.emit();
  }

  openTableColumnSettingsDialog(): void {
    const config = new MatDialogConfig();
    config.autoFocus = false;
    config.panelClass = 'table-column-settings-dialog';
    config.data = {
      title: 'table.tableSettings.title',
      tableType: this.tableType,
      defaultColumns: this.defaultColumns,
      defaultFilterColumns: this.defaultFilterColumns
    };
    this.dialog.open(TableColumnSettingsComponent, config);
  }
}
