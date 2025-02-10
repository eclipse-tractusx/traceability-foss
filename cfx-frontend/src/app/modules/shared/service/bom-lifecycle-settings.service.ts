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

import { Injectable } from '@angular/core';
import {
    BomLifecycleConfig,
    BomLifecycleSize
} from "@shared/components/bom-lifecycle-activator/bom-lifecycle-activator.model";

export enum UserSettingView {
    PARTS = 'parts', OTHER_PARTS = 'other_parts'
}

@Injectable({
    providedIn: 'root',
})
export class BomLifecycleSettingsService {
    private readonly DEFAULT: BomLifecycleConfig = {
        asDesignedActive: false,
        asBuiltActive: true,
        asOrderedActive: false,
        asPlannedActive: true,
        asSupportedActive: false,
        asRecycledActive: false
    };

    getUserSettings(userSettingView: UserSettingView): BomLifecycleConfig {
        const settingsJson = localStorage.getItem(userSettingView.toString());
        if (settingsJson) {
            return JSON.parse(settingsJson);
        }
        return this.DEFAULT;
    };

    getSize(userSettingView: UserSettingView): BomLifecycleSize {
        let size: BomLifecycleSize;
        const userSettings: BomLifecycleConfig = this.getUserSettings(userSettingView);

        if (userSettings.asPlannedActive && userSettings.asBuiltActive) {
            size = {
                asBuiltSize: 50,
                asPlannedSize: 50,
                asDesignedSize: 0,
                asOrderedSize: 0,
                asRecycledSize: 0,
                asSupportedSize: 0,
            };
        } else if (userSettings.asPlannedActive) {
            size = {
                asDesignedSize: 0,
                asBuiltSize: 0,
                asOrderedSize: 0,
                asRecycledSize: 0,
                asSupportedSize: 0,
                asPlannedSize: 100
            };
        } else if (userSettings.asBuiltActive) {
            size = {
                asBuiltSize: 100,
                asDesignedSize: 0,
                asOrderedSize: 0,
                asRecycledSize: 0,
                asSupportedSize: 0,
                asPlannedSize: 0
            };
        }
        return size;
    }

    setUserSettings(settings: BomLifecycleConfig, userSettingView: UserSettingView): void {
        localStorage.setItem(userSettingView.toString(), JSON.stringify(settings));
    }

    clearUserSettings(userSettingView: UserSettingView): void {
        localStorage.removeItem(userSettingView.toString());
    }
}
