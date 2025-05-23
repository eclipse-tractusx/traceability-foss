/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2025 Contributors to the Eclipse Foundation
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

import { CalendarDateModel } from '@core/model/calendar-date.model';
import { FlattenObjectPipe } from '@shared/pipes/flatten-object.pipe';
import { screen } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { SharedModule } from '..';

describe('FormatDatePipe', () => {
  let pipe: FlattenObjectPipe;

  beforeEach(() => {
    pipe = new FlattenObjectPipe();
  });
  it('should format date having the 1970 value for the year', async () => {
    const date = new CalendarDateModel(null);
    await renderComponent(`{{ date | formatDate }}`, {
      imports: [ SharedModule ],
      componentProperties: { date },
    });

    expect(screen.getByText('--')).toBeInTheDocument();
  });

  it('should format date without issues', async () => {
    const date = new CalendarDateModel('2022-02-04T13:48:54Z');
    await renderComponent(`{{ date | formatDate }}`, {
      imports: [ SharedModule ],
      componentProperties: { date },
    });

    expect(screen.getByText('2/4/22')).toBeInTheDocument();
  });

  it('should format date with options', async () => {
    const date = new CalendarDateModel('2022-02-04T13:48:54Z');
    await renderComponent(`{{ date | formatDate: {dateStyle: 'medium', timeStyle: 'short', timeZone: 'UTC'} }}`, {
      imports: [ SharedModule ],
      componentProperties: { date },
    });

    expect(screen.getByText('Feb 4, 2022, 1:48 PM')).toBeInTheDocument();
  });

  it('should return -- if string is empty', async () => {
    await renderComponent(`{{ '' | formatDate }}`, {
      imports: [ SharedModule ],
    });

    expect(screen.getByText('--')).toBeInTheDocument();
  });
  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return non-object input as is', () => {
    expect(pipe.transform(null)).toBe(null);
    expect(pipe.transform(undefined)).toBe(undefined);
    expect(pipe.transform(123)).toBe(123);
    expect(pipe.transform('test')).toBe('test');
    expect(pipe.transform(true)).toBe(true);
  });

  it('should flatten a simple nested object', () => {
    const input = { a: { b: { c: 1 } } };
    const result = pipe.transform(input);
    expect(result).toEqual({ 'a.b.c': 1 });
  });

  it('should handle objects with arrays', () => {
    const input = { a: [1, 2, 3] };
    const result = pipe.transform(input);
    expect(result).toEqual({
      a: [
        { 'a[0]': 1 },
        { 'a[1]': 2 },
        { 'a[2]': 3 }
      ]
    });
  });

  it('should preserve "children" and "parents" properties', () => {
    const input = {
      name: 'node',
      children: [{ id: 1 }],
      parents: [{ id: 0 }],
      details: { type: 'leaf' }
    };
    const result = pipe.transform(input);
    expect(result.children).toEqual([{ id: 1 }]);
    expect(result.parents).toEqual([{ id: 0 }]);
    expect(result['details.type']).toEqual('leaf');
  });

  it('should skip undefined properties', () => {
    const input = { a: undefined, b: 2 };
    const result = pipe.transform(input);
    expect(result).toEqual({ 'b': 2 });
  });

  it('should flatten mixed content correctly', () => {
    const input = {
      a: 1,
      b: {
        c: 'text',
        d: [true, false],
        e: { f: null }
      }
    };
    const result = pipe.transform(input);
    expect(result).toEqual({
      'a': 1,
      'b.c': 'text',
      'b.d': [
        { 'b.d[0]': true },
        { 'b.d[1]': false }
      ],
      'b.e.f': null
    });
  });
});
