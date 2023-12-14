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

import { Component, OnInit } from '@angular/core'
import { AdminFacade } from '@page/admin/core/admin.facade';

@Component({
  selector: 'app-import-json',
  templateUrl: 'import-json.component.html',
  styleUrls: [ './import-json.component.scss' ],
})
export class ImportJsonComponent {
  public showError = false;
  public file: File;

  constructor(private adminFacade: AdminFacade) {
  }

  public getFile(event:any){
    this.file = event.target.files[0];

    if ('json' !== this.getFileExtension().toLowerCase() ) {
      console.error("Fehler: Nur JSON-Dateien sind erlaubt.");
      this.showError = true;
      return;
    }
    this.showError = false;
    console.log("Datei:", this.file);
  }
 public uploadFile(){
    this.adminFacade.postJsonImport(this.file).subscribe(res => {
      console.log(res);
    } );
  }

  public clearFile(): void {
    this.file = undefined;
    this.showError = false;
  }

  public shouldShowFileContainer_drag_and_drop(): boolean {
    return !this.file || 'json' !== this.getFileExtension().toLowerCase() || this.showError;
  }

  public shouldShowFileContainer_upload_file(): boolean {
    return this.file && 'json' === this.getFileExtension().toLowerCase();
  }

    public getFileExtension(): string {
        if (this.file) {
            const fileNameParts = this.file.name.split('.');
            return fileNameParts[fileNameParts.length - 1];
        }
        return '';
    }

}
