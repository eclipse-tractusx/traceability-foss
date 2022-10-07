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
import { Observable, Subject } from 'rxjs';
import { ToastMessage, ToastStatus } from './toast-message/toast-message.model';

@Injectable({
  providedIn: 'root',
})
export class ToastService {
  private toastStore = new Subject<ToastMessage>();
  private idx = 0;

  public getCurrentToast$(): Observable<ToastMessage> {
    return this.toastStore.asObservable();
  }

  public success(message: string, timeout = 5000): void {
    this.toastStore.next(new ToastMessage(this.idx++, message, ToastStatus.Success, timeout));
  }

  public info(message: string, timeout = 5000): void {
    this.toastStore.next(new ToastMessage(this.idx++, message, ToastStatus.Informative, timeout));
  }

  public error(message: string, timeout = 5000): void {
    this.toastStore.next(new ToastMessage(this.idx++, message, ToastStatus.Error, timeout));
  }

  public warning(message: string, timeout = 5000): void {
    this.toastStore.next(new ToastMessage(this.idx++, message, ToastStatus.Warning, timeout));
  }
}
