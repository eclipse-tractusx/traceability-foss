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

import { screen } from '@testing-library/angular';
import { CalendarDateModel } from '@core/model/calendar-date.model';
import { renderComponent } from '@tests/test-render.utils';

import { SharedModule } from '..';

describe('AutoFormatPipe', () => {
  it('should format CalendarDateModel as short date', async () => {
    await renderComponent(`{{ rawValue | autoFormat }}`, {
      imports: [SharedModule],
      componentProperties: {
        rawValue: new CalendarDateModel('2022-07-15T10:00:00.000Z'),
      },
    });

    expect(screen.getByText('7/15/2022')).toBeInTheDocument();
  });

  it('should format string as it is', async () => {
    await renderComponent(`{{ rawValue | autoFormat }}`, {
      imports: [SharedModule],
      componentProperties: {
        rawValue: 'some text',
      },
    });

    expect(screen.getByText('some text')).toBeInTheDocument();
  });

  it('should format number as a string', async () => {
    await renderComponent(`{{ rawValue | autoFormat }}`, {
      imports: [SharedModule],
      componentProperties: {
        rawValue: 100000.11234,
      },
    });

    expect(screen.getByText('100000.11234')).toBeInTheDocument();
  });

  it('should format object as a string', async () => {
    await renderComponent(`{{ rawValue | autoFormat }}`, {
      imports: [SharedModule],
      componentProperties: {
        rawValue: {},
      },
    });

    expect(screen.getByText('[object Object]')).toBeInTheDocument();
  });
});
