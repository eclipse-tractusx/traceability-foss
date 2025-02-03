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

import { TestBed } from '@angular/core/testing';
import { BomLifecycleSettingsService, UserSettingView } from "@shared/service/bom-lifecycle-settings.service";

describe('BomLifecycleConfigUserSetting', () => {

    let service: BomLifecycleSettingsService;

    beforeEach(() => {
        TestBed.configureTestingModule({});
        service = TestBed.inject(BomLifecycleSettingsService);
    });

    afterEach(() => {
        service.clearUserSettings(UserSettingView.PARTS);
        service.clearUserSettings(UserSettingView.OTHER_PARTS);
    })

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('should return default settings when no settings are stored for PARTS', () => {
        const defaultSettings = service.getUserSettings(UserSettingView.PARTS);
        expect(defaultSettings).toEqual({
            asDesignedActive: false,
            asBuiltActive: true,
            asOrderedActive: false,
            asPlannedActive: true,
            asSupportedActive: false,
            asRecycledActive: false
        });
    });

    it('should return default settings when no settings are stored for OTHER_PARTS', () => {
        const defaultSettings = service.getUserSettings(UserSettingView.OTHER_PARTS);
        expect(defaultSettings).toEqual({
            asDesignedActive: false,
            asBuiltActive: true,
            asOrderedActive: false,
            asPlannedActive: true,
            asSupportedActive: false,
            asRecycledActive: false
        });
    });

    it('should store and retrieve user settings', () => {
        const newSettings = {
            asBuiltActive: false,
            asPlannedActive: true,
            asDesignedActive: false,
            asOrderedActive: false,
            asSupportedActive: false,
            asRecycledActive: false
        };
        service.setUserSettings(newSettings, UserSettingView.PARTS);
        const retrievedSettings = service.getUserSettings(UserSettingView.PARTS);
        expect(retrievedSettings).toEqual(newSettings);
    });

    it('should clear user settings', () => {
        const newSettings = {
            asBuiltActive: false,
            asPlannedActive: true,
            asDesignedActive: false,
            asOrderedActive: false,
            asSupportedActive: false,
            asRecycledActive: false
        };
        service.setUserSettings(newSettings, UserSettingView.PARTS);
        service.clearUserSettings(UserSettingView.PARTS);
        const retrievedSettings = service.getUserSettings(UserSettingView.PARTS);
        expect(retrievedSettings).toEqual({
            asDesignedActive: false,
            asBuiltActive: true,
            asOrderedActive: false,
            asPlannedActive: true,
            asSupportedActive: false,
            asRecycledActive: false
        });
    });
});
