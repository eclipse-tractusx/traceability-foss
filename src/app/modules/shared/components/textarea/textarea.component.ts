/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
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

import { Component, Input, OnInit, Self } from '@angular/core';
import { ControlValueAccessor, NgControl } from '@angular/forms';

@Component({
  selector: 'app-textarea',
  templateUrl: './textarea.component.html',
  styleUrls: ['./textarea.component.scss'],
})
export class TextareaComponent implements ControlValueAccessor, OnInit {
  @Input() label = '';
  public value: string;
  public touched = false;
  public disabled = false;

  public onChange: ((_value: string) => {}) | null = null;
  public onTouched: (() => void) | null = null;

  constructor(@Self() public readonly ngControl: NgControl) {
    // we cannot provide this component in NG_VALUE_ACCESSOR, as we want to have access to the ngControl
    // so we have to setup valueAccessor manualy
    this.ngControl.valueAccessor = this;
  }

  public ngOnInit(): void {
    // finish ngControl setup
    this.ngControl.control.updateValueAndValidity();
  }

  public writeValue(value: string): void {
    this.value = value;
  }

  public registerOnChange(onChange: any): void {
    this.onChange = onChange;
  }

  public registerOnTouched(onTouched: any): void {
    this.onTouched = onTouched;
  }

  public markAsTouched(): void {
    if (this.touched) {
      return;
    }

    this.onTouched?.();
    this.touched = true;
  }

  public setDisabledState(disabled: boolean): void {
    this.disabled = disabled;
  }

  public getTextAreaFromEvent(event: Event): HTMLTextAreaElement {
    return event.target as HTMLTextAreaElement;
  }
}
