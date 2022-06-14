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
import { State } from '@shared';

@Injectable({
  providedIn: 'root',
})
export class StaticIdService {
  private readonly _staticIds$: State<number[]> = new State<number[]>([]);

  public generateId(componentName: string) {
    const currentIds = this._staticIds$.snapshot;
    const currentId = currentIds[componentName] || 0;

    currentIds[componentName] = currentId + 1;
    this._staticIds$.update(currentIds);

    return componentName + currentId;
  }
}
