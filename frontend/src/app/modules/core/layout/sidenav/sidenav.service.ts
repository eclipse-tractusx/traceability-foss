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

import { EventEmitter, Injectable, TemplateRef } from '@angular/core';
import { SidenavConfig } from '@layout/sidenav/sidenav.component';
import { State } from '@shared/model/state';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class SidenavService {
  private readonly _action$ = new State<EventEmitter<boolean>>(null);
  private readonly _isOpen$ = new State<boolean>(false);
  private readonly _template$ = new State<TemplateRef<HTMLElement>>(null);

  public get action(): EventEmitter<boolean> {
    return this._action$.snapshot;
  }

  public get isOpen$(): Observable<boolean> {
    return this._isOpen$.observable;
  }

  public get template$(): Observable<TemplateRef<HTMLElement>> {
    return this._template$.observable;
  }

  public setConfig({ action, isOpen, template }: SidenavConfig): void {
    this._action$.update(action);
    this._isOpen$.update(isOpen);
    this._template$.update(template);
  }
}
