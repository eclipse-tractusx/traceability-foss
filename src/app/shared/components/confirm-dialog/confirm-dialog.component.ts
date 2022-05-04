/*
 * Copyright 2021 The PartChain Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Component, ElementRef, Inject, ViewChild } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

/**
 *
 *
 * @export
 * @class ConfirmDialogModel
 */
export class ConfirmDialogModel {
  /**
   * @constructor Creates an instance of ConfirmDialogModel
   * @param {string} title
   * @param {string} message
   * @param {string} type
   * @param {boolean} hasComment
   * @param {string} primaryBtnLabel
   * @param {string} secondaryBtnLabel
   * @memberof ConfirmDialogModel
   */
  constructor(
    public title: string,
    public message: string,
    public type: string,
    public hasComment: boolean,
    public primaryBtnLabel: string,
    public secondaryBtnLabel: string,
  ) {}
}

/**
 *
 *
 * @export
 * @class ConfirmDialogComponent
 */
@Component({
  selector: 'app-confirm-dialog',
  templateUrl: './confirm-dialog.component.html',
  styleUrls: ['./confirm-dialog.component.scss'],
})
export class ConfirmDialogComponent {
  /**
   * Dialog title
   *
   * @type {string}
   * @memberof ConfirmDialogComponent
   */
  public title: string;

  /**
   * Dialog message
   *
   * @type {string}
   * @memberof ConfirmDialogComponent
   */
  public message: string;

  /**
   * Dialog type
   *
   * @type {string}
   * @memberof ConfirmDialogComponent
   */
  public type: string;

  /**
   * Primary button label
   *
   * @type {string}
   * @memberof ConfirmDialogComponent
   */
  public primaryBtnLabel: string;

  /**
   * Secondary button label
   *
   * @type {string}
   * @memberof ConfirmDialogComponent
   */
  public secondaryBtnLabel: string;

  /**
   * Comment element ref
   *
   * @type {ElementRef}
   * @memberof ConfirmDialogComponent
   */
  @ViewChild('comment', { read: ElementRef }) comment: ElementRef;

  /**
   * Comment exists
   *
   * @type {boolean}
   * @memberof ConfirmDialogComponent
   */
  public hasComment: boolean;

  /**
   * @constructor ConfirmDialogComponent
   * @param {MatDialogRef<ConfirmDialogComponent>} dialogRef
   * @param {ConfirmDialogModel} data
   * @memberof ConfirmDialogComponent
   */
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

  /**
   * Confirm event
   *
   * @return {void}
   * @memberof ConfirmDialogComponent
   */
  public onConfirm(): void {
    // Close the dialog, return true
    this.dialogRef.close(true);
  }

  /**
   * Dismiss event
   *
   * @return {void}
   * @memberof ConfirmDialogComponent
   */
  public onDismiss(): void {
    // Close the dialog, return false
    this.dialogRef.close(false);
  }
}
