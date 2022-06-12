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

import { PartsModule } from '@page/parts/parts.module';
import { PartsComponent } from '@page/parts/presentation/parts.component';
import { screen } from '@testing-library/angular';
import { server } from '@tests/mock-server';
import { renderComponent } from '@tests/test-render.utils';

describe('Parts', () => {
  beforeAll(() => server.listen());
  afterEach(() => server.resetHandlers());
  afterAll(() => server.close());

  const renderParts = () =>
    renderComponent(PartsComponent, {
      imports: [PartsModule],
      translations: ['page.parts'],
    });

  it('should render part header', async () => {
    await renderParts();

    expect(screen.getByText('My Parts')).toBeInTheDocument();
  });

  it('should render part table', async () => {
    await renderParts();

    expect(await screen.findByTestId('table-component--test-id')).toBeInTheDocument();
  });

  it('should render table and display correct amount of rows', async () => {
    await renderParts();

    const tableElement = await screen.findByTestId('table-component--test-id');
    expect(tableElement).toBeInTheDocument();
    expect(tableElement.children[1].childElementCount).toEqual(5);
  });

  it('should render parts with closed sidenav', async () => {
    await renderParts();

    const sideNavElement = await screen.findByTestId('part-detail--sidenav');
    expect(sideNavElement).toBeInTheDocument();
    expect(sideNavElement).not.toHaveClass('part-detail--open');
  });

  it('should render parts with closed sidenav', async () => {
    await renderParts();

    const sideNavElement = await screen.findByTestId('part-detail--sidenav');
    expect(sideNavElement).toBeInTheDocument();
    expect(sideNavElement).not.toHaveClass('part-detail--open');
  });
});
