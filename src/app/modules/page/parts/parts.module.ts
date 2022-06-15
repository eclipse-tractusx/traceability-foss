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
import { PartsFacade } from '@page/parts/core/parts.facade';
import { PartsService } from '@page/parts/core/parts.service';
import { PartsState } from '@page/parts/core/parts.state';
import { PartRelationComponent } from '@page/parts/relations/presentation/part-relation.component';
import { RelationsModule } from '@page/parts/relations/relations.module';
import { FormatDatePipe, SharedModule, TemplateModule } from '@shared';
import { PartsRoutingModule } from './parts.routing';
import { PartsComponent } from './presentation/parts.component';
import { PartDetailComponent } from './presentation/part-detail/part-detail.component';
import { RelationComponent } from './presentation/relation/relation.component';

@NgModule({
  declarations: [PartsComponent, PartDetailComponent, RelationComponent],
  imports: [CommonModule, TemplateModule, SharedModule, PartsRoutingModule, RelationsModule],
  providers: [PartsState, PartsFacade, PartsService, ...getI18nPageProvider('page.parts'), FormatDatePipe],
  exports: [PartDetailComponent],
})
export class PartsModule {}
