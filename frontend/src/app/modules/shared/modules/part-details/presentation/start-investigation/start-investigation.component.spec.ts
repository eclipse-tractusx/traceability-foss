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
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { PartsModule } from '@page/parts/parts.module';
import { PartsAssembler } from '@shared/assembler/parts.assembler';
import { StaticIdService } from '@shared/service/staticId.service';
import { fireEvent, screen, waitFor } from '@testing-library/angular';
import { getTableCheckbox, renderComponent } from '@tests/test-render.utils';
import {
  MOCK_part_1,
  MOCK_part_2,
} from '../../../../../../mocks/services/parts-mock/partsAsBuilt/partsAsBuilt.test.model';
import { StartInvestigationComponent } from './start-investigation.component';

describe('StartInvestigationComponent', () => {
  const part = { data: PartsAssembler.assemblePart(MOCK_part_1, MainAspectType.AS_BUILT) };
  const firstChild = PartsAssembler.assemblePart(MOCK_part_2, MainAspectType.AS_BUILT);

  const renderStartInvestigation = async () => {
    const { fixture } = await renderComponent(StartInvestigationComponent, {
      declarations: [ StartInvestigationComponent ],
      imports: [ PartsModule, LayoutModule ],
      providers: [ StaticIdService ],
    });

    fixture.componentInstance.part = part;
    fixture.autoDetectChanges();
    fixture.detectChanges();
    return fixture;
  };

  it('should render component', async () => {
    await renderStartInvestigation();

    expect(await screen.findByText('partDetail.investigation.headline')).toBeInTheDocument();
  });

  it('should render request investigation on selection', async () => {
    await renderStartInvestigation();
    fireEvent.click(await getTableCheckbox(screen, 0));
  });

  it('should sort table data', async () => {
    const fixture = await renderStartInvestigation();
    const spy = spyOn((fixture.componentInstance as any).childPartsState, 'update').and.callThrough();
    const nameHeader = await waitFor(() => screen.getByText('table.column.name'));

    fireEvent.click(nameHeader);
    expect(spy).toHaveBeenCalledWith({ data: [ firstChild ] });

    fireEvent.click(nameHeader);
    expect(spy).toHaveBeenCalledWith({ data: [ firstChild ] });
  });
});
