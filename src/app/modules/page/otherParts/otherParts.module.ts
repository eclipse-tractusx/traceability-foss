/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { getI18nPageProvider } from '@core/i18n';
import { RequestInvestigationComponent } from '@page/otherParts/presentation/request-investigation/requestInvestigation.component';
import { PartDetailsModule } from '@shared/modules/part-details/partDetails.module';
import { SharedModule } from '@shared/shared.module';
import { TemplateModule } from '@shared/template.module';

import { OtherPartsFacade } from './core/otherParts.facade';
import { OtherPartsService } from './core/otherParts.service';
import { OtherPartsState } from './core/otherParts.state';
import { OtherPartsRoutingModule } from './otherParts.routing';
import { OtherPartsComponent } from './presentation/otherParts.component';

@NgModule({
  declarations: [OtherPartsComponent, RequestInvestigationComponent],
  imports: [CommonModule, TemplateModule, SharedModule, OtherPartsRoutingModule, PartDetailsModule],
  providers: [OtherPartsState, OtherPartsFacade, OtherPartsService, ...getI18nPageProvider('page.otherParts')],
  exports: [OtherPartsComponent],
})
export class OtherPartsModule {}
