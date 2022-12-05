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
import { OtherPartsModule } from '@page/other-parts/other-parts.module';
import { Part } from '@page/parts/model/parts.model';
import { RequestInvestigationComponent } from '@shared/components/request-investigation/request-investigation.component';
import { InvestigationsService } from '@shared/service/investigations.service';
import { SharedModule } from '@shared/shared.module';
import { screen, waitFor } from '@testing-library/angular';
import { server } from '@tests/mock-test-server';
import { renderComponent } from '@tests/test-render.utils';
import { of } from 'rxjs';

describe('requestInvestigationComponent', () => {
  beforeAll(() => server.start({ onUnhandledRequest: 'bypass' }));
  afterEach(() => server.resetHandlers());
  afterAll(() => server.stop());

  const deselectPartMock = jasmine.createSpy();
  const clearSelectedMock = jasmine.createSpy();
  const sidenavIsClosingMock = jasmine.createSpy();
  const submittedMock = jasmine.createSpy();
  const currentSelectedItems = [{ name: 'part_1' }, { name: 'part_2' }, { name: 'part_3' }];

  const renderRequestInvestigationComponent = () =>
    renderComponent(
      `<app-request-investigation
  (deselectPart)='deselectPartMock($event)'
  (clearSelected)='clearSelectedMock($event)'
  (submitted)='submittedMock($event)'
  [selectedItems]='currentSelectedItems'
></app-request-investigation>`,
      {
        declarations: [RequestInvestigationComponent],
        imports: [SharedModule, LayoutModule, OtherPartsModule],
        translations: ['page.otherParts', 'partDetail'],
        componentProperties: {
          deselectPartMock,
          clearSelectedMock,
          submittedMock,
          currentSelectedItems,
        },
      },
    );

  it('should render', async () => {
    await renderRequestInvestigationComponent();
    const headline = await waitFor(() => screen.getByText('Request quality investigation'), { timeout: 2000 });
    expect(headline).toBeInTheDocument();
  });

  it('should render parts in chips', async () => {
    await renderRequestInvestigationComponent();
    const part_1 = await waitFor(() => screen.getByText('part_1'));
    const part_2 = await screen.getByText('part_2');
    const part_3 = await screen.getByText('part_3');

    expect(part_1).toBeInTheDocument();
    expect(part_2).toBeInTheDocument();
    expect(part_3).toBeInTheDocument();
  });

  it('should render textarea', async () => {
    await renderRequestInvestigationComponent();
    const textAreaElement = await waitFor(() => screen.getByText('Description'));

    expect(textAreaElement).toBeInTheDocument();
  });

  it('should render buttons', async () => {
    await renderRequestInvestigationComponent();
    const cancelElement = await waitFor(() => screen.getByText('CANCEL'));
    const submitElement = await screen.getByText('ADD TO QUEUE');

    expect(cancelElement).toBeInTheDocument();
    expect(submitElement).toBeInTheDocument();
  });

  it('should submit parts', async () => {
    const { fixture } = await renderComponent(RequestInvestigationComponent, {
      imports: [OtherPartsModule],
      providers: [InvestigationsService],
      translations: ['page.otherParts', 'partDetail'],
    });
    const { componentInstance } = fixture;

    // TODO: Rework!
    const spy = spyOn(componentInstance.clearSelected, 'emit');
    const spy_2 = spyOn((componentInstance as any).investigationsService, 'postInvestigation').and.returnValue(of([]));
    const spy_3 = spyOn((componentInstance as any).otherPartsFacade, 'setActiveInvestigationForParts').and.returnValue(
      of([]),
    );

    const testText = 'This is for a testing purpose.';

    componentInstance.selectedItems = [{ id: 'id_1', name: 'part_1' } as Part];
    (componentInstance as any).textAreaControl.setValue(testText);
    componentInstance.submitInvestigation();

    await waitFor(() => expect(spy).toHaveBeenCalledTimes(1));
    expect(spy_2).toHaveBeenCalledWith(['id_1'], testText);
    await waitFor(() => expect(spy_3).toHaveBeenCalledTimes(1));
  });
});
