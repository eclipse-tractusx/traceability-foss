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
import { SidenavWrapperComponent } from '@shared/components/sidenav/sidenav-wrapper.component';
import { screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';

describe('SidenavComponent', () => {
  it('should render sidenav', async () => {
    await renderComponent(`<app-sidenav></app-sidenav><app-sidenav-wrapper></app-sidenav-wrapper>`, {
      declarations: [SidenavComponent, SidenavWrapperComponent],
      imports: [LayoutModule],
      providers: [{ provide: SidenavService }],
    });

    await waitFor(() => expect(screen.getByTestId('sidenav--test-id')).toBeInTheDocument());
  });

  it('should render closed sidenav', async () => {
    await renderComponent(`<app-sidenav></app-sidenav><app-sidenav-wrapper [isOpen]='false'></app-sidenav-wrapper>`, {
      declarations: [SidenavComponent, SidenavWrapperComponent],
      imports: [LayoutModule],
      providers: [{ provide: SidenavService }],
    });

    const sidenavElement = await waitFor(() => screen.getByTestId('sidenav--test-id'));
    expect(sidenavElement).toBeInTheDocument();

    await waitFor(() => expect(sidenavElement).not.toHaveClass('sidenav--container__open'));
  });

  it('should render open sidenav with content', async () => {
    const text = 'Some text';
    await renderComponent(
      `<app-sidenav></app-sidenav><app-sidenav-wrapper [isOpen]='true'><p>${text}</p></app-sidenav-wrapper>`,
      {
        declarations: [SidenavComponent, SidenavWrapperComponent],
        imports: [LayoutModule],
      },
    );

    await waitFor(() => expect(screen.getByText(text)).toBeInTheDocument());
  });
});
