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

import { By } from '@angular/platform-browser';
import { fireEvent, screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { ScrollWithShadowComponent } from './scroll-with-shadow.component';

describe('ScrollWithShadowComponent', () => {
  const renderScrollWithShadow = async ({ withScroll }: { withScroll: boolean }) => {
    const result = await renderComponent(
      `
      <div style="width: 200px;" data-testid="overflow-element">
        <app-scroll-with-shadow>
          <div style="width: ${withScroll ? '300px' : '100px'}" data-testid="scrollable-element">Test</div>
        </app-scroll-with-shadow>
      </div>
    `,
      {
        declarations: [ScrollWithShadowComponent],
      },
    );

    const scrollWithShadowDebugElement = result.fixture.debugElement.query(By.directive(ScrollWithShadowComponent));
    await scrollWithShadowDebugElement.componentInstance.whenMarkedAsReady;
    result.fixture.detectChanges();

    return result;
  };

  it('should have no scroll shadow if scroll unavailable', async () => {
    await renderScrollWithShadow({ withScroll: false });

    expect(screen.getByTestId('scroll-container')).not.toHaveClass('scroll-container__left-scroll');
    expect(screen.getByTestId('scroll-container')).not.toHaveClass('scroll-container__right-scroll');
  });

  it('should have right scroll if scroll available', async () => {
    await renderScrollWithShadow({ withScroll: true });

    expect(screen.getByTestId('scroll-container')).not.toHaveClass('scroll-container__left-scroll');
    expect(screen.getByTestId('scroll-container')).toHaveClass('scroll-container__right-scroll');
  });

  it('should have left scroll and right shadow if scrolled and there is space on both sides', async () => {
    await renderScrollWithShadow({ withScroll: true });

    screen.getByTestId('scroll-container').scrollLeft = 50;
    fireEvent.scroll(screen.getByTestId('scroll-container'));

    expect(screen.getByTestId('scroll-container')).toHaveClass('scroll-container__left-scroll');
    expect(screen.getByTestId('scroll-container')).toHaveClass('scroll-container__right-scroll');
  });

  it('should have only left scroll shadow if scrolled till the end', async () => {
    await renderScrollWithShadow({ withScroll: true });

    screen.getByTestId('scroll-container').scrollLeft = 100;
    fireEvent.scroll(screen.getByTestId('scroll-container'));

    expect(screen.getByTestId('scroll-container')).toHaveClass('scroll-container__left-scroll');
    expect(screen.getByTestId('scroll-container')).not.toHaveClass('scroll-container__right-scroll');
  });

  it('should remove scroll shadow if resized to no scroll', async () => {
    const { fixture } = await renderScrollWithShadow({ withScroll: true });
    screen.getByTestId('scrollable-element').style.width = '100px';
    fireEvent.scroll(screen.getByTestId('scroll-container'));
    fixture.detectChanges(true);

    await waitFor(() =>
      expect(screen.getByTestId('scroll-container')).not.toHaveClass('scroll-container__right-scroll'),
    );

    expect(screen.getByTestId('scroll-container')).not.toHaveClass('scroll-container__left-scroll');
  });
});
