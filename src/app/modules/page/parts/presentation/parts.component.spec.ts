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

import { Part } from '@page/parts/model/parts.model';
import { PartsComponent } from '@page/parts/presentation/parts.component';
import { PartDetailsModule } from '@shared/modules/part-details/partDetails.module';
import { SharedModule } from '@shared/shared.module';
import { screen, waitFor } from '@testing-library/angular';
import { server } from '@tests/mock-server';
import { renderComponent } from '@tests/test-render.utils';
import { MOCK_part_1, MOCK_part_2 } from '../../../../mocks/services/parts-mock/parts.model';
import { PartsModule } from '../parts.module';

describe('Parts', () => {
  beforeAll(() => server.listen());
  afterEach(() => server.resetHandlers());
  afterAll(() => server.close());

  const renderParts = () => {
    return renderComponent(PartsComponent, {
      imports: [PartsModule, SharedModule],
      translations: ['page.parts'],
    });
  };

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

    const sideNavElement = await screen.findByTestId('sidenav--test-id');
    expect(sideNavElement).toBeInTheDocument();
    expect(sideNavElement).not.toHaveClass('sidenav--container__open');
  });

  it('should open an investigation and remove duplicate children', async () => {
    const { fixture } = await renderParts();
    const children = [MOCK_part_1.id, MOCK_part_2.id, MOCK_part_1.id];
    fixture.componentInstance.startInvestigation(new MouseEvent('click'), { children } as Part);

    const sideNavElement = await screen.findByTestId('sidenav--test-id');
    expect(sideNavElement).toBeInTheDocument();
    await waitFor(() => expect(sideNavElement).toHaveClass('sidenav--container__open'));
    expect(screen.getByText('Request quality investigation')).toBeInTheDocument();
    await waitFor(() => expect(screen.getByText(MOCK_part_1.id)).toBeInTheDocument());
    expect(screen.getAllByText(MOCK_part_1.id).length).toBe(1);
  });
});
