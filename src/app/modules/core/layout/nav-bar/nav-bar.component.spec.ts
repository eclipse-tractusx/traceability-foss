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
import { NavBarComponent } from '@layout/nav-bar/nav-bar.component';
import { screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';

describe('Navbar', () => {
  it('should render ', async () => {
    await renderComponent(NavBarComponent, {
      imports: [LayoutModule],
    });

    expect(await waitFor(() => screen.getByText('Mock User'))).toBeInTheDocument();
  });

  it('should open details', async () => {
    await renderComponent(NavBarComponent, {
      imports: [LayoutModule],
    });

    const userAvatarButton = await waitFor(() => screen.getByTestId('user-menu'));
    userAvatarButton.click();
    expect((await waitFor(() => screen.getAllByText('Mock User'))).length).toEqual(2);
    expect(await waitFor(() => screen.getByText('user'))).toBeInTheDocument();
    expect(await waitFor(() => screen.getByText('mock.user@foss.de'))).toBeInTheDocument();
    expect(await waitFor(() => screen.getByText('Sign out'))).toBeInTheDocument();
  });
});
