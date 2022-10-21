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

import { Component, HostListener, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ConfirmModalData } from '@shared/modules/modal/core/modal.model';

@Component({
  selector: 'app-confirm',
  templateUrl: './modal.component.html',
  styleUrls: ['./modal.component.scss'],
})
export class ModalComponent {
  @HostListener('keydown.esc')
  public onEsc() {
    this.close(false);
  }

  constructor(
    @Inject(MAT_DIALOG_DATA) public readonly data: ConfirmModalData,
    private readonly matDialogRef: MatDialogRef<ModalComponent>,
  ) {}

  public close(value: boolean): void {
    this.matDialogRef.close(value || false);
  }

  public cancel(): void {
    this.close(false);
  }

  public confirm(): void {
    this.data.formGroup?.markAllAsTouched();
    this.data.formGroup?.updateValueAndValidity();

    if (!this.data.formGroup || this.data.formGroup.valid) {
      this.close(true);
    }
  }
}
