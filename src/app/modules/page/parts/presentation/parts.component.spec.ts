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

import { LayoutModule } from '@layout/layout.module';
import { SidenavComponent } from '@layout/sidenav/sidenav.component';
import { SidenavService } from '@layout/sidenav/sidenav.service';
import { OtherPartsModule } from '@page/other-parts/other-parts.module';
import { PartsComponent } from '@page/parts/presentation/parts.component';
import { SharedModule } from '@shared/shared.module';
import { screen, waitFor } from '@testing-library/angular';
import { server } from '@tests/mock-test-server';
import { renderComponent } from '@tests/test-render.utils';
import { MOCK_part_1 } from '../../../../mocks/services/parts-mock/parts.test.model';
import { PartsModule } from '../parts.module';

describe('Parts', () => {
  beforeAll(() => server.start());
  afterEach(() => server.resetHandlers());
  afterAll(() => server.stop());

  const renderParts = () => {
    return renderComponent(`<app-sidenav></app-sidenav><app-parts></app-parts>`, {
      declarations: [SidenavComponent, PartsComponent],
      imports: [PartsModule, SharedModule, LayoutModule, OtherPartsModule],
      translations: ['page.parts'],
      providers: [{ provide: SidenavService }],
      roles: ['wip', 'admin'],
    });
  };

  it('should render part header', async () => {
    await renderParts();

    expect(screen.getByText('My Parts')).toBeInTheDocument();
  });

  it('should render part table', async () => {
    await renderParts();

    expect(await waitFor(() => screen.getByTestId('table-component--test-id'))).toBeInTheDocument();
  });

  it('should render table and display correct amount of rows', async () => {
    await renderParts();

    const tableElement = await waitFor(() => screen.getByTestId('table-component--test-id'));
    expect(tableElement).toBeInTheDocument();
    expect(tableElement.children[1].childElementCount).toEqual(5);
  });

  it('should render parts with closed sidenav', async () => {
    await renderParts();

    const sideNavElement = await waitFor(() => screen.getByTestId('sidenav--test-id'));
    expect(sideNavElement).toBeInTheDocument();
    expect(sideNavElement).not.toHaveClass('sidenav--container__open');
  });

  it('should open an investigation and remove duplicate children', async () => {
    await renderParts();

    const buttonElement = await waitFor(() => screen.getAllByTestId('parts--investigation'));
    buttonElement[0].click();

    const sideNavElement = await waitFor(() => screen.getByTestId('sidenav--test-id'));
    expect(sideNavElement).toBeInTheDocument();
    await waitFor(() => expect(screen.getByText('Request quality investigation')).toBeInTheDocument());
    await waitFor(() => expect(sideNavElement).toHaveClass('sidenav--container__open'));
    await waitFor(() => expect(screen.getByText(MOCK_part_1.id)).toBeInTheDocument());
    expect(screen.getAllByText(MOCK_part_1.id).length).toBe(1);
  });
});
