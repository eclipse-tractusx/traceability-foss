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

import { By } from '@angular/platform-browser';
import { PartsModule } from '@page/parts/parts.module';
import { screen, waitFor } from '@testing-library/angular';
import { server } from '@tests/mock-server';
import { renderComponent } from '@tests/test-render.utils';

import { DashboardComponent } from './dashboard.component';
import { DashboardModule } from '../dashboard.module';

jest.mock('mapbox-gl/dist/mapbox-gl', () => ({
  GeolocateControl: jest.fn(),
  Map: jest.fn(() => ({
    addControl: jest.fn(),
    on: jest.fn(),
    remove: jest.fn(),
    resize: jest.fn(),
    getLayer: jest.fn(),
    addLayer: jest.fn(),
  })),
  NavigationControl: jest.fn(),
}));

describe('Dashboard', () => {
  beforeAll(() => server.listen());
  afterEach(() => server.resetHandlers());
  afterAll(() => server.close());

  const renderDashboard = ({ roles = [] } = {}) =>
    renderComponent(DashboardComponent, {
      imports: [DashboardModule, PartsModule],
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

    expect(await screen.findByText('Total of parts in department')).toBeInTheDocument();
  });

  it('should render supervisor section when admin user', async () => {
    await renderDashboard({
      roles: ['admin'],
    });

    expect(await screen.findByText('Total of parts in department')).toBeInTheDocument();
  });

  it('should render map', async () => {
    const { fixture } = await renderDashboard();
    expect(await screen.findByText('Number of parts per country')).toBeInTheDocument();

    await waitFor(() => expect(fixture.debugElement.query(By.css('.dashboard--map')).componentInstance).toBeDefined());
  });
});
