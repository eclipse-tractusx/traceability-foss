import { Component, ElementRef, Inject, ViewChild } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

export class ConfirmDialogModel {
  constructor(
    public title: string,
    public message: string,
    public type: string,
    public hasComment: boolean,
    public primaryBtnLabel: string,
    public secondaryBtnLabel: string,
  ) {}
}

@Component({
  selector: 'app-confirm-dialog',
  templateUrl: './confirm-dialog.component.html',
  styleUrls: ['./confirm-dialog.component.scss'],
})
export class ConfirmDialogComponent {
  @ViewChild('comment', { read: ElementRef }) comment: ElementRef;

  public title: string;
  public message: string;
  public type: string;
  public primaryBtnLabel: string;
  public secondaryBtnLabel: string;
  public hasComment: boolean;

  constructor(
    public dialogRef: MatDialogRef<ConfirmDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ConfirmDialogModel,
  ) {
    dialogRef.disableClose = true;
    // Update view with given values
    this.title = data.title;
    this.message = data.message;
    this.type = data.type;
    this.hasComment = data.hasComment;
    this.primaryBtnLabel = data.primaryBtnLabel;
    this.secondaryBtnLabel = data.secondaryBtnLabel;
  }

  public onConfirm(): void {
    // Close the dialog, return true
    this.dialogRef.close(true);
  }

  public onDismiss(): void {
    // Close the dialog, return false
    this.dialogRef.close(false);
  }
}
