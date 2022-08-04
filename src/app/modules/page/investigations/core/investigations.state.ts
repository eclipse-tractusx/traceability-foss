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
import { Investigations } from '@shared/model/investigations.model';
import { State } from '@shared/model/state';
import { View } from '@shared/model/view.model';
import { Observable } from 'rxjs';

@Injectable()
export class InvestigationsState {
  private readonly _investigationsReceived$ = new State<View<Investigations>>({ loader: true });
  private readonly _investigationsQueuedAndRequested$ = new State<View<Investigations>>({ loader: true });

  public get investigationsReceived$(): Observable<View<Investigations>> {
    return this._investigationsReceived$.observable;
  }

  public set investigationsReceived({ data, loader, error }: View<Investigations>) {
    const investigationsView: View<Investigations> = { data, loader, error };
    this._investigationsReceived$.update(investigationsView);
  }

  public get investigationsQueuedAndRequested$(): Observable<View<Investigations>> {
    return this._investigationsQueuedAndRequested$.observable;
  }

  public set investigationsQueuedAndRequested({ data, loader, error }: View<Investigations>) {
    const investigationsView: View<Investigations> = { data, loader, error };
    this._investigationsQueuedAndRequested$.update(investigationsView);
  }
}
