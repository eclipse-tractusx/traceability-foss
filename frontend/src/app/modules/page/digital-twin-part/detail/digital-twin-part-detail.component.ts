/********************************************************************************
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DigitalTwinPartFacade } from '../core/digital-twin-part.facade';
import { DigitalTwinPartDetailResponse } from '../model/digitalTwinPart.model';
import { DigitalTwinPartAssembler } from '@shared/assembler/digital-twin-part.assembler';


@Component({
  selector: 'app-digital-twin-part-detail',
  templateUrl: './digital-twin-part-detail.component.html',
  styleUrls: ['./digital-twin-part-detail.component.scss']
})
export class DigitalTwinPartDetailComponent implements OnInit {
  aasId!: string;
  detail!: DigitalTwinPartDetailResponse;

  registryData!: Record<string, string>;
  irsData!: Record<string, string>;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly facade: DigitalTwinPartFacade
  ) {}

  ngOnInit(): void {
    this.aasId = this.route.snapshot.paramMap.get('aasId')!;
    this.facade.getDigitalTwinPartDetail(this.aasId).subscribe(detail => {
      this.detail = detail;

      const assembled = DigitalTwinPartAssembler.assembleDetailView(detail);
      this.registryData = assembled.registryData;
      this.irsData = assembled.irsData;
    });
  }
}
