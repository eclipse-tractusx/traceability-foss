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

import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { LayoutFacade } from 'src/app/modules/shared/abstraction/layout-facade';

@Component({
  selector: 'app-private-layout',
  templateUrl: './private-layout.component.html',
  styleUrls: ['./private-layout.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class PrivateLayoutComponent implements OnInit {
  public expanded: boolean;

  constructor(private readonly layoutFacade: LayoutFacade) {
    this.expanded = false;
  }

  ngOnInit(): void {
    this.handleResize();
  }

  public handleResize(): void {
    const match = window.matchMedia('(min-width: 1024px)');
    match.addEventListener('change', e => {
      this.expanded = e.matches;
    });
  }

  public manualToggle(): void {
    this.expanded = !this.expanded;
    this.layoutFacade.isSideBarExpanded = this.expanded;
  }
}
