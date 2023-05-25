/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

import { AlertsModule } from '@page/alerts/alerts.module';
import { fireEvent, screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';

import { AlertsComponent } from './alerts.component';


describe('AlertsComponent', () => {
  const renderAlerts = async () => {
    return await renderComponent(AlertsComponent, {
      imports: [AlertsModule],
      providers: [],
      translations: ['page.alert'],
    });
  };


  it('should call change pagination of received notification', async () => {
    await renderAlerts();
    fireEvent.click(await waitFor(() => screen.getByLabelText('pagination.nextPageLabel', { selector: 'button' })));

    expect(await waitFor(() => screen.getByText('Alert No 18'))).toBeInTheDocument();
    expect(await waitFor(() => screen.getByText('Alert No 5'))).toBeInTheDocument();
  });
});
