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
    match.addEventListener('change', e => (this.expanded = e.matches), { passive: true });
  }

  public manualToggle(): void {
    this.expanded = !this.expanded;
    this.layoutFacade.isSideBarExpanded = this.expanded;
  }
}
