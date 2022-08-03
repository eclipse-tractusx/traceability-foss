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

import { AfterContentInit, Component, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { MatSidenav } from '@angular/material/sidenav';

@Component({
  selector: 'app-sidenav',
  templateUrl: './sidenav.component.html',
  styleUrls: ['./sidenav.component.scss'],
})
export class SidenavComponent implements AfterContentInit {
  @ViewChild('sidenav') sidenav: MatSidenav;
  @Output() sidenavAction = new EventEmitter<boolean>();

  @Input()
  set isOpen(isOpenState: boolean) {
    this._isOpen = isOpenState;
    this._isOpen ? void this.sidenav?.open() : void this.sidenav?.close();
  }

  get isOpen(): boolean {
    return this._isOpen;
  }

  private _isOpen = false;

  ngAfterContentInit(): void {
    this._isOpen ? void this.sidenav?.open() : void this.sidenav?.close();
  }
}
