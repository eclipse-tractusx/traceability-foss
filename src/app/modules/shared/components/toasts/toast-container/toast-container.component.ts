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

import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { ToastMessage } from '../toast-message/toast-message.model';
import { ToastService } from '../toast.service';
import { notifyAnimation } from './animation';

@Component({
  selector: 'app-toast-container',
  templateUrl: './toast-container.component.html',
  styleUrls: ['./toast-container.component.scss'],
  animations: [notifyAnimation],
})
export class ToastContainerComponent implements OnInit, OnDestroy {
  public toastMessages: ToastMessage[] = [];

  private subscription: Subscription;

  constructor(private readonly toastService: ToastService) {}

  public ngOnInit(): void {
    this.subscription = this.toastService.getCurrentToast$().subscribe(toast => this.add(toast));
  }

  public ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  public remove({ id }: ToastMessage): void {
    this.toastMessages = this.toastMessages.filter(toast => toast.id !== id);
  }

  public add(message: ToastMessage): void {
    this.toastMessages.unshift(message);
    setTimeout(() => this.remove(message), message.timeout);
  }
}
