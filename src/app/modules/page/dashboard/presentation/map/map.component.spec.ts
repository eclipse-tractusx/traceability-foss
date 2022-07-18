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
import { renderComponent } from '@tests/test-render.utils';

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

describe('Map', () => {
  const renderMap = mapData =>
    renderComponent(MapComponent, {
      declarations: [MapComponent],
      imports: [DashboardModule],
      translations: ['page.dashboard'],
      componentProperties: {
        mapData,
      },
    });

  it('should render map', async () => {
    const { fixture } = await renderMap([]);
    expect(fixture.componentInstance.map).toBeDefined();
  });

  it('should handle zoom', async () => {
    const { fixture } = await renderMap([]);

    (fixture.componentInstance.map.on as jest.Mock).mock.lastCall[1]({
      target: {
        getZoom: () => 3,
      },
    });

    expect(fixture.componentInstance.map.resize).toHaveBeenCalled();
    expect(fixture.componentInstance.map.addLayer).toHaveBeenCalled();
  });
});
