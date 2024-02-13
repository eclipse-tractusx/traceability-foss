/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
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
import { screen } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { SharedModule } from '@shared/shared.module';

describe('AutoFormatPipe', () => {
  it('should format CalendarDateModel as short date', async () => {
    await renderComponent(`{{ rawValue | autoFormat }}`, {
      imports: [SharedModule],
      componentProperties: {
        rawValue: new CalendarDateModel('2022-07-15T10:00:00.000Z'),
      },
    });

    expect(screen.getByText('15/07/2022')).toBeInTheDocument();
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
