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

import { By } from '@angular/platform-browser';
import { PartsModule } from '@page/parts/parts.module';
import { SharedModule } from '@shared/shared.module';
import { screen, waitFor } from '@testing-library/angular';
import { server } from '@tests/mock-test-server';
import { renderComponent } from '@tests/test-render.utils';
import { DashboardModule } from '../dashboard.module';
import { DashboardComponent } from './dashboard.component';

describe('Dashboard', () => {
  beforeAll(() => server.start());
  afterEach(() => server.resetHandlers());
  afterAll(() => server.stop());

  const renderDashboard = ({ roles = [] } = {}) =>
    renderComponent(DashboardComponent, {
      imports: [DashboardModule, SharedModule, PartsModule],
      translations: ['page.dashboard'],
      roles,
    });

  it('should render header', async () => {
    await renderDashboard();

    expect(screen.getByText('Dashboard')).toBeInTheDocument();
  });

  it('should render total of parts', async () => {
    await renderDashboard();

    expect(await screen.findByText('3')).toBeInTheDocument();

    expect(screen.getByText('Total of parts')).toHaveAttribute(
      'id',
      screen.getByText('3').getAttribute('aria-describedby'),
    );
  });

  it('should render supervisor section when supervisor user', async () => {
    await renderDashboard({
      roles: ['supervisor'],
    });

    expect(await screen.findByText('Total of other parts')).toBeInTheDocument();
  });

  it('should render supervisor section when admin user', async () => {
    await renderDashboard({
      roles: ['admin'],
    });

    expect(await screen.findByText('Total of other parts')).toBeInTheDocument();
  });

  it('should render map', async () => {
    const { fixture } = await renderDashboard();
    expect(await screen.findByText('Number of parts per country')).toBeInTheDocument();

    await waitFor(() => expect(fixture.debugElement.query(By.css('.dashboard--map')).componentInstance).toBeDefined());
  });

  describe('investigations', () => {
    it('should render investigation component', async () => {
      await renderDashboard({ roles: ['wip'] });

      expect(await screen.findByText('Quality Investigations')).toBeInTheDocument();
    });

    it('should render count for investigations', async () => {
      await renderDashboard({ roles: ['wip'] });

      expect(await screen.findByText('20')).toBeInTheDocument();

      expect(screen.getByText('Total investigations')).toHaveAttribute(
        'id',
        screen.getByText('20').getAttribute('aria-describedby'),
      );
    });
  });
});
