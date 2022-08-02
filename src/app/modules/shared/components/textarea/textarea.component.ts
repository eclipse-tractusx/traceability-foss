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

import { Component, Input, Self, OnInit } from '@angular/core';
import { NgControl, ControlValueAccessor } from '@angular/forms';

@Component({
  selector: 'app-textarea',
  templateUrl: './textarea.component.html',
  styleUrls: ['./textarea.component.scss'],
})
export class TextareaComponent implements ControlValueAccessor, OnInit {
  @Input() label = '';

  public value: string;

  public onChange = (_value: string) => {};

  public onTouched = () => {};

  public touched = false;

  public disabled = false;

  constructor(@Self() public ngControl: NgControl) {
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
    if (!this.touched) {
      this.onTouched();
      this.touched = true;
    }
  }

  public setDisabledState(disabled: boolean): void {
    this.disabled = disabled;
  }

  public getTextAreaFromEvent(event: Event): HTMLTextAreaElement {
    return event.target as HTMLTextAreaElement;
  }
}
