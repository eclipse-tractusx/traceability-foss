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

import { AfterViewInit, Component, ElementRef, OnDestroy, ViewChild } from '@angular/core';

@Component({
  selector: 'app-scroll-with-shadow',
  templateUrl: './scroll-with-shadow.component.html',
  styleUrls: ['./scroll-with-shadow.component.scss'],
})
export class ScrollWithShadowComponent implements AfterViewInit, OnDestroy {
  @ViewChild('container') public containerRef: ElementRef<HTMLDivElement>;

  public whenMarkedAsReady = new Promise(resolve => {
    this.markAsReady = resolve as () => void;
  });

  public hasLeftScroll = false;
  public hasRightScroll = false;

  private hasScroll = false;

  private markAsReady: () => void;

  private readonly resizeObserver = new ResizeObserver(() => {
    const el = this.containerRef.nativeElement;
    const hasScroll = el.scrollWidth > el.offsetWidth;

    if (!this.hasScroll && hasScroll) {
      // scroll enabled
      this.calculateScrollSettings();
    } else if (this.hasScroll && !hasScroll) {
      // scroll disabled
      this.hasLeftScroll = false;
      this.hasRightScroll = false;
    }

    this.hasScroll = hasScroll;

    this.markAsReady();
  });

  public ngAfterViewInit(): void {
    this.resizeObserver.observe(this.containerRef.nativeElement);
  }

  public ngOnDestroy(): void {
    this.resizeObserver.disconnect();
  }

  public calculateScrollSettings(): void {
    const el = this.containerRef.nativeElement;
    this.hasLeftScroll = el.scrollLeft > 0;
    this.hasRightScroll = el.scrollLeft + el.offsetWidth < el.scrollWidth;
  }
}
