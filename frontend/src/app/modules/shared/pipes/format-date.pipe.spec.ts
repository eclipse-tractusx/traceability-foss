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
import { SharedModule } from '..';

describe('FormatDatePipe', () => {
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
});
