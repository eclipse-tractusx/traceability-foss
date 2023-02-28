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
import { fireEvent, screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { UserMenuComponent } from '@layout/header/user-navigation/user-menu.component';

describe('UserMenuComponent', () => {
  const renderUserMenuComponent = () => renderComponent(UserMenuComponent, { imports: [LayoutModule] });
  it('should render user menu', async () => {
    await renderUserMenuComponent();

    expect(await waitFor(() => screen.getByTestId('user-menu'))).toBeInTheDocument();
  });

  it('should open details', async () => {
    await renderUserMenuComponent();
    fireEvent.click(await waitFor(() => screen.getByTestId('user-menu')));

    expect((await waitFor(() => screen.getAllByText('OEM A'))).length).toEqual(1);
    expect(await waitFor(() => screen.getByText('user'))).toBeInTheDocument();
    expect(await waitFor(() => screen.getByText('layout.nav.signOut'))).toBeInTheDocument();

    expect(await screen.findByText('EN')).toBeInTheDocument();
    expect(await screen.findByText('EN')).toHaveClassName('selectedText');
    expect(await screen.findByText('DE')).toBeInTheDocument();
  });
});
