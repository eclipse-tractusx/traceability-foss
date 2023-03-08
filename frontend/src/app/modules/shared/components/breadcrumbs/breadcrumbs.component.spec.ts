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

import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { LayoutFacade } from '@shared/abstraction/layout-facade';
import { BreadcrumbsComponent } from '@shared/components/breadcrumbs/breadcrumbs.component';
import { SharedModule } from '@shared/shared.module';
import { screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { of } from 'rxjs';

describe('BreadcrumbsComponent', () => {
  const createRoutRoot = (path: string, firstChild?: ActivatedRoute, breadcrumb?: string) => {
    breadcrumb = breadcrumb || path;
    return {
      routeConfig: { data: { breadcrumb }, path },
      snapshot: { params: { id: path + '-1' } },
      firstChild,
    } as unknown as ActivatedRoute;
  };

  const renderBreadcrumbsComponent = async root => {
    const routerEvents = new NavigationEnd(1, 'test', '');

    return await renderComponent(BreadcrumbsComponent, {
      declarations: [BreadcrumbsComponent],
      imports: [SharedModule],
      providers: [
        LayoutFacade,
        { provide: Router, useValue: { root: '', events: of(routerEvents) } },
        {
          provide: ActivatedRoute,
          useValue: {
            root,
          },
        },
      ],
    });
  };

  it('should render', async () => {
    const root = createRoutRoot('home', createRoutRoot('test', createRoutRoot('path')));
    await renderBreadcrumbsComponent(root);
    expect(await waitFor(() => screen.getByText('routing.home'))).toBeInTheDocument();
    expect(await waitFor(() => screen.getByText('routing.test'))).toBeInTheDocument();
    expect(await waitFor(() => screen.getByText('routing.path'))).toBeInTheDocument();
  });

  it('should not render id', async () => {
    const root = createRoutRoot('home', createRoutRoot('test', createRoutRoot(':random_id')));
    await renderBreadcrumbsComponent(root);
    expect(await waitFor(() => screen.getByText('routing.home'))).toBeInTheDocument();
    expect(await waitFor(() => screen.getByText('routing.test'))).toBeInTheDocument();
    expect(await screen.queryByText('random_id')).not.toBeInTheDocument();
  });
});
