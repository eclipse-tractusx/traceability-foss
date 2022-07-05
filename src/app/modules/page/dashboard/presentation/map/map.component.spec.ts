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

import { DashboardModule } from '@page/dashboard/dashboard.module';
import { MapComponent } from '@page/dashboard/presentation/map/map.component';
import { screen } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';

jest.mock('mapbox-gl/dist/mapbox-gl', () => ({
  GeolocateControl: jest.fn(),
  Map: jest.fn(() => ({
    addControl: jest.fn(),
    on: jest.fn(),
    remove: jest.fn(),
  })),
  NavigationControl: jest.fn(),
}));

describe('Map', () => {
  const renderMap = mapData =>
    renderComponent(`<app-map [mapData]='${mapData}'></app-map>`, {
      declarations: [MapComponent],
      imports: [DashboardModule],
      translations: ['page.dashboard'],
    });

  it('should render map', async () => {
    await renderMap([]);

    // ToDo: Will ask anton on how to setup tests for this use case.
    expect(screen.getByText('aspect_ratio')).toBeInTheDocument();
  });
});
