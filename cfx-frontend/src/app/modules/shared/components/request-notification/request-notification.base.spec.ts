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
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RequestInvestigationComponent } from '@shared/components/request-notification/request-investigation.component';
import { RequestAlertComponent } from '@shared/components/request-notification/request-alert.component';
import { RequestContext } from '@shared/components/request-notification/request-notification.base';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { cloneDeep } from 'lodash-es';
import { RequestComponentData } from './request.component.model';
import { By } from '@angular/platform-browser';
import { Part } from '@page/parts/model/parts.model';

describe('requestInvestigationComponent', () => {
  let deselectPartMock: jasmine.Spy<jasmine.Func>;
  let clearSelectedMock: jasmine.Spy<jasmine.Func>;
  let submittedMock: jasmine.Spy<jasmine.Func> = jasmine.createSpy();

  const currentSelectedItems = [{ nameAtManufacturer: 'part_1' }, { nameAtManufacturer: 'part_2' }, { nameAtManufacturer: 'part_3' }];

  const requestDataDefault = {
    showHeadline: true,
    selectedItems: currentSelectedItems,
  } as RequestComponentData;

  let requestData = requestDataDefault;

  const renderRequestInvestigationComponent = (component = `<app-request-investigation
    [selectedItems]="currentSelectedItems"
    (submitted)='submittedMock($event)'>
  </app-request-investigation>` as any) => {
    return renderComponent(component, {
      declarations: [RequestInvestigationComponent],
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

  const renderRequestInvestigationComponentObject = () => {
    return renderComponent(RequestInvestigationComponent, {
      imports: [SharedModule, LayoutModule, OtherPartsModule],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: requestData },
        {
          provide: MatDialogRef, useValue: {
            close: jasmine.createSpy(),
          }
        },
      ],
    });
  };

  // by default we use component as a string, but when need to use spyOn we pass component class
  const renderRequestAlertComponent = (component = `<app-request-alert
    [selectedItems]="currentSelectedItems"
    (submitted)='submittedMock($event)'>
  </app-request-alert>` as any) => {
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

  describe('Request Investigation', () => {
    beforeEach(() => {
      requestData = cloneDeep(requestDataDefault)
      submittedMock = jasmine.createSpy();
    });

    it('should render', async () => {
      await renderRequestInvestigationComponent();
    });

    // TODO: fix test
    // it('should render parts in chips', async () => {
    //   await renderRequestInvestigationComponent();
    //   await shouldRenderPartsInChips();
    // });

    it('should render textarea', async () => {
      await renderRequestInvestigationComponent();
      await shouldRenderTextarea();
    });

    it('should render buttons', async () => {
      await renderRequestInvestigationComponent();
      await shouldRenderButtons();
    });

    it('should submit parts', async () => {
      const { fixture } = await renderRequestInvestigationComponent();
      await shouldSubmitParts(RequestContext.REQUEST_INVESTIGATION, fixture);
    });

    it('should submit', async () => {
      const { fixture } = await renderRequestInvestigationComponentObject();
      const { componentInstance } = fixture;
      const requestInvestigationComponentSpy = spyOn(componentInstance, 'submit');
      componentInstance.submit();
      expect(requestInvestigationComponentSpy).toHaveBeenCalled();
    });

    it('should not prepare or submit form if invalid', async () => {
      const { fixture } = await renderRequestInvestigationComponentObject();
      const { componentInstance } = fixture;

      componentInstance.submit(); // Call the submit method

      // Expectations for an invalid form
      expect(componentInstance.formGroup.invalid).toBeTrue(); // Ensure the form is invalid
    });

    it('should emit onBackClicked when onBack is called', async () => {
      const { fixture } = await renderRequestInvestigationComponentObject();
      const { componentInstance } = fixture;

      spyOn(componentInstance.onBackClicked, 'emit');

      componentInstance.onBack();
      expect(componentInstance.onBackClicked.emit).toHaveBeenCalled();
    });
  });

  describe('Request Alert', () => {
    beforeEach(() => {
      requestData = cloneDeep(requestDataDefault);
      submittedMock = jasmine.createSpy();
    });

    it('should render', async () => {
      await renderRequestAlertComponent();
    });

    // TODO: fix test
    // it('should render parts in chips', async () => {
    //   await renderRequestAlertComponent();
    //   await shouldRenderPartsInChips();
    // });

    it('should render textarea', async () => {
      await renderRequestAlertComponent();
      await shouldRenderTextarea();
    });

    it('should render buttons', async () => {
      await renderRequestAlertComponent();
      await shouldRenderButtons();
    });

//     it('should submit parts', async () => {
//       const { fixture } = await renderRequestAlertComponent();
//       await shouldSubmitParts(RequestContext.REQUEST_ALERT, fixture, false);
//     });

   });

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

    const textAreaElementTitle = await waitFor(() => screen.getByText('requestNotification.textAreaLabelTitle'));
    expect(textAreaElementTitle).toBeInTheDocument();
  };

  const shouldRenderButtons = async () => {
    const cancelElement = await waitFor(() => screen.getByText('Back'));
    const submitElement = await screen.getByText('Submit');

    expect(cancelElement).toBeInTheDocument();
    expect(submitElement).toBeInTheDocument();
  };

  const shouldSubmitParts = async (context: RequestContext, fixture, shouldFillTargetDate = true) => {

    const titleText = '';
    const titleTextArea = (await waitFor(() => screen.getByTestId('BaseInputElement-1'))) as HTMLTextAreaElement;
    fireEvent.input(titleTextArea, { target: { value: titleText } });

    const testText = 'This is for a testing purpose.';
    const textArea = (await waitFor(() => screen.getByTestId('BaseInputElement-2'))) as HTMLTextAreaElement;
    fireEvent.input(textArea, { target: { value: testText } });

    const bpn = 'BPNA0123TEST0123';
    const bpnInput = (await waitFor(() => screen.getByTestId('BaseInputElement-5'))) as HTMLTextAreaElement;
    fireEvent.input(bpnInput, { target: { value: bpn } });

    const severitySelect = fixture.debugElement.query(By.css('mat-select')).nativeElement;
    severitySelect.click();  // Open the dropdown
    fixture.detectChanges(); // Update the view

    const option = fixture.debugElement.query(By.css('mat-option')).nativeElement;
    option.click();  // Select the option
    fixture.detectChanges(); // Update the view
    const tomorrow = new Date(); // Get the current date
    tomorrow.setDate(tomorrow.getDate() + 1); // Set it to tomorrow
    const tomorrowString = tomorrow.toISOString().split('T')[0];
    const targetDate: HTMLInputElement = null

    if (shouldFillTargetDate) {
      const matFormField = (await waitFor(() => screen.getByTestId('multi-select-autocomplete--date-search-form'))) as HTMLInputElement;
      const targetDate = matFormField.querySelector('input');
      fireEvent.input(targetDate, { target: { value: tomorrowString } }); // Set the date
    }

    const submit = await waitFor(() => screen.getByText('Submit'));
    expect(submit).toBeInTheDocument();
    expect(textArea.value).toEqual(testText);
    expect(bpnInput.value).toEqual(bpn);
    fireEvent.click(submit);
    await sleepForTests(2000);
    expect(titleTextArea.value).toEqual('');
    expect(submittedMock).toHaveBeenCalledTimes(1);
  };
});
