import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { renderComponent } from '@tests/test-render.utils';

import { DeletionDialogComponent } from './deletion-dialog.component';

describe('DeletionDialogComponent', () => {
  const dialogRefMock = {
    close: jasmine.createSpy('close'),
  };

  const dataMock = {
    policyIds: [ 'policy1', 'policy2' ],
    title: 'Delete Policies',
  };

  const renderDeletionDialogComponent = () => renderComponent(DeletionDialogComponent, {
    imports: [ MatDialogModule ],
    providers: [
      { provide: MatDialogRef, useValue: dialogRefMock },
      { provide: MAT_DIALOG_DATA, useValue: dataMock },
    ],
  });

  it('should create', async () => {
    const { fixture } = await renderDeletionDialogComponent();
    const { componentInstance } = fixture;

    expect(componentInstance).toBeTruthy();
  });

  it('should initialize with provided data', async () => {
    const { fixture } = await renderDeletionDialogComponent();
    const { componentInstance } = fixture;

    expect(componentInstance.policyIds).toEqual(dataMock.policyIds);
    expect(componentInstance.title).toEqual(dataMock.title);
  });

  it('should call dialogRef.close(true) on save', async () => {
    const { fixture } = await renderDeletionDialogComponent();
    const { componentInstance } = fixture;

    componentInstance.save();
    expect(dialogRefMock.close).toHaveBeenCalledWith(true);
  });
});

