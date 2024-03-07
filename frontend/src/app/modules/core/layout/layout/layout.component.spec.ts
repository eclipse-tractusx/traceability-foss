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
import { By } from '@angular/platform-browser';
import { LayoutModule } from '@layout/layout.module';
import { renderComponent } from '@tests/test-render.utils';
import { LayoutComponent } from './layout.component';

describe('LayoutComponent', () => {

  it('should render', async () => {
    const { fixture } = await renderComponent(LayoutComponent, {
      imports: [ LayoutModule ],
      providers: [ LayoutComponent ],
    });
    expect(fixture).toBeTruthy();
  });

  it('should render toast and header in same vertical position', async () => {
    const { fixture } = await renderComponent(LayoutComponent, {
      imports: [ LayoutModule ],
      providers: [ LayoutComponent ],
    });
    let header = fixture.debugElement.query(By.css('.layout-content__box-modal'));
    expect(header).not.toBeNull();
    let toast = fixture.debugElement.query(By.css('.layout-toast-component'));
    let headerDistanceToTop = header.nativeElement.getBoundingClientRect().y + 'px';
    let toastDistanceToTop = toast.nativeElement.style.top;
    expect(headerDistanceToTop).toEqual(toastDistanceToTop);
  });

});
