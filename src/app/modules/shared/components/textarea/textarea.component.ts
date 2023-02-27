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

import { Component, ElementRef, Input, OnInit, Self, ViewChild } from '@angular/core';
import { ControlValueAccessor, NgControl } from '@angular/forms';
import { StaticIdService } from '@shared/service/staticId.service';

@Component({
  selector: 'app-textarea',
  templateUrl: './textarea.component.html',
  styleUrls: ['./textarea.component.scss'],
})
export class TextareaComponent implements ControlValueAccessor, OnInit {
  @ViewChild('textareaRef') textareaRef: ElementRef<HTMLTextAreaElement>;

  @Input() label = '';
  public value: string;
  public disabled = false;

  public onChange: ((_value: string) => {}) | null = null;
  public onTouched: (() => void) | null = null;
  public htmlId: string;

  private htmlIdBase = 'TextAreaComponent-';

  constructor(@Self() public readonly ngControl: NgControl, staticIdService: StaticIdService) {
    // we cannot provide this component in NG_VALUE_ACCESSOR, as we want to have access to the ngControl
    // so we have to setup valueAccessor manualy
    this.ngControl.valueAccessor = this;
    this.htmlId = staticIdService.generateId(this.htmlIdBase);
  }

  public ngOnInit(): void {
    // finish ngControl setup
    this.ngControl.control.updateValueAndValidity();
  }

  public writeValue(value: string): void {
    /* TODO: improve this to more straightforward solution
        by some reason Angular does not update textarea value if rely on data-binding
        as workaround we update it directly after textarea get created */
    if (this.textareaRef?.nativeElement) {
      this.textareaRef.nativeElement.value = value ?? '';
    } else {
      this.value = value;
    }
  }

  public registerOnChange(onChange: any): void {
    this.onChange = onChange;
  }

  public registerOnTouched(onTouched: any): void {
    this.onTouched = onTouched;
  }

  public markAsTouched(): void {
    this.ngControl.control.markAsTouched();
  }

  public setDisabledState(disabled: boolean): void {
    this.disabled = disabled;
  }

  public getTextAreaFromEvent(event: Event): HTMLTextAreaElement {
    return event.target as HTMLTextAreaElement;
  }
}
