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

import { Role } from './role';
import { UserService } from './user.service';

type RoleRelation = {
  role: Role;
  child: Role;
};

const ROLES_RELATIONS: RoleRelation[] = [
  {
    role: 'admin',
    child: 'supervisor',
  },
  {
    role: 'supervisor',
    child: 'user',
  },
];

@Injectable({
  providedIn: 'root',
})
export class RoleService {
  constructor(private readonly userService: UserService) {}

  hasAccess(requiredRoles: Role | Role[]): boolean {
    const requiredRolesList = typeof requiredRoles === 'string' ? [requiredRoles] : requiredRoles;

    const roles = this.userService.getRoles();
    const allPossibleRoles = [...requiredRolesList, ...this.getParentsRolesFor(requiredRolesList)];

    return allPossibleRoles.some(possibleRole => roles.includes(possibleRole));
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
