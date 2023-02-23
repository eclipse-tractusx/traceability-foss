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

import { LayoutModule } from '@layout/layout.module';
import { SidenavComponent } from '@layout/sidenav/sidenav.component';
import { SidenavService } from '@layout/sidenav/sidenav.service';
import { OtherPartsModule } from '@page/other-parts/other-parts.module';
import { PartsComponent } from '@page/parts/presentation/parts.component';
import { SharedModule } from '@shared/shared.module';
import { screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { PartsModule } from '../parts.module';

describe('Parts', () => {
  const renderParts = () => {
    return renderComponent(`<app-sidenav></app-sidenav><app-parts></app-parts>`, {
      declarations: [SidenavComponent, PartsComponent],
      imports: [PartsModule, SharedModule, LayoutModule, OtherPartsModule],
      providers: [{ provide: SidenavService }],
      roles: ['admin'],
    });
  };

  it('should render part header', async () => {
    await renderParts();

    expect(screen.getByText('pageParts.title')).toBeInTheDocument();
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
});
