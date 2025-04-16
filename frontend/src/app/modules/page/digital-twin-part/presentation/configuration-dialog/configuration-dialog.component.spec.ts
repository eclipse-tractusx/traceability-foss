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


import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ConfigurationDialogComponent, validQuartzCron } from './configuration-dialog.component';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ConfigurationService } from '@shared/service/configuration.service';
import { of } from 'rxjs';
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'i18n' })
class MockI18nPipe implements PipeTransform {
    transform(value: string): string {
        return value;
    }
}

describe('ConfigurationDialogComponent Validation', () => {
    let component: ConfigurationDialogComponent;
    let fixture: ComponentFixture<ConfigurationDialogComponent>;
    let mockDialogRef: jasmine.SpyObj<MatDialogRef<ConfigurationDialogComponent>>;
    let mockConfigService: jasmine.SpyObj<ConfigurationService>;

    beforeEach(async () => {
        mockDialogRef = jasmine.createSpyObj('MatDialogRef', ['close']);
        mockConfigService = jasmine.createSpyObj('ConfigurationService', [
            'getLatestTriggerConfiguration',
            'postOrderConfiguration',
            'postTriggerConfiguration',
        ]);

        mockConfigService.getLatestTriggerConfiguration.and.returnValue(of({
            id: 1,
            cronExpressionRegisterOrderTTLReached: '* * * * * *',
            cronExpressionMapCompletedOrders: '* * * * * *',
            partTTL: 60,
            cronExpressionAASLookup: '* * * * * *',
            cronExpressionAASCleanup: '* * * * * *',
            cronExpressionPublishAssets: '* * * * * *',
            aasTTL: 60,
            aasLimit: 1000
        }));

        mockConfigService.postOrderConfiguration.and.returnValue(of(void 0));
        mockConfigService.postTriggerConfiguration.and.returnValue(of(void 0));

        await TestBed.configureTestingModule({
            declarations: [ConfigurationDialogComponent, MockI18nPipe],
            imports: [ReactiveFormsModule],
            providers: [
                { provide: MatDialogRef, useValue: mockDialogRef },
                { provide: MAT_DIALOG_DATA, useValue: {} },
                { provide: ConfigurationService, useValue: mockConfigService },
            ],
        }).compileComponents();

        fixture = TestBed.createComponent(ConfigurationDialogComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create the component and initialize the form', () => {
        expect(component).toBeTruthy();
        expect(component.form.contains('order')).toBeTrue();
        expect(component.form.contains('trigger')).toBeTrue();
    });

    it('should mark order fields as invalid if empty', () => {
        component.orderFormGroup.setValue({
            batchSize: null,
            timeoutMs: null,
            jobTimeoutMs: null,
        });

        expect(component.orderFormGroup.valid).toBeFalse();
        expect(component.orderFormGroup.get('batchSize')?.hasError('required')).toBeTrue();
    });

    it('should validate min/max for order fields', () => {
        component.orderFormGroup.setValue({
            batchSize: 5,
            timeoutMs: 100000,
            jobTimeoutMs: 50,
        });

        expect(component.orderFormGroup.get('batchSize')?.hasError('min')).toBeTrue();
        expect(component.orderFormGroup.get('timeoutMs')?.hasError('max')).toBeTrue();
        expect(component.orderFormGroup.get('jobTimeoutMs')?.hasError('min')).toBeTrue();
    });

    it('should validate cron expressions in trigger form', () => {
        component.triggerFormGroup.setValue({
            cronExpressionRegisterOrderTTLReached: 'bad cron',
            cronExpressionMapCompletedOrders: '* * * * * *',
            partTTL: 60,
            cronExpressionAASLookup: '* * * * * *',
            cronExpressionAASCleanup: '* *',
            cronExpressionPublishAssets: '* * * * * *',
            aasTTL: 60,
            aasLimit: 1000,
        });

        expect(component.triggerFormGroup.get('cronExpressionRegisterOrderTTLReached')?.hasError('invalidCron')).toBeTrue();
        expect(component.triggerFormGroup.get('cronExpressionAASCleanup')?.hasError('invalidCron')).toBeTrue();
    });

    it('should validate required numeric fields in trigger form', () => {
        component.triggerFormGroup.setValue({
            cronExpressionRegisterOrderTTLReached: '* * * * * *',
            cronExpressionMapCompletedOrders: '* * * * * *',
            partTTL: null,
            cronExpressionAASLookup: '* * * * * *',
            cronExpressionAASCleanup: '* * * * * *',
            cronExpressionPublishAssets: '* * * * * *',
            aasTTL: null,
            aasLimit: null,
        });

        expect(component.triggerFormGroup.get('partTTL')?.hasError('required')).toBeTrue();
        expect(component.triggerFormGroup.get('aasTTL')?.hasError('required')).toBeTrue();
        expect(component.triggerFormGroup.get('aasLimit')?.hasError('required')).toBeTrue();
    });

    it('should call postOrderConfiguration when order tab is active', () => {
        component.selectedTabIndex = 0;
        component.orderFormGroup.setValue({
            batchSize: 20,
            timeoutMs: 600,
            jobTimeoutMs: 600
        });

        component.submit();

        expect(mockConfigService.postOrderConfiguration).toHaveBeenCalledWith({
            batchSize: 20,
            timeoutMs: 600,
            jobTimeoutMs: 600
        });

        expect(mockDialogRef.close).toHaveBeenCalledWith({ success: true });
    });

    it('should call postTriggerConfiguration when trigger tab is active', () => {
        component.selectedTabIndex = 1;
        component.triggerFormGroup.setValue({
            cronExpressionRegisterOrderTTLReached: '* * * * * *',
            cronExpressionMapCompletedOrders: '* * * * * *',
            partTTL: 60,
            cronExpressionAASLookup: '* * * * * *',
            cronExpressionAASCleanup: '* * * * * *',
            cronExpressionPublishAssets: '* * * * * *',
            aasTTL: 60,
            aasLimit: 1000
        });

        component.submit();

        expect(mockConfigService.postTriggerConfiguration).toHaveBeenCalledWith({
            cronExpressionRegisterOrderTTLReached: '* * * * * *',
            cronExpressionMapCompletedOrders: '* * * * * *',
            partTTL: 60,
            cronExpressionAASLookup: '* * * * * *',
            cronExpressionAASCleanup: '* * * * * *',
            cronExpressionPublishAssets: '* * * * * *',
            aasTTL: 60,
            aasLimit: 1000
        });

        expect(mockDialogRef.close).toHaveBeenCalledWith({ success: true });
    });


});
