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

import { Directive, Input, OnInit, TemplateRef, ViewContainerRef } from '@angular/core';
import { Role } from '@core/user/role';
import { RoleService } from '@core/user/role.service';

@Directive({
  selector: '[appHasRole]',
})
export class RoleDirective implements OnInit {
  @Input() appHasRole: Role[] | Role;

  private isVisible = false;

  /**
   * @constructor RoleDirective
   * @param {ViewContainerRef} viewContainerRef
   * 	-- the location where we need to render the templateRef
   * @param {TemplateRef<unknown>} templateRef
   *   -- the templateRef to be potentially rendered
   * @param {RoleService} roleService
   *   -- will check access
   */
  constructor(
    private viewContainerRef: ViewContainerRef,
    private templateRef: TemplateRef<unknown>,
    private roleService: RoleService,
  ) {}

  ngOnInit(): void {
    if (this.roleService.hasAccess(this.appHasRole)) {
      // We update the `isVisible` property and add the
      // templateRef to the view using the
      // 'createEmbeddedView' method of the viewContainerRef
      this.isVisible = true;
      this.viewContainerRef.createEmbeddedView(this.templateRef);
    } else {
      // If the user does not have the role,
      // we update the `isVisible` property and clear
      // the contents of the viewContainerRef
      this.isVisible = true;
      this.viewContainerRef.clear();
    }
  }
}
