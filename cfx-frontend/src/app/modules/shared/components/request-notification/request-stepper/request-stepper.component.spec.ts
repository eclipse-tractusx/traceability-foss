import { ComponentFixture, TestBed, fakeAsync, flushMicrotasks, tick } from '@angular/core/testing';
import { RequestStepperComponent } from './request-stepper.component';
import { MatDialog, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ChangeDetectorRef, Pipe, PipeTransform, QueryList } from '@angular/core';
import { SupplierPartsComponent } from '@page/other-parts/presentation/supplier-parts/supplier-parts.component';
import { ModalComponent } from '@shared/modules/modal/component/modal.component';
import { of } from 'rxjs';
import { MatTabsModule } from '@angular/material/tabs';
import { InputComponent } from '@shared/components/input/input.component';
import { OtherPartsFacade } from '@page/other-parts/core/other-parts.facade';
import { PartDetailsFacade } from '@shared/modules/part-details/core/partDetails.facade';
import { CommonModule, DatePipe } from '@angular/common';
import { renderComponent } from '@tests/test-render.utils';
import { RequestContext } from '../request-notification.base';

// Mocking MatDialog for testing purposes
class MatDialogMock {
  open() {
    return {
      afterClosed: () => of(true),
    };
  }

  closeAll() {
    // Mock implementation
  }
}

// Mock OtherPartsFacade if needed
class OtherPartsFacadeMock {
  setSupplierPartsAsBuilt() { }
  unsubscribeParts() { }
}
class PartDetailsFacadeMock { }

describe('RequestStepperComponent', () => {

  let componentInstance: RequestStepperComponent;
  let fixture: ComponentFixture<RequestStepperComponent>;


  beforeEach(async () => {
    fixture = (await renderRequestStepperComponent()).fixture;
    componentInstance = fixture.componentInstance;
  });

  const requestDataDefault = {
    context: RequestContext.REQUEST_INVESTIGATION,
    tabIndex: 0,
    selectedParts: [],
    fromExternal: false
  } as any;

  let requestData = requestDataDefault;

  const renderRequestStepperComponent = () => {
    return renderComponent(RequestStepperComponent, {
      declarations: [RequestStepperComponent, InputComponent, SupplierPartsComponent],
      imports: [MatTabsModule, CommonModule],
      providers: [
        { provide: MatDialog, useClass: MatDialogMock },
        { provide: MAT_DIALOG_DATA, useValue: requestData },
        {
          provide: MatDialogRef, useValue: {
            close: jasmine.createSpy(),
          }
        },
        ChangeDetectorRef,
        // Provide the OtherPartsFacade service or a mock if needed
        { provide: OtherPartsFacade, useClass: OtherPartsFacadeMock },
        { provide: PartDetailsFacade, useClass: PartDetailsFacadeMock },
        DatePipe
      ],
    });
  };

  it('should render', async () => {
    expect(componentInstance).toBeTruthy();
  });

  it('should close dialog when fromExternal is true and onBackClicked is called', () => {
    spyOn(componentInstance.dialog, 'closeAll');
    componentInstance['fromExternal'] = true;
    componentInstance.onBackClicked();
    expect(componentInstance.dialog.closeAll).toHaveBeenCalled();
  });

  it('should set tabIndex to the provided index and when onTabChange is called', () => {
    let newIndex = 0;
    componentInstance.onTabChange(newIndex);
    expect(componentInstance.tabIndex).toBe(newIndex);

    newIndex = 1;
    componentInstance.onTabChange(newIndex);
    expect(componentInstance.tabIndex).toBe(newIndex);
  });

  it('should close dialog when fromExternal is true and closeAction is called', fakeAsync(() => {
    spyOn(componentInstance.dialog, 'closeAll');
    componentInstance['fromExternal'] = true;
    componentInstance.closeAction();

    // Use flushMicrotasks to ensure all microtasks are completed
    flushMicrotasks();

    // Use whenStable to wait for all asynchronous tasks to complete
    fixture.whenStable().then(() => {
      expect(componentInstance.dialog.closeAll).toHaveBeenCalled();
    });
  }));

  it('should open ModalComponent when closeAction is called', fakeAsync(() => {
    spyOn(componentInstance.dialog, 'open').and.callThrough();
    componentInstance.closeAction();
    tick();
    expect(componentInstance.dialog.open).toHaveBeenCalledWith(ModalComponent, jasmine.any(Object));
  }));

  it('should update selectedParts when onPartsSelected is called', async () => {
    const parts: any[] = [{ name: 'part_1' }];
    componentInstance.onPartsSelected(parts);
    expect(componentInstance.selectedParts).toEqual(parts);
  });

  it('should change the total items when onTotalItemsChanged is call', async () => {
    componentInstance.onTotalItemsChanged(10);
    expect(componentInstance.totalItems).toEqual(10);
    componentInstance.onTotalItemsChanged(15);
    expect(componentInstance.totalItems).toEqual(15);
  });

  it('should trigger updateSupplierParts on supplierPartsComponents when triggerPartSearch is called', () => {
    const searchValue = 'testSearchValue';

    const updateSupplierPartsSpy = spyOn(
      SupplierPartsComponent.prototype,
      'updateSupplierParts',
    );

    componentInstance.searchControl.setValue(searchValue);

    // Act
    componentInstance.triggerPartSearch();

    // Assert
    expect(updateSupplierPartsSpy).toHaveBeenCalledWith('testSearchValue');
  });

  it('should close action on Esc key press', () => {
    // Initial state: action not closed
    expect(componentInstance.isOpen).toBeTruthy();

    // Trigger Esc key press event
    const mockEvent = new KeyboardEvent('keydown', { key: 'Escape' });
    document.dispatchEvent(mockEvent);

    // After Esc key press event, action should be closed
    expect(componentInstance.isOpen).toBeFalsy();
  });
});
