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

import { InvestigationsModule } from '@page/investigations/investigations.module';
import { InvestigationsComponent } from '@page/investigations/presentation/investigations.component';
import { InvestigationsService } from '@shared/service/investigations.service';
import { screen, waitFor } from '@testing-library/angular';
import { server } from '@tests/mock-test-server';
import { renderComponent } from '@tests/test-render.utils';

describe('InvestigationsComponent', () => {
  const renderInvestigations = async (id?: string) => {
    return await renderComponent(InvestigationsComponent, {
      imports: [InvestigationsModule],
      providers: [InvestigationsService],
      translations: ['page.investigation'],
    });
  };

  it('should render', async () => {
    await renderInvestigations();
    expect(await waitFor(() => screen.getByText('Quality Investigations'))).toBeInTheDocument();
  });

  it('should call detail page with correct ID', async () => {
    const { fixture } = await renderInvestigations();
    const menuButtons = await waitFor(() => screen.getAllByTestId('table-menu-button'));
    menuButtons[0].click();

    const spy = spyOn((fixture.componentInstance as any).router, 'navigate');
    const viewDetailsButton = await waitFor(() => screen.getByTestId('table-menu-button--actions.viewDetails'));
    viewDetailsButton.click();

    expect(spy).toHaveBeenCalledWith(['/context/investigations/id-1']);
  });

  it('should call change pagination of received notification', async () => {
    await renderInvestigations();
    const nextButton = await waitFor(() => screen.getByLabelText('Next page', { selector: 'button' }));
    nextButton.click();

    expect(await waitFor(() => screen.getByText('Investigation No 6'))).toBeInTheDocument();
    expect(await waitFor(() => screen.getByText('Investigation No 10'))).toBeInTheDocument();
  });
});
