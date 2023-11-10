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

import { LayoutModule } from '@layout/layout.module';
import { OtherPartsModule } from '@page/other-parts/other-parts.module';
import { SharedModule } from '@shared/shared.module';
import { fireEvent, screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { sleepForTests } from '../../../../../test';
import { RequestInvestigationComponent } from '@shared/components/request-notification/request-investigation.component';
import { RequestAlertComponent } from '@shared/components/request-notification/request-alert.component';
import { RequestContext } from '@shared/components/request-notification/request-notification.base';
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { cloneDeep } from 'lodash-es';
import { RequestComponentData } from './request.componenet.model';
import { Severity } from '@shared/model/severity.model';
import { By } from '@angular/platform-browser';

describe('requestInvestigationComponent', () => {
  let deselectPartMock: jasmine.Spy<jasmine.Func>;
  let clearSelectedMock: jasmine.Spy<jasmine.Func>;
  let submittedMock: jasmine.Spy<jasmine.Func> = jasmine.createSpy();

  const currentSelectedItems = [{ name: 'part_1' }, { name: 'part_2' }, { name: 'part_3' }];

  const requestDataDefault = {
    showHeadline: true,
    selectedItems: currentSelectedItems,
  } as RequestComponentData;

  let requestData = requestDataDefault;

  // TODO fix tests when create investigation is ready
  // const renderRequestInvestigationComponent = (component = `<app-request-investigation (submitted)='submittedMock($event)'></app-request-investigation>` as any) => {
  //   return renderComponent(component, {
  //     declarations: [RequestInvestigationComponent],
  //     providers: [
  //       { provide: MAT_DIALOG_DATA, useValue: requestData },
  //       {
  //         provide: MatDialogRef, useValue: {
  //           close: jasmine.createSpy(),
  //         }
  //       },
  //     ],
  //     imports: [SharedModule, LayoutModule, OtherPartsModule],
  //     translations: ['page.otherParts', 'partDetail'],
  //     componentProperties: {
  //       deselectPartMock,
  //       clearSelectedMock,
  //       submittedMock,
  //       currentSelectedItems,
  //     },
  //   });
  // };

  // by default we use component as a string, but when need to use spyOn we pass componend class
  const renderRequestAlertComponent = (component = `<app-request-alert (submitted)='submittedMock($event)'></app-request-alert>` as any) => {
    return renderComponent(component, {
      declarations: [RequestAlertComponent],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: requestData },
        {
          provide: MatDialogRef, useValue: {
            close: jasmine.createSpy(),
          }
        },
      ],
      imports: [SharedModule, LayoutModule, OtherPartsModule],
      translations: ['page.otherParts', 'partDetail'],
      componentProperties: {
        deselectPartMock,
        clearSelectedMock,
        submittedMock,
        currentSelectedItems,
      },
    });
  };

  // describe('Request Investigation', () => {
  //   beforeEach(() => {
  //     requestData = cloneDeep(requestDataDefault)
  //     submittedMock = jasmine.createSpy();
  //   });

  //   it('should render', async () => {
  //     await renderRequestInvestigationComponent();
  //     await shouldRender('requestInvestigations');
  //   });

  //   it('should render parts in chips', async () => {
  //     await renderRequestInvestigationComponent();
  //     await shouldRenderPartsInChips();
  //   });

  //   it('should render textarea', async () => {
  //     await renderRequestInvestigationComponent();
  //     await shouldRenderTextarea();
  //   });

  //   it('should render buttons', async () => {
  //     await renderRequestInvestigationComponent();
  //     await shouldRenderButtons();
  //   });

  //   it('should submit parts', async () => {
  //     await renderRequestInvestigationComponent();
  //     await shouldSubmitParts('requestInvestigations');
  //   });
  // });

  describe('Request Alert', () => {
    beforeEach(() => {
      requestData = cloneDeep(requestDataDefault);
      submittedMock = jasmine.createSpy();
    });

    it('should render', async () => {
      await renderRequestAlertComponent();
      await shouldRender('requestAlert');
    });

    it('should render parts in chips', async () => {
      await renderRequestAlertComponent();
      await shouldRenderPartsInChips();
    });

    it('should render textarea', async () => {
      await renderRequestAlertComponent();
      await shouldRenderTextarea();
    });

    it('should render buttons', async () => {
      await renderRequestAlertComponent();
      await shouldRenderButtons();
    });

    it('should submit parts', async () => {
      const { fixture } = await renderRequestAlertComponent();
      await shouldSubmitParts('requestAlert', fixture, true);
    });
  });

  const shouldRender = async (context: RequestContext) => {
    const headline = await waitFor(() => screen.getByText(context + '.headline'), { timeout: 2000 });
    expect(headline).toBeInTheDocument();
  };

  const shouldRenderPartsInChips = async () => {
    const part_1 = await waitFor(() => screen.getByText('part_1'));
    const part_2 = await screen.getByText('part_2');
    const part_3 = await screen.getByText('part_3');

    expect(part_1).toBeInTheDocument();
    expect(part_2).toBeInTheDocument();
    expect(part_3).toBeInTheDocument();
  };

  const shouldRenderTextarea = async () => {
    const textAreaElement = await waitFor(() => screen.getByText('requestNotification.textAreaLabel'));

    expect(textAreaElement).toBeInTheDocument();
  };

  const shouldRenderButtons = async () => {
    const cancelElement = await waitFor(() => screen.getByText('requestNotification.cancel'));
    const submitElement = await screen.getByText('requestNotification.submit');

    expect(cancelElement).toBeInTheDocument();
    expect(submitElement).toBeInTheDocument();
  };

  const shouldSubmitParts = async (context: RequestContext, fixture, shouldFillBpn = false) => {
    const testText = 'This is for a testing purpose.';
    const textArea = (await waitFor(() => screen.getByTestId('BaseInputElement-1'))) as HTMLTextAreaElement;
    fireEvent.input(textArea, { target: { value: testText } });

    const severitySelect = fixture.debugElement.query(By.css('mat-select')).nativeElement;
    severitySelect.click();  // Open the dropdown
    fixture.detectChanges(); // Update the view

    const option = fixture.debugElement.query(By.css('mat-option')).nativeElement;
    option.click();  // Select the option
    fixture.detectChanges(); // Update the view

    if (shouldFillBpn) {
      const bpnInput = (await waitFor(() => screen.getByTestId('BaseInputElement-3'))) as HTMLTextAreaElement;
      fireEvent.input(bpnInput, { target: { value: 'BPNA0123TEST0123' } });
    }

    const submit = await waitFor(() => screen.getByText('requestNotification.submit'));
    expect(submit).toBeInTheDocument();
    expect(textArea.value).toEqual(testText);
    fireEvent.click(submit);
    await sleepForTests(2000);
    expect(textArea.value).toEqual('');
    expect(submittedMock).toHaveBeenCalledTimes(1);
  };
});
