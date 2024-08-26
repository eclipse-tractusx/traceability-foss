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

import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { Role } from './role.model';
import { RoleService } from './role.service';

type GuardValue = Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree;

@Injectable({
  providedIn: 'root',
})
export class RoleGuard  {
  constructor(private readonly roleService: RoleService, private readonly router: Router) {
  }

  public canActivate(next: ActivatedRouteSnapshot, _state: RouterStateSnapshot): GuardValue {
    return this.validateUserRole(next);
  }

  public canActivateChild(next: ActivatedRouteSnapshot, _state: RouterStateSnapshot): GuardValue {
    return this.canActivate(next, _state);
  }

  public canDeactivate(): GuardValue {
    return true;
  }

  public canMatch(): Observable<boolean> | Promise<boolean> | boolean {
    return true;
  }

  private validateUserRole(route: ActivatedRouteSnapshot): boolean {
    const requiredRoles = route.data.roles as Role[] | Role;

    if (!requiredRoles || this.roleService.hasAccess(requiredRoles)) {
      return true;
    }

    // we use skipLocationChange = true, to don't lose context
    void this.router.navigate([ 'no-permissions' ], { skipLocationChange: true });
    return false;
  }
}
