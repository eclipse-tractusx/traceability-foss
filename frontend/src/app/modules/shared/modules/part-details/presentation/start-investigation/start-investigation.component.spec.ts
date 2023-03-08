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
import { PartsModule } from '@page/parts/parts.module';
import { PartsAssembler } from '@shared/assembler/parts.assembler';
import { PartDetailsModule } from '@shared/modules/part-details/partDetails.module';
import { StaticIdService } from '@shared/service/staticId.service';
import { fireEvent, screen, waitFor } from '@testing-library/angular';
import { getTableCheckbox, renderComponent } from '@tests/test-render.utils';
import { sleepForTests } from '../../../../../../../test';
import { MOCK_part_1, MOCK_part_2, MOCK_part_3 } from '../../../../../../mocks/services/parts-mock/parts.test.model';
import { StartInvestigationComponent } from './start-investigation.component';

describe('StartInvestigationComponent', () => {
  const part = { data: PartsAssembler.assemblePart(MOCK_part_1) };
  const firstChild = PartsAssembler.assemblePart(MOCK_part_2);
  const secondChild = PartsAssembler.assemblePart(MOCK_part_3);

  const renderStartInvestigation = async () => {
    const { fixture } = await renderComponent(StartInvestigationComponent, {
      declarations: [StartInvestigationComponent],
      imports: [PartDetailsModule, PartsModule, OtherPartsModule, LayoutModule],
      providers: [StaticIdService],
    });

    fixture.componentInstance.part = part;
    fixture.autoDetectChanges();
    fixture.detectChanges();
    return fixture;
  };

  it('should render component', async () => {
    await renderStartInvestigation();

    expect(await screen.findByText('partDetail.investigation.headline')).toBeInTheDocument();
    expect(await screen.findByText('partDetail.investigation.noSelection.header')).toBeInTheDocument();
  });

  it('should render request investigation on selection', async () => {
    await renderStartInvestigation();
    fireEvent.click(await getTableCheckbox(screen, 0));

    await sleepForTests(2000);
    expect(await waitFor(() => screen.getByText('page.requestInvestigations.partDescription'))).toBeInTheDocument();
  });

  it('should render selected items and remove them again', async () => {
    await renderStartInvestigation();

    fireEvent.click(await getTableCheckbox(screen, 0));
    fireEvent.click(await getTableCheckbox(screen, 1));

    const matChipElement = await waitFor(() => screen.getByTestId('mat-chip--' + firstChild.name));
    expect(matChipElement).toBeInTheDocument();
    fireEvent.click(matChipElement.lastElementChild.firstChild);

    const historyElement = await waitFor(() => screen.getByTestId('mat-chip-history--' + firstChild.name));
    expect(historyElement).toBeInTheDocument();
    fireEvent.click(historyElement);

    const restoredElement = await waitFor(() => screen.getByTestId('mat-chip--' + firstChild.name));
    expect(restoredElement).toBeInTheDocument();
  });

  it('should sort table data', async () => {
    const fixture = await renderStartInvestigation();
    const spy = spyOn((fixture.componentInstance as any).childPartsState, 'update').and.callThrough();
    const nameHeader = await waitFor(() => screen.getByText('table.partsColumn.name'));

    fireEvent.click(nameHeader);
    expect(spy).toHaveBeenCalledWith({ data: [firstChild, secondChild] });

    fireEvent.click(nameHeader);
    expect(spy).toHaveBeenCalledWith({ data: [secondChild, firstChild] });
  });
});
