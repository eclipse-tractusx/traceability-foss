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


import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DigitalTwinPartComponent } from './presentation/digital-twin-part.component';
import { DigitalTwinPartService } from '@shared/service/digitalTwinPart.service';
import { DigitalTwinPartFacade } from './core/digital-twin-part.facade';
import { DigitalTwinPartState } from './core/digital-twin-part.state';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TemplateModule } from '@shared/template.module';
import { SharedModule } from '@shared/shared.module';
import { ConfigurationDialogComponent } from './presentation/configuration-dialog/configuration-dialog.component';
import { ConfigurationService } from '@shared/service/configuration.service';
import { DigitalTwinPartDetailComponent } from './detail/digital-twin-part-detail.component';
import { RouterModule } from '@angular/router';

@NgModule({
  declarations: [DigitalTwinPartComponent, ConfigurationDialogComponent, DigitalTwinPartDetailComponent,],
  imports: [
    CommonModule,
    SharedModule,
    TemplateModule,
    MatCardModule,
    MatProgressSpinnerModule,
    RouterModule
  ],
  providers: [DigitalTwinPartFacade, DigitalTwinPartState, DigitalTwinPartService, ConfigurationService, RouterModule],
  exports: [DigitalTwinPartComponent, ConfigurationDialogComponent, DigitalTwinPartDetailComponent]

})
export class DigitalTwinPartModule { }
