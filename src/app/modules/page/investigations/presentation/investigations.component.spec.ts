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

import { ActivatedRoute } from '@angular/router';
import { InvestigationDetailComponent } from '@page/investigations/detail/investigation-detail.component';
import { InvestigationsModule } from '@page/investigations/investigations.module';
import { InvestigationsComponent } from '@page/investigations/presentation/investigations.component';
import { InvestigationsService } from '@shared/service/investigations.service';
import { screen } from '@testing-library/angular';
import { server } from '@tests/mock-test-server';
import { renderComponent } from '@tests/test-render.utils';
import { of } from 'rxjs';

describe('InvestigationsComponent', () => {
  beforeAll(() => server.start({ quiet: true, onUnhandledRequest: 'bypass' }));
  afterEach(() => server.resetHandlers());
  afterAll(() => server.stop());

  const renderInvestigations = async (id?: string) => {
    return await renderComponent(InvestigationsComponent, {
      imports: [InvestigationsModule],
      providers: [
        InvestigationsService,
        {
          provide: ActivatedRoute,
          useValue: {
            queryParams: of([{ tabIndex: '0' }]),
          },
        },
      ],
      translations: ['page.investigation'],
    });
  };

  it('should render', async () => {
    await renderInvestigations();
    expect(false).toBeTruthy();
  });
});
