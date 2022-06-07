/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { MatCardModule } from '@angular/material/card';
import { CardListComponent, SharedModule } from '@shared';
import { screen } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';

describe('CardList', () => {
  it('should render card list', async () => {
    await renderComponent(`<app-card-list [list]="{ test_key: 'test_value' }" title='Test title'></app-card-list>`, {
      declarations: [CardListComponent],
      imports: [MatCardModule, SharedModule],
    });

    const cardTitleElement = screen.getByText('Test title');
    expect(cardTitleElement).toBeInTheDocument();

    const cardKeyElement = screen.getByText('partDetail.test_key');
    expect(cardKeyElement).toBeInTheDocument();
    expect(cardKeyElement).toHaveClass('card-list--key');

    const cardValueElement = screen.getByText('test_value');
    expect(cardValueElement).toBeInTheDocument();
    expect(cardValueElement).toHaveClass('card-list--value');
  });
});
