/*
 * Copyright 2021 The PartChain Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Directive, HostListener, Input } from '@angular/core';
import { MatTooltip } from '@angular/material/tooltip';

/**
 *
 *
 * @export
 * @class TooltipDirective
 */
@Directive({
  selector: '[appTooltip]',
  providers: [MatTooltip],
})
export class TooltipDirective {
  /**
   * Tooltip
   *
   * @type {MatTooltip}
   * @memberof TooltipDirective
   */
  public appTooltip: MatTooltip;

  /**
   * Tooltip label
   *
   * @type {string}
   * @memberof TooltipDirective
   */
  @Input() tooltip: string;

  /**
   * @constructor TooltipDirective
   * @param {MatTooltip} appTooltip
   * @memberof TooltipDirective
   */
  constructor(appTooltip: MatTooltip) {
    this.appTooltip = appTooltip;
  }

  /**
   * Mouse over listener
   *
   * @return {void}
   * @memberof TooltipDirective
   */
  @HostListener('mouseover') mouseover(): void {
    this.appTooltip.message = this.tooltip;
    this.appTooltip.show();
  }

  /**
   * Mouse leave listener
   *
   * @return {void}
   * @memberof TooltipDirective
   */
  @HostListener('mouseleave') mouseleave(): void {
    this.appTooltip.hide();
  }
}
