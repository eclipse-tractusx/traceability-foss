/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
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

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { getI18nPageProvider } from '@core/i18n';
import { PartDetailsModule } from '@shared/modules/part-details/partDetails.module';
import { SharedModule } from '@shared/shared.module';
import { TemplateModule } from '@shared/template.module';
import { OtherPartsFacade } from './core/other-parts.facade';
import { OtherPartsService } from './core/other-parts.service';
import { OtherPartsState } from './core/other-parts.state';
import { OtherPartsRoutingModule } from './other-parts.routing';
import { OtherPartsComponent } from './presentation/other-parts.component';

@NgModule({
  declarations: [OtherPartsComponent],
  imports: [CommonModule, TemplateModule, SharedModule, OtherPartsRoutingModule, PartDetailsModule],
  providers: [OtherPartsState, OtherPartsFacade, OtherPartsService, ...getI18nPageProvider('page.otherParts')],
  exports: [OtherPartsComponent],
})
export class OtherPartsModule {}
