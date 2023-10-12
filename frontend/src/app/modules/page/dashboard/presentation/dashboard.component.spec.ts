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

import { PartsModule } from '@page/parts/parts.module';
import { SharedModule } from '@shared/shared.module';
import { screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { DashboardModule } from '../dashboard.module';
import { DashboardComponent } from './dashboard.component';

describe('Dashboard', () => {
  const renderDashboard = ({ roles = [] } = {}) =>
    renderComponent(DashboardComponent, {
      imports: [DashboardModule, SharedModule, PartsModule],
      translations: ['page.dashboard'],
      roles,
    });

  it('should render total of parts', async () => {
    await renderDashboard();

    expect(await waitFor(() => screen.getByText('3'))).toBeInTheDocument();

    expect(screen.getByText('pageDashboard.totalOfMyParts.label')).toHaveAttribute(
      'id',
      screen.getByText('3').getAttribute('aria-describedby'),
    );
  });

  it('should render supervisor section when supervisor user', async () => {
    await renderDashboard({
      roles: ['supervisor'],
    });

    expect(await screen.findByText('pageDashboard.totalOfOtherParts.label')).toBeInTheDocument();
  });

  it('should render supervisor section when admin user', async () => {
    await renderDashboard({
      roles: ['admin'],
    });

    expect(await screen.findByText('pageDashboard.totalOfMyParts.label')).toBeInTheDocument();
  });

  describe('investigations', () => {
    it('should render count for investigations', async () => {
      await renderDashboard();

      expect(await waitFor(() => screen.getByText('20'))).toBeInTheDocument();

      expect(screen.getByText('pageDashboard.totalInvestigations.label')).toHaveAttribute(
        'id',
        screen.getByText('20').getAttribute('aria-describedby'),
      );
    });
  });

  describe('alerts', () => {
    it('should render count for alerts', async () => {
      await renderDashboard();

      expect(await waitFor(() => screen.getByText('101'))).toBeInTheDocument();

      expect(screen.getByText('pageDashboard.totalAlerts.label')).toHaveAttribute(
        'id',
        screen.getByText('101').getAttribute('aria-describedby'),
      );
    });
  });
});
