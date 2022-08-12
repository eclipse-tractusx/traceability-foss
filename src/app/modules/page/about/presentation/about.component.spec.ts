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

import { AboutModule } from '@page/about/about.module';
import { AboutComponent } from '@page/about/presentation/about.component';
import { screen } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';

describe('About Page', () => {
  const renderMap = () =>
    renderComponent(AboutComponent, {
      imports: [AboutModule],
      translations: ['page.about'],
    });

  it('should render about page', async () => {
    await renderMap();

    expect(screen.getByText('About Catena-X Open-Source Traceability')).toBeInTheDocument();
  });
});
