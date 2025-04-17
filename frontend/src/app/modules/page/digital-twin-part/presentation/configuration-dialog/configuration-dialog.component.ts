/********************************************************************************
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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

import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import {
    OrderConfigurationRequest,
    OrderConfigurationResponse,
    TriggerConfigurationRequest,
    TriggerConfigurationResponse,
} from './model/configuration.model';
import { ConfigurationService } from '@shared/service/configuration.service';

@Component({
    selector: 'app-configuration-dialog',
    templateUrl: './configuration-dialog.component.html',
    styleUrls: ['./configuration-dialog.component.scss'],
})
export class ConfigurationDialogComponent {
    form: FormGroup;
    selectedTabIndex: number = 0;

    constructor(
        private readonly fb: FormBuilder,
        private readonly configService: ConfigurationService,
        public dialogRef: MatDialogRef<ConfigurationDialogComponent>,
        @Inject(MAT_DIALOG_DATA)
        public data: { order?: OrderConfigurationResponse; trigger?: TriggerConfigurationResponse },
    ) { }

    ngOnInit(): void {
        this.form = this.fb.group({
            order: this.fb.group({
                batchSize: [null, [Validators.required, Validators.min(10), Validators.max(100)]],
                timeoutMs: [null, [Validators.required, Validators.min(60), Validators.max(86400)]],
                jobTimeoutMs: [null, [Validators.required, Validators.min(60), Validators.max(7200)]],
            }),
            trigger: this.fb.group({
                cronExpressionRegisterOrderTTLReached: ['', [Validators.required, validQuartzCron]],
                cronExpressionMapCompletedOrders: ['', [Validators.required, validQuartzCron]],
                partTTL: [null, [Validators.required, Validators.min(1)]],
                cronExpressionAASLookup: ['', [Validators.required, validQuartzCron]],
                cronExpressionAASCleanup: ['', [Validators.required, validQuartzCron]],
                cronExpressionPublishAssets: ['', [Validators.required, validQuartzCron]], 
                aasTTL: [null, [Validators.required, Validators.min(1)]],
                aasLimit: [null, [Validators.required, Validators.min(1)]],
            }),
        });

        if (this.data?.order) {
            this.orderFormGroup.patchValue(this.data.order);
        }

        if (!this.data?.trigger) {
            this.configService.getLatestTriggerConfiguration().subscribe(data => {
                this.triggerFormGroup.patchValue(data);
            });
        } else {
            this.triggerFormGroup.patchValue(this.data.trigger);
        }
    }

    onTabChange(event: number): void {
        this.selectedTabIndex = event;
    }

    submit(): void {
        if (this.selectedTabIndex === 0) {
            const orderConfig: OrderConfigurationRequest = this.orderFormGroup.value;
            this.configService.postOrderConfiguration(orderConfig).subscribe();
        } else {
            const triggerConfig: TriggerConfigurationRequest = this.triggerFormGroup.value;
            this.configService.postTriggerConfiguration(triggerConfig).subscribe();
        }

        this.dialogRef.close({ success: true });
    }

    cancel(): void {
        this.dialogRef.close({ success: true });
    }

    get orderFormGroup(): FormGroup {
        return this.form.get('order') as FormGroup;
    }

    get triggerFormGroup(): FormGroup {
        return this.form.get('trigger') as FormGroup;
    }
}

export function validQuartzCron(control: AbstractControl): ValidationErrors | null {
    const value = control.value?.trim();
    if (!value) {
    return { invalidCron: true };
    }
    
    // Matches 6 or 7 space-separated parts
    const quartzCronRegex = /^([0-9*/?,-LW#C]+ ){5,6}[0-9*/?,-LW#C]+$/i;
    
    if (!quartzCronRegex.test(value)) {
    return { invalidCron: true };
    }
    
    return null;
    }
