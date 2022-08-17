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

import { CalendarDateModel } from '@core/model/calendar-date.model';
import { PartsState } from '@page/parts/core/parts.state';
import { Part } from '@page/parts/model/parts.model';
import { State } from '@shared/model/state';
import { View } from '@shared/model/view.model';
import { PartDetailsFacade } from '@shared/modules/part-details/core/partDetails.facade';
import { PartDetailsModule } from '@shared/modules/part-details/partDetails.module';
import { screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { Observable } from 'rxjs';
import { PartDetailComponent } from './part-detail.component';

export const PartDetailsFacadeFactory = (initialPart: View<Part>) => {
  return class PartDetailsFacadeMock {
    public readonly _selectedPart: State<View<Part>> = new State<View<Part>>(initialPart);

    get selectedPart$(): Observable<View<Part>> {
      return this._selectedPart.observable;
    }

    set selectedPart(part: Part) {
      this._selectedPart.update({ data: part });
    }
  };
};

describe('PartDetailComponent', () => {
  it('should render side nav', async () => {
    await renderComponent(PartDetailComponent, {
      imports: [PartDetailsModule],
      providers: [{ provide: PartsState }],
    });

    const sideNavElement = await screen.findByTestId('sidenav--test-id');
    expect(sideNavElement).toBeInTheDocument();
  });

  it('should render an open sidenav with part details', async () => {
    const testPart = {
      name: 'Test_01',
      productionDate: new CalendarDateModel('1997-05-30T12:34:12Z'),
      customerPartId: '333',
    } as Part;
    await renderComponent(PartDetailComponent, {
      imports: [PartDetailsModule],
      providers: [
        {
          provide: PartDetailsFacade,
          useClass: PartDetailsFacadeFactory({ data: testPart }),
        },
      ],
    });

    const sideNavElement = await screen.findByTestId('sidenav--test-id');
    const nameElement = await screen.findByText(testPart.name);
    const productionDateElement = await screen.findByText('5/30/1997');
    const partNumberElement = await screen.findByText(testPart.customerPartId);

    expect(sideNavElement).toBeInTheDocument();
    await waitFor(() => expect(sideNavElement).toHaveClass('sidenav--container__open'));

    expect(nameElement).toBeInTheDocument();
    expect(productionDateElement).toBeInTheDocument();
    expect(partNumberElement).toBeInTheDocument();
  });

  it('should render error messages if data failed loading', async () => {
    const error = { message: 'Error message' } as Error;
    await renderComponent(PartDetailComponent, {
      imports: [PartDetailsModule],
      providers: [
        {
          provide: PartDetailsFacade,
          useClass: PartDetailsFacadeFactory({ error }),
        },
      ],
    });

    const errorElements = await screen.findAllByText(error.message);
    expect(errorElements.length).toBe(4);
    errorElements.forEach(errorElement => expect(errorElement).toBeInTheDocument());
  });
});
