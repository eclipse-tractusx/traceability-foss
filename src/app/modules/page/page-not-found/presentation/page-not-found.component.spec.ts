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

import { PageNotFoundModule } from '@page/page-not-found/page-not-found.module';
import { PageNotFoundComponent } from '@page/page-not-found/presentation/page-not-found.component';
import { screen } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';

describe('PagNotFound', () => {
  const renderMap = () =>
    renderComponent(PageNotFoundComponent, {
      imports: [PageNotFoundModule],
    });

  it('should render page not found component', async () => {
    await renderMap();

    expect(screen.getByText('404')).toBeInTheDocument();
    expect(screen.getByText('The page you requested could not be found')).toBeInTheDocument();
  });
});
