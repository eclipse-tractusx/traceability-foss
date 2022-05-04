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

import { SelectionModel } from '@angular/cdk/collections';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Table } from '../table';

/**
 *
 *
 * @export
 * @class ChildTableComponent
 */
@Component({
  selector: 'app-child-table',
  templateUrl: './child-table.component.html',
})
export class ChildTableComponent {
  /**
   * Detail table data
   *
   * @type {unknown}
   * @memberof ChildTableComponent
   */
  @Input() data: MatTableDataSource<unknown> | Array<unknown>;

  /**
   * Detail table configurations
   *
   * @type {Table}
   * @memberof ChildTableComponent
   */
  @Input() configuration: Table;

  /**
   * Is child clickable
   *
   * @type {boolean}
   * @memberof ChildTableComponent
   */
  @Input() isChildClickable: boolean;

  /**
   * Remove the child selection
   *
   * @type {boolean}
   * @memberof ChildTableComponent
   */
  @Input() removeChildSelection: boolean;

  /**
   * Child row click event
   *
   * @type {EventEmitter<unknown>}
   * @memberof ChildTableComponent
   */
  @Output() childLinkEvent: EventEmitter<unknown> = new EventEmitter<unknown>();

  /**
   * Child table selection emitter
   *
   * @type {EventEmitter<SelectionModel<unknown>>}
   * @memberof ChildTableComponent
   */
  @Output() childTableSelection: EventEmitter<SelectionModel<unknown>> = new EventEmitter<SelectionModel<unknown>>(
    undefined,
  );

  /**
   * Emit the row which has been clicked from the child table
   *
   * @param event
   * @returns {void}
   * @memberof ChildTableComponent
   */
  // eslint-disable-next-line @typescript-eslint/explicit-module-boundary-types
  public getDetails(event): void {
    this.childLinkEvent.emit(event);
  }

  /**
   * Emits the table selection
   *
   * @param {SelectionModel<unknown>} selection
   * @memberof ChildTableComponent
   */
  public getTableSelection(selection: SelectionModel<unknown>): void {
    this.childTableSelection.emit(selection);
  }
}
