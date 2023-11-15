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

import { ActivatedRoute } from '@angular/router';
import { InvestigationDetailComponent } from '@page/investigations/detail/investigation-detail.component';
import { InvestigationsModule } from '@page/investigations/investigations.module';
import { InvestigationsService } from '@shared/service/investigations.service';
import { fireEvent, screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { of } from 'rxjs';
import { MOCK_part_1 } from '../../../../mocks/services/parts-mock/partsAsPlanned/partsAsPlanned.test.model';

describe('InvestigationDetailComponent', () => {

  const renderInvestigationDetail = async (id?: string) => {
    return await renderComponent(InvestigationDetailComponent, {
      imports: [InvestigationsModule],
      providers: [
        InvestigationsService,
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: () => id || 'id-2',
              },
            },
            queryParams: of({ pageNumber: 0, tabIndex: 0 })
          },
        },
      ],
    });
  };

  it('should render specific text and additional table for received investigation', async () => {
    await renderInvestigationDetail();
    await waitFor(() => expect(screen.getByText('pageInvestigation.subHeadline.affectedParts')).toBeInTheDocument());
    await waitFor(() => expect(screen.getByText('pageInvestigation.subHeadline.supplierParts')).toBeInTheDocument());
  });

  it('should render specific text for queued or requested investigations', async () => {
    await renderInvestigationDetail('id-1');
    await waitFor(() => expect(screen.getByText('pageInvestigation.subHeadline.supplierParts')).toBeInTheDocument());
  });

  it('should render copy data to clipboard', async () => {
    await renderInvestigationDetail('id-1');
    await waitFor(() => expect(screen.getByText('pageInvestigation.subHeadline.supplierParts')).toBeInTheDocument());

    const spy = spyOn(navigator.clipboard, 'writeText').and.returnValue(new Promise(null));
    fireEvent.click(await waitFor(() => screen.getByTestId('copy-button--' + MOCK_part_1.id)));

    expect(spy).toHaveBeenCalledWith("NO-341449848714937445621543");
  });
});
