import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-deletion-dialog',
  templateUrl: './deletion-dialog.component.html',
  styleUrls: [ './deletion-dialog.component.scss' ],
})
export class DeletionDialogComponent {
  policyIds: string[];
  title: string;

  constructor(public dialogRef: MatDialogRef<DeletionDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: any) {
    this.policyIds = data?.policyIds;
    this.title = data?.title;
  }


  save() {
    this.dialogRef.close(true);
  }
}
