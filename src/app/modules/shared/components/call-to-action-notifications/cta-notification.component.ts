/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { Component, Inject } from '@angular/core';
import { MAT_SNACK_BAR_DATA, MatSnackBar } from '@angular/material/snack-bar';
import { I18nMessage } from '@shared/model/i18n-message';
import { CallAction, CtaNotificationData } from './cta-notification.model';

// CTA stands for call-to-action
@Component({
  selector: 'cta-notification',
  templateUrl: './cta-notification.component.html',
})
export class CtaNotificationComponent {
  public get text(): I18nMessage {
    return this.data.text;
  }

  public get actions(): CallAction[] {
    return this.data.actions;
  }

  constructor(
    @Inject(MAT_SNACK_BAR_DATA)
    private readonly data: CtaNotificationData,
    private readonly snackBar: MatSnackBar,
  ) {}

  public onActionClick(): void {
    this.snackBar.dismiss();
  }
}
