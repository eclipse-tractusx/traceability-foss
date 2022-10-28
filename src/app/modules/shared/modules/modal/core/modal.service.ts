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

import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { ModalComponent } from '@shared/modules/modal/component/modal.component';
import { ConfirmModalData } from '@shared/modules/modal/core/modal.model';
import { Subscription } from 'rxjs';
import { take, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class ModalService {
  private dialogRef: MatDialogRef<ModalComponent>;
  private dialogConfirmSubscription: Subscription;

  constructor(private readonly matDialog: MatDialog) {}

  public open({ title, template, cancelText, confirmText, onConfirm, formGroup }: ConfirmModalData, width = 600): void {
    const data = { title, template, cancelText, confirmText, formGroup };
    this.dialogRef = this.matDialog.open(ModalComponent, { width: `${width}px`, data });

    this.dialogConfirmSubscription = this.dialogRef
      .afterClosed()
      .pipe(
        take(1),
        tap(res => onConfirm?.(res)),
      )
      .subscribe();
  }
}
