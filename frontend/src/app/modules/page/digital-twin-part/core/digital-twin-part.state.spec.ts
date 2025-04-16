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
import { DigitalTwinPartState } from './digital-twin-part.state';
import { Pagination } from '@core/model/pagination.model';
import { View } from '@shared/model/view.model';
import { DigitalTwinPartResponse } from '@page/digital-twin-part/model/digitalTwinPart.model';
import { take } from 'rxjs';

describe('DigitalTwinPartState', () => {
  let state: DigitalTwinPartState;

  beforeEach(() => {
    state = new DigitalTwinPartState();
  });

  it('should initialize with loader=true', (done) => {
    state.digitalTwinParts$.pipe(take(1)).subscribe(view => {
      expect(view).toEqual(jasmine.objectContaining({ loader: true }));
      done();
    });
  });

  it('should update state with data via setter', () => {
    const mockData: Pagination<DigitalTwinPartResponse> = {
      page: 0,
      pageCount: 1,
      pageSize: 10,
      totalItems: 1,
      content: []
    };

    const view: View<Pagination<DigitalTwinPartResponse>> = { data: mockData };
    state.digitalTwinParts = view;

    expect(state.digitalTwinParts).toEqual(view);
  });

  it('should emit updated state to observers', (done) => {
    const mockData: Pagination<DigitalTwinPartResponse> = {
      page: 0,
      pageCount: 1,
      pageSize: 10,
      totalItems: 1,
      content: []
    };

    const view: View<Pagination<DigitalTwinPartResponse>> = { data: mockData };

    state.digitalTwinParts$.pipe(take(2)).subscribe({
      next: (value) => {
        if (value.data) {
          expect(value.data).toEqual(mockData);
          done();
        }
      }
    });

    state.digitalTwinParts = view;
  });
});
