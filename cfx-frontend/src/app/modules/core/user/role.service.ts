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
import { Role, RoleRelation } from './role.model';
import { UserService } from './user.service';

const ROLES_RELATIONS: RoleRelation[] = [
  {
    role: Role.WIP,
  },
  {
    role: Role.ADMIN,
  },
  {
    role: Role.SUPERVISOR,
    child: Role.USER,
  },
];

@Injectable({
  providedIn: 'root',
})
export class RoleService {
  constructor(private readonly userService: UserService) { }

  public hasAccess(requiredRoles: Role | Role[]): boolean {
    const requiredRolesList = typeof requiredRoles === 'string' ? [requiredRoles] : requiredRoles;

    const roles = this.userService.roles.map(role => role.toLocaleLowerCase());
    const allPossibleRoles = [...requiredRolesList, ...this.getParentsRolesFor(requiredRolesList)];

    return allPossibleRoles.some(possibleRole => roles.includes(possibleRole));
  }

  public isAtLeastSupervisor(): boolean {
    return this.hasAccess(Role.SUPERVISOR);
  }

  public isAtLeastUser(): boolean {
    return this.hasAccess(Role.USER);
  }

  private getParentsRolesFor(lookupRoles: Role[]): Role[] {
    const parentRoles = ROLES_RELATIONS.filter(({ child }) => lookupRoles.includes(child));

    if (parentRoles.length) {
      const roles = parentRoles.map(({ role }) => role);
      return [...roles, ...this.getParentsRolesFor(roles)];
    }

    return [];
  }
}
