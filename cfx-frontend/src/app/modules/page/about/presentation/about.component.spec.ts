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
import { TestBed } from '@angular/core/testing';
import { AboutModule } from '@page/about/about.module';
import { AboutComponent } from '@page/about/presentation/about.component';
import { screen } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';


describe('About Page', () => {

  it('should open link in new tab', async () => {
    const { fixture } = await renderComponent(AboutComponent, { imports: [AboutModule] });
    let component = fixture.componentInstance;
    const url = 'https://www.example.com';

    spyOn(window, 'open');

    component.openLink(url);

    expect(window.open).toHaveBeenCalledWith(url, '_blank');
  });


  it('should render about page', async () => {
    await renderComponent(AboutComponent, { imports: [AboutModule], providers: [AboutComponent] });
    expect(screen.getByText('pageAbout.source')).toBeInTheDocument();
    const componentInstance = TestBed.inject(AboutComponent);

    expect(componentInstance.name).toBeDefined();
    expect(componentInstance.repositoryPath).toBeDefined();
    expect(componentInstance.license).toBeDefined();
    expect(componentInstance.licensePath).toBeDefined();
    expect(componentInstance.noticePath).toBeDefined();
    expect(componentInstance.sourcePath).toBeDefined();
    expect(componentInstance.commitId).toBeDefined();
  });
});
