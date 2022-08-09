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
import { Role } from '@core/user/role.model';
import { RoleService } from '@core/user/role.service';

@Directive({
  selector: '[appHasRole]',
})
export class RoleDirective implements OnInit {
  @Input() appHasRole: Role[] | Role;

  private isVisible = false;

  constructor(
    private readonly viewContainerRef: ViewContainerRef,
    private readonly templateRef: TemplateRef<unknown>,
    private readonly roleService: RoleService,
  ) {}

  public ngOnInit(): void {
    this.isVisible = true;

    this.roleService.hasAccess(this.appHasRole)
      ? this.viewContainerRef.createEmbeddedView(this.templateRef)
      : this.viewContainerRef.clear();
  }
}
