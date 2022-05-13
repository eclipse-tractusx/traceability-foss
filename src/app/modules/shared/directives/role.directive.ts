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
import { UserService } from 'src/app/modules/core/user/user.service';

@Directive({
  selector: '[appHasRole]',
})
export class RoleDirective implements OnInit {
  @Input() appHasRole: string[];

  private isVisible = false;

  /**
   * @constructor RoleDirective
   * @param {ViewContainerRef} viewContainerRef
   * 	-- the location where we need to render the templateRef
   * @param {TemplateRef<unknown>} templateRef
   *   -- the templateRef to be potentially rendered
   * @param {UserService} userService
   *   -- will give us access to the roles a user has
   */
  constructor(
    private viewContainerRef: ViewContainerRef,
    private templateRef: TemplateRef<unknown>,
    private userService: UserService,
  ) {}

  ngOnInit(): void {
    const roles = this.userService.getRoles();
    // If the user doesn't have any roles, we clear the viewContainerRef
    if (!roles) {
      this.viewContainerRef.clear();
    }
    // If the user has the role needed to
    // render this component we can add it
    // If it is already visible (which can happen if
    // his roles changed we do not need to add it a second time
    const hasSomeRole = this.appHasRole.some(role => roles.includes(role));
    if (hasSomeRole && !this.isVisible) {
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
