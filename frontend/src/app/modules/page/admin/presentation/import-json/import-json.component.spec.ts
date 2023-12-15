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



import { AdminModule } from '@page/admin/admin.module';
import { ImportJsonComponent } from '@page/admin/presentation/import-json/import-json.component';
import { fireEvent, screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';



describe('ImportJsonComponent', () => {
  const renderImportJsonComponent = () => renderComponent(ImportJsonComponent, {imports: [ AdminModule]});

  it('should create', async () => {
    await renderImportJsonComponent();
    expect(await waitFor( () => screen.getByText('pageAdmin.importJson.title'))).toBeInTheDocument();
    });
})

