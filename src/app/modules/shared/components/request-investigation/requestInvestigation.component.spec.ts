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

import { OtherPartsModule } from '@page/otherParts/otherParts.module';
import { Part } from '@page/parts/model/parts.model';
import { RequestInvestigationComponent } from '@shared/components/request-investigation/requestInvestigation.component';
import { InvestigationsService } from '@shared/service/investigations.service';
import { SharedModule } from '@shared/shared.module';
import { screen, waitFor } from '@testing-library/angular';
import { server } from '@tests/mock-server';
import { renderComponent } from '@tests/test-render.utils';

describe('requestInvestigationComponent', () => {
  beforeAll(() => server.listen());
  afterEach(() => server.resetHandlers());
  afterAll(() => server.close());

  const deselectPartMock = jest.fn();
  const clearSelectedMock = jest.fn();
  const sidenavIsClosingMock = jest.fn();
  const currentSelectedItems = [{ name: 'part_1' }, { name: 'part_2' }, { name: 'part_3' }];

  const renderRequestInvestigationComponent = () =>
    renderComponent(
      `<app-request-investigation
  (deselectPart)='deselectPartMock($event)'
  (clearSelected)='clearSelectedMock($event)'
  (sidenavIsClosing)='sidenavIsClosingMock($event)'
  [isOpen]='true'
  [selectedItems]='currentSelectedItems'
></app-request-investigation>`,
      {
        declarations: [RequestInvestigationComponent],
        imports: [SharedModule],
        translations: ['page.otherParts'],
        componentProperties: {
          deselectPartMock,
          clearSelectedMock,
          sidenavIsClosingMock,
          currentSelectedItems,
        },
      },
    );

  it('should render', async () => {
    await renderRequestInvestigationComponent();
    const headline = await screen.getByText('Request quality investigation');
    expect(headline).toBeInTheDocument();
  });

  it('should render parts in chips', async () => {
    await renderRequestInvestigationComponent();
    const part_1 = await screen.getByText('part_1');
    const part_2 = await screen.getByText('part_2');
    const part_3 = await screen.getByText('part_3');

    expect(part_1).toBeInTheDocument();
    expect(part_2).toBeInTheDocument();
    expect(part_3).toBeInTheDocument();
  });

  it('should render textarea', async () => {
    await renderRequestInvestigationComponent();
    const textAreaElement = await screen.getByText('Description');

    expect(textAreaElement).toBeInTheDocument();
  });

  it('should render buttons', async () => {
    await renderRequestInvestigationComponent();
    const cancelElement = await screen.getByText('CANCEL');
    const submitElement = await screen.getByText('ADD TO QUEUE');

    expect(cancelElement).toBeInTheDocument();
    expect(submitElement).toBeInTheDocument();
  });

  it('should submit parts', async () => {
    const { fixture } = await renderComponent(RequestInvestigationComponent, {
      imports: [OtherPartsModule],
      providers: [InvestigationsService],
      translations: ['page.otherParts'],
    });
    const { componentInstance } = fixture;

    const spy = jest.spyOn(componentInstance.clearSelected, 'emit');
    const spy_2 = jest.spyOn((componentInstance as any).investigationsService, 'postInvestigation');

    const testText = 'This is for a testing purpose.';

    componentInstance.selectedItems = [{ id: 'id_1', name: 'part_1' } as Part];
    componentInstance.textAreaControl.setValue(testText);
    componentInstance.submitInvestigation();

    await waitFor(() => expect(spy).toHaveBeenCalledTimes(1));
    expect(spy_2).toHaveBeenCalledWith([{ id: 'id_1', name: 'part_1' } as Part], testText);
  });
});
