/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
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

import { fireEvent, screen, within } from '@testing-library/angular';
import { server } from '@tests/mock-test-server';
import { renderComponent } from '@tests/test-render.utils';
import { InvestigationsModule } from '../investigations.module';
import { InvestigationsComponent } from './investigations.component';

describe('InvestigationsInboxComponent', () => {
  beforeAll(() => server.listen());
  afterEach(() => server.resetHandlers());
  afterAll(() => server.close());

  const renderInvestigationsInbox = () =>
    renderComponent(InvestigationsComponent, {
      imports: [InvestigationsModule],
      translations: ['page.investigations'],
    });

  it('should render received investigations', async () => {
    await renderInvestigationsInbox();
    expect(await screen.findByText('Investigation No 1')).toBeInTheDocument();
  });

  it('should render received investigations with date and status', async () => {
    await renderInvestigationsInbox();

    const descriptionEl = await screen.findByText('Investigation No 1');
    const row = descriptionEl.closest('tr');

    expect(within(row).getByText('5/1/2022')).toBeInTheDocument();
    expect(within(row).getByText('Received')).toBeInTheDocument();
  });

  it('should be able to change investigations page', async () => {
    await renderInvestigationsInbox();

    await screen.findByText('Investigation No 1');
    fireEvent.click(screen.getByLabelText('Next page'));

    expect(await screen.findByText('Investigation No 6')).toBeInTheDocument();
    expect(screen.queryByText('Investigation No 1')).not.toBeInTheDocument();
  });

  it('should render queued & requested investigations', async () => {
    await renderInvestigationsInbox();
    fireEvent.click(screen.getByText('Queued & Requested'));
    expect(await screen.findByText('Investigation No 1')).toBeInTheDocument();
  });
});
