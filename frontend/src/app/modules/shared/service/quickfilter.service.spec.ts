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

import { TestBed } from '@angular/core/testing';
import { Owner } from '@page/parts/model/owner.enum';

import { QuickfilterService } from './quickfilter.service';

describe('QuickfilterService', () => {
  let service: QuickfilterService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(QuickfilterService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should have an initial owner of UNKNOWN', () => {
    expect(service.getOwner()).toBe(Owner.UNKNOWN);
    expect(service.isQuickFilterSet()).toBeFalse();
  });

  it('should set owner and reflect in getOwner()', () => {
    service.setOwner(Owner.OWN);
    expect(service.getOwner()).toBe(Owner.OWN);
    expect(service.isQuickFilterSet()).toBeTrue();
  });

  it('should emit new values through owner$', (done) => {
    const expectedOwner = Owner.SUPPLIER;
    service.owner$.subscribe({
      next: (ownerValue) => {
        if (ownerValue === expectedOwner) {
          expect(ownerValue).toBe(expectedOwner);
          done();
        }
      }
    });

    service.setOwner(expectedOwner);
  });

  it('isQuickFilterSet() should return false when owner is set back to UNKNOWN', () => {
    service.setOwner(Owner.OWN);
    service.setOwner(Owner.UNKNOWN);
    expect(service.getOwner()).toBe(Owner.UNKNOWN);
    expect(service.isQuickFilterSet()).toBeFalse();
  });
});
