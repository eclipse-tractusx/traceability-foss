/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

import {AfterViewInit, Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { bpnRegex } from '@page/admin/presentation/bpn-configuration/bpn-configuration.component';
import { Part } from '@page/parts/model/parts.model';
import { BaseInputHelper } from '@shared/abstraction/baseInput/baseInput.helper';
import { DateValidators } from '@shared/components/dateTime/dateValidators.model';
import { ToastService } from '@shared/components/toasts/toast.service';
import {Notification, NotificationType} from '@shared/model/notification.model';
import { Severity } from '@shared/model/severity.model';
import { NotificationService } from '@shared/service/notification.service';
import {BehaviorSubject, Observable, Subscription} from 'rxjs';
import {filter, tap} from "rxjs/operators";
import {NotificationDetailFacade} from "@page/notifications/core/notification-detail.facade";
import {View} from "@shared/model/view.model";

@Component({
  selector: 'app-notification-new-request',
  templateUrl: './notification-new-request.component.html',
})
export class RequestNotificationNewComponent implements  AfterViewInit, OnDestroy{
  @Input() title: string;
  @Input() selectedItems: Part[];
  private subscription: Subscription;
  @Input() editMode: boolean;
  formGroup = new FormGroup<any>({});
  public readonly selected$: Observable<View<Notification>>;
  @Output() submitted = new EventEmitter<void>();

  public readonly isLoading$ = new BehaviorSubject(false);
  public readonly minDate = new Date();
  public selectedNotification: Notification;
  constructor(public readonly notificationDetailFacade: NotificationDetailFacade,) {
    this.selected$ = this.notificationDetailFacade.selected$;
    this.formGroup.addControl('title', new FormControl('', [ Validators.maxLength(30), Validators.minLength(0) ]));
    this.formGroup.addControl('description', new FormControl('', [ Validators.required, Validators.maxLength(1000), Validators.minLength(15) ]));
    this.formGroup.addControl('severity', new FormControl(Severity.MINOR, [ Validators.required ]));
    this.formGroup.addControl('targetDate', new FormControl(null, []));
    this.formGroup.addControl('bpn', new FormControl(null, [ Validators.required, BaseInputHelper.getCustomPatternValidator(bpnRegex, 'bpn') ]));
    this.formGroup.addControl('type', new FormControl(NotificationType.INVESTIGATION, [ Validators.required ]));

    this.formGroup.valueChanges.subscribe((data: any) => {
      console.log('Form has been changed', data);
      // You can perform any action here when the form is changed
    });


  }
  public ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

  public ngAfterViewInit(): void {
    if (this.editMode){
      this.formGroup.get('type').disable();
    }
    this.subscription = this.selected$
        .pipe(
            filter(({ data }) => !!data),
            tap(({ data }) => {
              // TODO check if we need to set it after form change to the parent comp
           //   this.setTableConfigs(data);
              this.selectedNotification = data;
              this.formGroup.controls['title'].setValue(data.title);
              this.formGroup.controls['description'].setValue(data.description);
              this.formGroup.controls['severity'].setValue(data.severity);
              this.formGroup.controls['type'].setValue(data.type);
              this.formGroup.controls['bpn'].setValue(data.sendTo);
              if (!data.targetDate.isInitial()){
                this.formGroup.controls['targetDate'].patchValue(data.targetDate.valueOf().toISOString().slice(0, 16));
              }
            }),
        )
        .subscribe();
  }

  protected prepareSubmit(): void {
    this.formGroup.markAllAsTouched();
    this.formGroup.updateValueAndValidity();

    if (this.formGroup.invalid) return;

    this.isLoading$.next(true);
    this.formGroup.disable();
  }

}
