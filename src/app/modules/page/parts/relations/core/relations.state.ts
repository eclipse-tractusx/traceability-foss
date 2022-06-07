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

import { Injectable } from '@angular/core';
import { LoadedElements, OpenElements } from '@page/parts/relations/model/relations.model';
import { State } from '@shared';
import { Observable } from 'rxjs';

@Injectable()
export class RelationsState {
  private readonly _openElements$: State<OpenElements> = new State<OpenElements>({});
  private readonly _loadedElements$: State<LoadedElements> = new State<LoadedElements>({});

  get openElements$(): Observable<OpenElements> {
    return this._openElements$.observable;
  }

  get openElements() {
    return this._openElements$.snapshot;
  }

  set openElements(data: OpenElements) {
    this._openElements$.update(data);
  }

  public resetOpenElements(): void {
    this._openElements$.reset();
  }

  get loadedElements(): LoadedElements {
    return this._loadedElements$.snapshot;
  }

  set loadedElements(data: LoadedElements) {
    this._loadedElements$.update(data);
  }

  public resetLoadedElements(): void {
    this._loadedElements$.reset();
  }
}
