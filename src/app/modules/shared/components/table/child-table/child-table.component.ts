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
