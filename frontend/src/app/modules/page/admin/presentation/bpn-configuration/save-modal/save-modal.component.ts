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

import { Component, Input, TemplateRef, ViewChild } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { ToastService } from '@shared/components/toasts/toast.service';
import { ModalService } from '@shared/modules/modal/core/modal.service';
import { ModalData } from '@shared/modules/modal/core/modal.model';
import { ChangedInformation } from '@page/admin/presentation/bpn-configuration/bpn-configuration.model';
import { BpnConfig } from '@page/admin/core/admin.model';

@Component({
  selector: 'app-save-bpn-config-modal',
  templateUrl: './save-modal.component.html',
  styleUrls: [ './save-modal.component.scss' ],
})
export class SaveBpnConfigModal {
  @ViewChild('Modal') modal: TemplateRef<unknown>;
  @Input() updateCall: (changeInformation: ChangedInformation) => Observable<void>;

  public changedInformation$ = new BehaviorSubject<ChangedInformation>(null);
  public originalValues$ = new BehaviorSubject<BpnConfig[]>(null);

  constructor(private readonly toastService: ToastService, private readonly confirmModalService: ModalService) {
  }

  public show(changedInformation: ChangedInformation, originalValues: BpnConfig[]): void {
    this.changedInformation$.next(changedInformation);
    this.originalValues$.next(originalValues);

    const onConfirm = (isConfirmed: boolean) => {
      if (!isConfirmed) return;

      this.updateCall(changedInformation).subscribe({
        next: () => {
          this.toastService.success('pageAdmin.bpnConfig.modal.successfullySaved');
        },
        error: () => {
          this.toastService.error('pageAdmin.bpnConfig.modal.failedSave');
        },
      });
    };

    const options: ModalData = {
      title: 'pageAdmin.bpnConfig.modal.title',
      buttonRight: 'actions.save',
      buttonLeft: 'actions.cancel',

      template: this.modal,
      onConfirm,
    };

    this.confirmModalService.open(options);
  }

  public getOriginalValue(changed: BpnConfig) {
    return this.originalValues$.value.find(({ bpn }) => bpn === changed.bpn);
  }
}
