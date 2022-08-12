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

import {
  AfterContentInit,
  AfterViewInit,
  Component,
  EventEmitter,
  Input,
  Output,
  TemplateRef,
  ViewChild,
} from '@angular/core';
import { SidenavService } from '@layout/sidenav/sidenav.service';
import { State } from '@shared/model/state';

@Component({
  selector: 'app-sidenav-wrapper',
  templateUrl: './sidenav-wrapper.component.html',
})
export class SidenavWrapperComponent implements AfterViewInit {
  @ViewChild('templateRef') templateRef: TemplateRef<HTMLElement>;
  @Output() sidenavAction = new EventEmitter<boolean>();

  @Input()
  public set isOpen(isOpen: boolean) {
    this.isOpen$.update(isOpen);

    if (this.templateRef) {
      this.configureSidenav();
    }
  }

  private readonly isOpen$ = new State(false);

  constructor(private readonly sidenavService: SidenavService) {}

  public ngAfterViewInit(): void {
    if (this.isOpen$.snapshot) {
      this.configureSidenav();
    }
  }

  private configureSidenav(): void {
    this.sidenavService.setConfig({
      template: this.templateRef,
      action: this.sidenavAction,
      isOpen: this.isOpen$.snapshot,
    });
  }
}
