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

import { Component, ElementRef, Renderer2, ViewEncapsulation } from '@angular/core';

@Component({
  selector: 'app-layout',
  templateUrl: './layout.component.html',
  styleUrls: [ './layout.component.scss' ],
  encapsulation: ViewEncapsulation.None,
})
export class LayoutComponent {
  constructor(private renderer: Renderer2, private elementRef: ElementRef) {
  }

  public ngOnInit() {
    /**
     * This Block positions the toast component to the start of the header breadcrumb component (vertical top distance)
     * so that on every screen size the position stays the same (not relative)
     */
    const headerBreadCrumbRef = this.elementRef.nativeElement.querySelector('.header--breadcrumb-container');
    const toastLayoutRef = this.elementRef.nativeElement.querySelector('.layout-toast-component');
    const elementTopDistance = headerBreadCrumbRef.getBoundingClientRect().top;
    this.renderer.setStyle(toastLayoutRef, 'top', `${ elementTopDistance }px`);
  }

}
