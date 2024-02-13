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

import { ErrorPageComponent } from './error-page.component';
import { renderComponent } from '@tests/test-render.utils';
import { SharedModule } from '@shared/shared.module';
import { screen } from '@testing-library/angular';
import { ErrorPageModule } from '@page/error-page/error-page.module';
import { ActivatedRoute } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { ErrorPageType } from '@page/error-page/model/error-page.model';

describe('ErrorPageComponent', () => {
  const renderErrorPageComponent = ({ errorPage = {}, roles = [] } = {}) =>
    renderComponent(ErrorPageComponent, {
      imports: [ ErrorPageModule, SharedModule ],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: new BehaviorSubject({
              errorPage,
            }),
          } as unknown as ActivatedRoute,
        },
      ],
      roles,
    });

  it('should render generic error page - with user role', async () => {
    const { fixture } = await renderErrorPageComponent({ errorPage: {}, roles: [ 'user' ] });

    await fixture.whenStable();
    fixture.detectChanges();

    expect(screen.getByText('errorPage.title')).toBeInTheDocument();
    expect(screen.getByText('errorPage.message')).toBeInTheDocument();
    expect(screen.getByText('actions.homepage')).toBeInTheDocument();
  });

  it('should render generic error page - no user role', async () => {
    const { fixture } = await renderErrorPageComponent({ errorPage: {}, roles: [] });

    await fixture.whenStable();
    fixture.detectChanges();

    expect(screen.getByText('errorPage.title')).toBeInTheDocument();
    expect(screen.getByText('errorPage.message')).toBeInTheDocument();
    expect(screen.getByText('layout.nav.signOut')).toBeInTheDocument();
  });

  it('should render page-not-found page - with user role', async () => {
    const { fixture } = await renderErrorPageComponent({
      errorPage: { type: ErrorPageType.pageNotFound },
      roles: [ 'user' ],
    });

    await fixture.whenStable();
    fixture.detectChanges();

    expect(screen.getByText('pageNotFound.title')).toBeInTheDocument();
    expect(screen.getByText('pageNotFound.message')).toBeInTheDocument();
    expect(screen.getByText('actions.homepage')).toBeInTheDocument();
  });

  it('should render page-not-found page - no user role', async () => {
    const { fixture } = await renderErrorPageComponent({
      errorPage: { type: ErrorPageType.pageNotFound },
      roles: [],
    });

    await fixture.whenStable();
    fixture.detectChanges();

    expect(screen.getByText('pageNotFound.title')).toBeInTheDocument();
    expect(screen.getByText('pageNotFound.message')).toBeInTheDocument();
    expect(screen.getByText('layout.nav.signOut')).toBeInTheDocument();
  });

  it('should render no-permissions page - with user role', async () => {
    const { fixture } = await renderErrorPageComponent({
      errorPage: { type: ErrorPageType.noPermissions },
      roles: [ 'user' ],
    });

    await fixture.whenStable();
    fixture.detectChanges();

    expect(screen.getByText('noPermissions.title')).toBeInTheDocument();
    expect(screen.getByText('noPermissions.message')).toBeInTheDocument();
    expect(screen.getByText('actions.homepage')).toBeInTheDocument();
  });

  it('should render no-permissions page - no user role', async () => {
    const { fixture } = await renderErrorPageComponent({
      errorPage: { type: ErrorPageType.noPermissions },
      roles: [],
    });

    await fixture.whenStable();
    fixture.detectChanges();

    expect(screen.getByText('noPermissions.title')).toBeInTheDocument();
    expect(screen.getByText('noPermissions.message')).toBeInTheDocument();
    expect(screen.getByText('layout.nav.signOut')).toBeInTheDocument();
  });
});
