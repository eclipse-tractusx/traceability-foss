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

import { Component, ElementRef, Input, ViewChild } from '@angular/core';

type ButtonVariant = 'button' | 'raised' | 'flat' | 'stroked' | 'icon' | 'fab' | 'miniFab';

@Component({
  selector: 'app-button',
  templateUrl: './button.component.html',
})
export class ButtonComponent {
  @ViewChild('ButtonElement') buttonElement: ElementRef;
  @Input() color: 'primary' | 'accent' | 'warn';
  @Input() variant: ButtonVariant = 'button';

  @Input() label: string;
  @Input() iconName: string;
  @Input() isDisabled: boolean = false;

  public getClasses(): string {
    return 'mat-' + this.color;
  }
}
