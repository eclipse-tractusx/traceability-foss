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

import { AdminModule } from '@page/admin/admin.module';
import { screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { ScheduledRegistryProcessesComponent } from './scheduled-registry-processes.component';

describe('ScheduledRegistryProcessesComponent', () => {
  const renderRegistryProcessesComponent = () =>
    renderComponent(ScheduledRegistryProcessesComponent, { imports: [AdminModule] });

  it('should create', async () => {
    await renderRegistryProcessesComponent();

    expect(await screen.getByText('pageAdmin.scheduledRegistryProcesses.title')).toBeInTheDocument();
  });

  // it('should render data', async () => {
  //   await renderRegistryProcessesComponent();
  //   await waitFor(() => expect(screen.getByText('01/01/2000')).toBeInTheDocument());
  //   await waitFor(() => expect(screen.getByText('02/01/2000')).toBeInTheDocument());
  // });
});
