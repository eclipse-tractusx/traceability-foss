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

import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { bpnRegex } from '@page/admin/presentation/bpn-configuration/bpn-configuration.component';
import { NotificationDetailFacade } from '@page/notifications/core/notification-detail.facade';
import { BaseInputHelper } from '@shared/abstraction/baseInput/baseInput.helper';
import { Notification, NotificationType } from '@shared/model/notification.model';
import { Severity } from '@shared/model/severity.model';
import { View } from '@shared/model/view.model';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { filter, tap } from 'rxjs/operators';

@Component({
  selector: 'app-notification-new-request',
  templateUrl: './notification-new-request.component.html',
})
export class RequestNotificationNewComponent implements OnDestroy, OnInit {
  @Input() title: string;
  @Input() editMode: boolean;
  @Output() formGroupChanged = new EventEmitter<FormGroup>();


  public readonly formGroup = new FormGroup<any>({
    'title': new FormControl('', [ Validators.maxLength(30), Validators.minLength(0) ]),
    'description': new FormControl('', [ Validators.required, Validators.maxLength(1000), Validators.minLength(15) ]),
    'severity': new FormControl(Severity.MINOR, [ Validators.required ]),
    'targetDate': new FormControl(null),
    'bpn': new FormControl(null, [ Validators.required, BaseInputHelper.getCustomPatternValidator(bpnRegex, 'bpn') ]),
    'type': new FormControl(NotificationType.INVESTIGATION, [ Validators.required ]),
  });
  public selected$: Observable<View<Notification>>;

  public readonly isLoading$ = new BehaviorSubject(false);
  public readonly minDate = new Date();

  private subscription: Subscription;

  constructor(public readonly notificationDetailFacade: NotificationDetailFacade) {

  }

  ngOnInit(): void {
    console.log(this.editMode)
    if(this.editMode) {
      this.selected$ = this.notificationDetailFacade.selected$;
    }
    this.formGroup.valueChanges.subscribe(value => {
      //TODO: For Create, check here or in parent if the part tables should update (depending on passed partId, investigation or alert type)
      this.formGroupChanged.emit(this.formGroup);
    })

    if(this.selected$) {
      this.subscription = this.selected$
        .pipe(
          filter(({ data }) => !!data),
          tap(({ data }) => {
            const { title, description, severity, type, sendTo, targetDate } = data;
            this.formGroup.setValue({
              'title': title,
              'description': description,
              'severity': severity,
              'type': type,
              'bpn': sendTo,
              'targetDate': targetDate.isInitial() ? null : targetDate.valueOf().toISOString().slice(0, 16),
            });
            if (this.editMode) {
              this.formGroup.get('type').disable();
            }
            this.formGroupChanged.emit(this.formGroup);
          }),
        )
        .subscribe();
    }

  }

  public ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }
}
