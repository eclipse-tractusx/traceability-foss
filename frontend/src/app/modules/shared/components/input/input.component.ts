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

import {
  Component,
  ElementRef,
  EventEmitter,
  HostListener,
  Inject,
  Injector,
  Input,
  Output,
  ViewChild,
} from '@angular/core';
import { BaseInputComponent } from '@shared/abstraction/baseInput/baseInput.component';
import { StaticIdService } from '@shared/service/staticId.service';
import { ThemePalette } from '@angular/material/core';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-input',
  templateUrl: './input.component.html',
  styleUrls: [ './input.component.scss' ],
})
export class InputComponent extends BaseInputComponent<string> {
  @Input() suffixIcon: string;
  @Input() suffixIconColor: ThemePalette;
  @Input() suffixIconHover: boolean = false;
  @Input() onEnterActive: boolean = false;
  @Input() displayClearButton: boolean = false;
  @Input() parentFormGroup: FormGroup;
  @Input() parentControlName: string;
  @Output() suffixIconClick = new EventEmitter<void>();
  @ViewChild('inputElement') inputElement: ElementRef;

  constructor(@Inject(Injector) injector: Injector, staticIdService: StaticIdService) {
    super(injector, staticIdService);
  }

  @HostListener('keydown.enter', [ '$event' ])
  onEnterKey(event: KeyboardEvent): void {
    // Check if the Enter key was pressed
    if (event.key === 'Enter') {
      // Trigger the suffixIconClick output event
      if (this.onEnterActive) {
        this.suffixIconClick.emit();
      }
    }
  }

  clearIconClick(): void {
    if (this.parentControlName && this.parentFormGroup) {
      this.parentFormGroup.get(this.parentControlName).setValue('');
      this.suffixIconClick.emit();
    }
  }
}
