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

import { MatCardModule } from '@angular/material/card';
import { InfoListComponent } from '@shared/components/info-list/info-list.component';
import { SharedModule } from '@shared/shared.module';
import { screen } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';

describe('InfoList', () => {
  it('should render info list', async () => {
    await renderComponent(`<app-info-list [list]="{ test_key: 'test_value' }" title='Test title'></app-info-list>`, {
      declarations: [InfoListComponent],
      imports: [MatCardModule, SharedModule],
    });

    const cardTitleElement = screen.getByText('Test title');
    expect(cardTitleElement).toBeInTheDocument();

    const cardKeyElement = screen.getByText('partDetail.test_key');
    expect(cardKeyElement).toBeInTheDocument();
    expect(cardKeyElement).toHaveClass('info-list--key');

    const cardValueElement = screen.getByText('test_value');
    expect(cardValueElement).toBeInTheDocument();
    expect(cardValueElement).toHaveClass('info-list--value');
  });
});
