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

import { SidenavComponent } from '@shared/components/sidenav/sidenav.component';
import { SharedModule } from '@shared/shared.module';
import { screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';

describe('SidenavComponent', () => {
  it('should render sidenav', async () => {
    await renderComponent(`<app-sidenav></app-sidenav>`, {
      declarations: [SidenavComponent],
      imports: [SharedModule],
    });

    const sidenavElement = screen.getByTestId('sidenav--test-id');
    expect(sidenavElement).toBeInTheDocument();
  });

  it('should render closed sidenav', async () => {
    await renderComponent(`<app-sidenav [isOpen]='false'></app-sidenav>`, {
      declarations: [SidenavComponent],
      imports: [SharedModule],
    });

    const sidenavElement = screen.getByTestId('sidenav--test-id');
    expect(sidenavElement).toBeInTheDocument();
    await waitFor(() => expect(sidenavElement).not.toHaveClass('sidenav--container__open'));
  });

  it('should render open sidenav', async () => {
    await renderComponent(`<app-sidenav [isOpen]='true'></app-sidenav>`, {
      declarations: [SidenavComponent],
      imports: [SharedModule],
    });

    const sidenavElement = screen.getByTestId('sidenav--test-id');
    expect(sidenavElement).toBeInTheDocument();
    await waitFor(() => expect(sidenavElement).toHaveClass('sidenav--container__open'));
  });

  it('should render sidenav with content', async () => {
    const text = 'Some text';
    await renderComponent(`<app-sidenav><p>${text}</p></app-sidenav>`, {
      declarations: [SidenavComponent],
      imports: [SharedModule],
    });

    const textElement = screen.getByText(text);
    expect(textElement).toBeInTheDocument();
  });
});
