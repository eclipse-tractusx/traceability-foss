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

import { Component, ElementRef, Inject, Injector, Input, OnInit, ViewChild } from '@angular/core';
import {
  ControlValueAccessor,
  FormControl,
  FormControlDirective,
  FormControlName,
  FormGroupDirective,
  NgControl,
  NgModel,
} from '@angular/forms';
import { StaticIdService } from '@shared/service/staticId.service';
import { Subject } from 'rxjs';
import { takeUntil, tap } from 'rxjs/operators';
import { MyErrorStateMatcher } from '@shared/abstraction/baseInput/baseInput.helper';

@Component({ selector: 'app-baseInput', template: '' })
export class BaseInputComponent<T> implements ControlValueAccessor, OnInit {
  @ViewChild('inputElement') inputElement: ElementRef<HTMLInputElement>;
  @Input() label = '';
  @Input() hint = '';

  public control!: FormControl;
  public matcher = new MyErrorStateMatcher();

  public maxLength: number;
  public minLength: number;

  public htmlId: string;
  private htmlIdBase = 'BaseInputElement-';

  protected readonly destroy = new Subject<void>();

  constructor(@Inject(Injector) private injector: Injector, staticIdService: StaticIdService) {
    this.htmlId = staticIdService.generateId(this.htmlIdBase);
  }

  public ngOnInit(): void {
    this.setComponentControl();

    // Check validators for length validators
    const oneMillion = 1000000;
    const minLengthErrors = new FormControl('#', this.control.validator).errors;
    const maxLengthErrors = new FormControl('#'.repeat(oneMillion), this.control.validator).errors;

    this.minLength = minLengthErrors?.minlength?.requiredLength || 0;
    this.maxLength = maxLengthErrors?.maxlength?.requiredLength || 0;
  }

  public writeValue(value: T): void {
    this.onChange(value);
  }

  public registerOnChange(fn: (value: T | null) => T): void {
    this.onChange = fn;
  }

  public registerOnTouched(fn: () => void): void {
    this.onTouch = fn;
  }

  public onChange = (value: T | null): T | null => value;

  public onTouch = (): void => { };

  public ngOnDestroy(): void {
    this.destroy.next();
    this.destroy.complete();
  }

  private setComponentControl(): void {
    try {
      const formControl = this.injector.get(NgControl);
      formControl.valueAccessor = this;

      switch (formControl.constructor) {
        case NgModel: {
          const { control, update } = formControl as NgModel;

          this.control = control;

          this.control.valueChanges
            .pipe(
              tap((value: T) => update.emit(value)),
              takeUntil(this.destroy),
            )
            .subscribe();
          break;
        }
        case FormControlName: {
          this.control = this.injector.get(FormGroupDirective).getControl(formControl as FormControlName);
          break;
        }
        default: {
          this.control = (formControl as FormControlDirective).form;
          break;
        }
      }
    } catch (error) {
      this.control = new FormControl();
    }
  }
}
