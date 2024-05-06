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
import { BomLifecycleSettingsService } from '@shared/service/bom-lifecycle-settings.service';

describe('BomLifecycleConfigUserSetting', () => {

  let service: BomLifecycleSettingsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BomLifecycleSettingsService);
  });

  afterEach(() => {
    service.clearUserSettings();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return default settings when no settings are stored for PARTS', () => {
    const defaultSettings = service.getUserSettings();
    expect(defaultSettings).toEqual({
      asBuiltSize: 50,
      asPlannedSize: 50,
    });
  });

  it('should store and retrieve user settings', () => {
    const newSettings = {
      asBuiltSize: 0,
      asPlannedSize: 100,
    };
    service.setUserSettings(newSettings);
    const retrievedSettings = service.getUserSettings();
    expect(retrievedSettings).toEqual(newSettings);
  });

  it('should clear user settings', () => {
    const newSettings = {
      asBuiltSize: 0,
      asPlannedSize: 100,
    };
    service.setUserSettings(newSettings);
    service.clearUserSettings();
    const retrievedSettings = service.getUserSettings();
    expect(retrievedSettings).toEqual({
      asBuiltSize: 50,
      asPlannedSize: 50,
    });
  });

});
