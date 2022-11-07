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

import { discardPeriodicTasks, fakeAsync, flush, tick } from '@angular/core/testing';
import { LayoutModule } from '@layout/layout.module';
import { OtherPartsModule } from '@page/other-parts/other-parts.module';
import { PartsModule } from '@page/parts/parts.module';
import { PartsAssembler } from '@shared/assembler/parts.assembler';
import { PartDetailsModule } from '@shared/modules/part-details/partDetails.module';
import { StaticIdService } from '@shared/service/staticId.service';
import { screen, waitFor } from '@testing-library/angular';
import { server } from '@tests/mock-test-server';
import { renderComponent } from '@tests/test-render.utils';
import { MOCK_part_1, MOCK_part_2, MOCK_part_3 } from '../../../../../../mocks/services/parts-mock/parts.test.model';
import { StartInvestigationComponent } from './start-investigation.component';

describe('StartInvestigationComponent', () => {
  beforeAll(() => server.start({ quiet: true, onUnhandledRequest: 'bypass' }));
  afterEach(() => server.resetHandlers());
  afterAll(() => server.stop());

  const isOpen = jasmine.createSpy();
  const part = { data: PartsAssembler.assemblePart(MOCK_part_1) };
  const firstChild = PartsAssembler.assemblePart(MOCK_part_2);
  const secondChild = PartsAssembler.assemblePart(MOCK_part_3);

  const renderStartInvestigation = async () => {
    return await renderComponent(
      `<app-start-investigation [part]='part' (submitted)='isOpen(false)'></app-start-investigation>`,
      {
        declarations: [StartInvestigationComponent],
        imports: [PartDetailsModule, PartsModule, OtherPartsModule, LayoutModule],
        providers: [StaticIdService],
        translations: ['page.parts', 'partDetail'],
        componentProperties: {
          isOpen,
          part,
        },
      },
    );
  };

  it('should render component', async () => {
    await renderStartInvestigation();

    const childTableHeadline = await screen.findByText('Request quality investigation for child parts');
    expect(childTableHeadline).toBeInTheDocument();
    expect(await screen.findByText('No parts selected.')).toBeInTheDocument();
  });

  it('should render request investigation on selection', async () => {
    const fixture = await renderStartInvestigation();

    const listOfCheckboxes = await waitFor(() => screen.getAllByTestId('select-one--test-id'));
    (listOfCheckboxes[0].firstChild as HTMLElement).click();
    fixture.detectChanges();

    expect(
      await waitFor(() => screen.getByText('It may take a while to load the name of your selected parts.')),
    ).toBeInTheDocument();
  });

  it('should render selected items and remove them again', async () => {
    const fixture = await renderStartInvestigation();

    const listOfCheckboxes = await waitFor(() => screen.getAllByTestId('select-one--test-id'));
    (listOfCheckboxes[0].firstChild as HTMLElement).click();
    (listOfCheckboxes[1].firstChild as HTMLElement).click();
    fixture.detectChanges();

    const matChipElement = await waitFor(() => screen.getByTestId('mat-chip--' + firstChild.name));
    expect(matChipElement).toBeInTheDocument();
    (matChipElement.lastElementChild as HTMLElement).click();

    const historyElement = await waitFor(() => screen.getByTestId('mat-chip-history--' + firstChild.name));
    expect(historyElement).toBeInTheDocument();
    historyElement.click();

    const restoredElement = await waitFor(() => screen.getByTestId('mat-chip--' + firstChild.name));
    expect(restoredElement).toBeInTheDocument();
  });

  it('should sort table data', fakeAsync(async () => {
    const fixture = await renderStartInvestigation();

    const nameHeader = await waitFor(() => screen.getByText('Name'));
    nameHeader.click();
    fixture.detectChanges();

    tick(50);

    const tableData = await waitFor(() => screen.getAllByTestId('table-component--cell-data'));
    expect(tableData[0].innerHTML).toContain(firstChild.name);
    expect(tableData[2].innerHTML).toContain(secondChild.name);

    nameHeader.click();
    fixture.detectChanges();

    tick(50);

    expect(tableData[0].innerHTML).toContain(firstChild.name);
    expect(tableData[2].innerHTML).toContain(secondChild.name);
    discardPeriodicTasks();
    flush();
  }));
});
