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
import { TableSettingsService } from '@core/user/table-settings.service';

describe('TableSettingsService', () => {
  let service: TableSettingsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [],
      providers: [ TableSettingsService ],
    });
    service = TestBed.inject(TableSettingsService);
  });


  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
