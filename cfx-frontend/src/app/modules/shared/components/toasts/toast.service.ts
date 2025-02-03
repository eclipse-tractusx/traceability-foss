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
import { I18nMessage } from '@shared/model/i18n-message';
import { Observable, Subject } from 'rxjs';
import { CallAction, ToastMessage, ToastStatus } from './toast-message/toast-message.model';

@Injectable({
  providedIn: 'root',
})
export class ToastService {
  private toastStore = new Subject<ToastMessage>();
  private idx = 0;

  public getCurrentToast$(): Observable<ToastMessage> {
    return this.toastStore.asObservable();
  }

  public success(title: I18nMessage, message: I18nMessage | string, timeout = 5000, actions?: CallAction[]): void {
    if (actions) {
      this.toastStore.next(new ToastMessage(this.idx++, title, message, ToastStatus.Success, timeout, actions));
    } else {
      this.toastStore.next(new ToastMessage(this.idx++, title, message, ToastStatus.Success, timeout));
    }
  }

  public info(title: I18nMessage, message: I18nMessage, timeout = 5000): void {
    this.toastStore.next(new ToastMessage(this.idx++, title, message, ToastStatus.Informative, timeout));
  }

  public error(title: I18nMessage, message: I18nMessage, timeout = 5000): void {
    this.toastStore.next(new ToastMessage(this.idx++, title, message, ToastStatus.Error, timeout));
  }

  public warning(title: I18nMessage, message: I18nMessage, timeout = 5000): void {
    this.toastStore.next(new ToastMessage(this.idx++, title, message, ToastStatus.Warning, timeout));
  }
}
