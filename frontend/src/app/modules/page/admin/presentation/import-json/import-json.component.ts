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

import { Component, Input } from '@angular/core';
import { AdminFacade } from '@page/admin/core/admin.facade';
import { ToastService } from '@shared/components/toasts/toast.service';
import { BehaviorSubject } from 'rxjs';

@Component({
  selector: 'app-import-json',
  templateUrl: 'import-json.component.html',
  styleUrls: [ './import-json.component.scss' ],
})
export class ImportJsonComponent {
  @Input() showError = false;
  @Input() file: File;

  importResponse = new BehaviorSubject<any>([]);
  displayUploader: boolean = true;




  constructor(private readonly adminFacade: AdminFacade, private readonly toastService: ToastService) {
    this.importResponse.subscribe(next => console.log(next))
  }

  public getFile(event: any) {
    this.file = event.target.files[0];

    if ('json' !== this.getFileExtension(this.file)) {
      this.showError = true;
      return;
    }
    this.showError = false;
  }
  public uploadFile(file: File) {
    this.adminFacade.postJsonImport(file).subscribe(response => {
      console.log(response);
      this.importResponse.next(response);
      this.toastService.success('pageAdmin.importJson.success');
      this.displayUploader = false;
    });
  }

  public clearFile(): void {
    this.file = undefined;
    this.showError = false;
  }

  public shouldShowFileContainer_upload_file(file: File): boolean {
    return this.file && 'json' === this.getFileExtension(file);
  }

  public getFileExtension(file: File): string {
    if (file) {
      const fileNameParts = file?.name?.split('.');
      return fileNameParts[fileNameParts.length - 1].toLowerCase();
    }
    return null;
  }

  protected readonly Object = Object;
}
