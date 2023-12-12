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

import { Component } from '@angular/core'
import { AdminFacade } from '@page/admin/core/admin.facade';

@Component({
  selector: 'app-import-json',
  templateUrl: 'import-json.component.html',
  styleUrls: [ './import-json.component.scss' ],
})
export class ImportJsonComponent{
  public jsonfile = false;
  public showError = false;
  public file:any;

  constructor(private readonly adminFacade: AdminFacade) {
  }
  public getFile(event:any){
    this.file = event.target.files[0];

    if (this.file) {
      const fileNameParts = this.file.name.split('.');
      const fileExtension = fileNameParts[fileNameParts.length - 1].toLowerCase();

      if (fileExtension !== 'json') {
        console.error("Fehler: Nur JSON-Dateien sind erlaubt.");
        this.showError = true;
        return;
      }
      this.showError = false;
      console.log("Datei:", this.file);

    }
  }
 public uploadFile(){
    this.adminFacade.postJsonImport(this.file)
  }

  public  isJsonFile(): boolean {
    return this.file && this.getFileExtension().toLowerCase() === 'json';
  }

  public isNotJsonFile(): boolean {
    return !this.isJsonFile();
  }

  public getFileExtension(): string {
    if (this.file) {
      const fileNameParts = this.file.name.split('.');
      return fileNameParts[fileNameParts.length - 1];
    }
    return '';
  }

}
