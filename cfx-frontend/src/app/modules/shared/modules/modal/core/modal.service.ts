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

import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { ModalComponent } from '@shared/modules/modal/component/modal.component';
import { ModalData } from '@shared/modules/modal/core/modal.model';
import { Subscription } from 'rxjs';
import { take, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class ModalService {
  private dialogRef: MatDialogRef<ModalComponent>;
  private dialogConfirmSubscription: Subscription;

  constructor(private readonly matDialog: MatDialog) {
  }

  public open(modalData: ModalData): void {
    const { title, template, buttonLeft, buttonRight, onConfirm, formGroup, primaryButtonColour, notificationId, type } = modalData;
    const data = { title, template, buttonLeft, buttonRight, primaryButtonColour, formGroup, notificationId, type };

    this.dialogRef = this.matDialog.open(ModalComponent, { data });

    this.dialogConfirmSubscription = this.dialogRef
      .afterClosed()
      .pipe(
        take(1),
        tap(res => onConfirm?.(res)),
      )
      .subscribe();
  }
}
