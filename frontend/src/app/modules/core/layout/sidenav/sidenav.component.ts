/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

import { Component, EventEmitter, TemplateRef, ViewChild } from '@angular/core';
import { MatSidenav } from '@angular/material/sidenav';
import { SidenavService } from '@layout/sidenav/sidenav.service';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

export interface SidenavConfig {
  template: TemplateRef<HTMLElement>;
  action: EventEmitter<boolean>;
  isOpen: boolean;
}

@Component({
  selector: 'app-sidenav',
  templateUrl: './sidenav.component.html',
  styleUrls: [ './sidenav.component.scss' ],
})
export class SidenavComponent {
  @ViewChild('sidenav') sidenav: MatSidenav;

  public readonly isOpen$: Observable<boolean>;
  public readonly template$: Observable<TemplateRef<HTMLElement>>;

  constructor(private readonly sidenavService: SidenavService) {
    this.isOpen$ = this.sidenavService.isOpen$.pipe(tap(isOpen => this.openSidenav(isOpen)));
    this.template$ = this.sidenavService.template$;
  }

  public sidenavAction(action: boolean): void {
    this.sidenavService.action.emit(action);
  }

  private openSidenav(shouldOpen: boolean): void {
    shouldOpen ? void this.sidenav?.open() : void this.sidenav?.close();
  }
}
