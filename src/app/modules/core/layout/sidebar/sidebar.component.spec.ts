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

import { NavigationEnd, Router } from '@angular/router';
import { Role } from '@core/user/role.model';

import { LayoutModule } from '@layout/layout.module';
import { SidebarComponent } from '@layout/sidebar/sidebar.component';
import { getDashboardRoute } from '@page/dashboard/dashboard-route';
import { screen } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { of } from 'rxjs';

describe('Sidebar', () => {
  const renderSidebar = async (url: string, role: Role = 'user') => {
    const routerEvents = new NavigationEnd(1, url, null);
    await renderComponent(SidebarComponent, {
      imports: [LayoutModule],
      providers: [{ provide: Router, useValue: { events: of(routerEvents) } }],
      roles: [role],
    });
  };

  it('should render', async () => {
    await renderSidebar(getDashboardRoute().link);
    const dashboardButton = screen.getByText('Dashboard');

    expect(dashboardButton).toBeInTheDocument();
  });

  it('should mark dashboard as active', async () => {
    await renderSidebar(getDashboardRoute().link);
    const dashboardButton = screen.getByText('Dashboard');

    expect(dashboardButton).toBeInTheDocument();
    expect(dashboardButton).toHaveClass('item-active');
  });

  it('should not mark about as active', async () => {
    await renderSidebar(getDashboardRoute().link);
    const dashboardButton = screen.getByText('About');

    expect(dashboardButton).toBeInTheDocument();
    expect(dashboardButton).not.toHaveClass('item-active');
  });

  it('should hide admin panel if not admin role', async () => {
    await renderSidebar(getDashboardRoute().link);
    const adminButton = screen.queryByText('Administration');

    expect(adminButton).not.toBeInTheDocument();
  });

  it('should show admin panel if user has admin role', async () => {
    await renderSidebar(getDashboardRoute().link, 'admin');
    const adminButton = screen.queryByText('Administration');

    expect(adminButton).toBeInTheDocument();
  });
});
