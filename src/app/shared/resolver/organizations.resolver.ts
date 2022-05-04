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
import { LayoutFacade } from '../abstraction/layout-facade';

/**
 *
 *
 * @export
 * @class OrganizationsResolver
 * @implements {Resolve<string[]>}
 */
@Injectable()
export class OrganizationsResolver implements Resolve<string[]> {
  /**
   * @constructor OrganizationsResolver.
   * @param {LayoutFacade} layoutFacade
   * @memberof OrganizationsResolver
   */
  constructor(private layoutFacade: LayoutFacade) {}

  /**
   * Gets organizations before rendering the asset list
   *
   * @return {(string[] | Observable<string[]> | Promise<string[]>)}
   * @memberof OrganizationsResolver
   */
  resolve(): string[] | Observable<string[]> | Promise<string[]> {
    this.layoutFacade.setOrganizations();
    return this.layoutFacade.organizations$;
  }
}
