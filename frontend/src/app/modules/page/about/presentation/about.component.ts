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

import { Component } from '@angular/core';
import { environment } from '@env';

@Component({
  selector: 'app-about',
  templateUrl: './about.component.html',
  styleUrls: [ './about.component.scss' ],
})
export class AboutComponent {
  name: string;
  repositoryPath: string;
  license: string;
  licensePath: string;
  noticePath: string;
  sourcePath: string;
  commitId: string;

  constructor() {
    this.license = 'Apache-2.0';
    this.name = 'Traceability Foss';
    this.commitId = environment.gitTag;
    this.repositoryPath = 'https://github.com/eclipse-tractusx/traceability-foss';
    this.licensePath = this.repositoryPath + '/blob/' + this.commitId + '/LICENSE';
    this.noticePath = this.repositoryPath + '/blob/' + this.commitId + '/NOTICE.md';
    this.sourcePath = this.repositoryPath + '/tree/' + this.commitId;
  }

  openLink(url: string): void {
    window.open(url, '_blank');
  }

  protected readonly environment = environment;
}
