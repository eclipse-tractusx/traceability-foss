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

import { Directive, ElementRef, EventEmitter, HostListener, Output } from '@angular/core';

/**
 *
 *
 * @export
 * @class ClickOutsideDirective
 */
@Directive({ selector: '[clickOutside]' })
export class ClickOutsideDirective {
  /**
   * Click outside event emitter
   *
   * @type {EventEmitter<MouseEvent>}
   * @memberof ClickOutsideDirective
   */
  @Output() clickOutside: EventEmitter<MouseEvent> = new EventEmitter<MouseEvent>();

  /**
   * @constructor ClickOutsideDirective
   * @param {ElementRef} elementRef
   * @memberof ClickOutsideDirective
   */
  constructor(private elementRef: ElementRef) {}

  /**
   * On document click
   *
   * @param {MouseEvent} event
   * @return {void}
   * @memberof ClickOutsideDirective
   */
  @HostListener('document:click', ['$event'])
  public onDocumentClick(event: MouseEvent): void {
    const targetElement = event.target as HTMLElement;

    // Check if the click was outside the element
    if (targetElement && !this.elementRef.nativeElement.contains(targetElement)) {
      this.clickOutside.emit(event);
    }
  }
}
