import { SelectionModel } from '@angular/cdk/collections';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Table } from '../table';

@Component({
  selector: 'app-child-table',
  templateUrl: './child-table.component.html',
})
export class ChildTableComponent {
  @Input() data: MatTableDataSource<unknown> | Array<unknown>;
  @Input() configuration: Table;
  @Input() isChildClickable: boolean;
  @Input() removeChildSelection: boolean;

  @Output() childLinkEvent: EventEmitter<unknown> = new EventEmitter<unknown>();
  @Output() childTableSelection: EventEmitter<SelectionModel<unknown>> = new EventEmitter<SelectionModel<unknown>>();

  // eslint-disable-next-line @typescript-eslint/explicit-module-boundary-types
  public getDetails(event): void {
    this.childLinkEvent.emit(event);
  }

  public getTableSelection(selection: SelectionModel<unknown>): void {
    this.childTableSelection.emit(selection);
  }
}
