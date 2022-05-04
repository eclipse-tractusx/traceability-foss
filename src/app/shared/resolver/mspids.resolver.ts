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

import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';
import { Observable } from 'rxjs';
import { LayoutFacade } from 'src/app/shared/abstraction/layout-facade';
import { Mspid } from 'src/app/shared/model/mspid.model';

/**
 *
 *
 * @export
 * @class MspidsResolver
 * @implements {Resolve<Mspid[]>}
 */
@Injectable()
export class MspidsResolver implements Resolve<Mspid[]> {
  /**
   * @constructor MspidsResolver.
   * @param {LayoutFacade} layoutFacade
   * @memberof MspidsResolver
   */
  constructor(private layoutFacade: LayoutFacade) {}

  /**
   * Gets the mspids before rendering the component
   *
   * @return {(Mspid[] | Observable<Mspid[]> | Promise<Mspid[]>)}
   * @memberof MspidsResolver
   */
  resolve(): Mspid[] | Observable<Mspid[]> | Promise<Mspid[]> {
    const mspids = this.layoutFacade.mspidsSnapshot;
    return !mspids.length ? this.layoutFacade.getMspidRequest() : mspids;
  }
}
