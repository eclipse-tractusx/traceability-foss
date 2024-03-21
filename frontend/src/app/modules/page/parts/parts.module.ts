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

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MatDialogModule } from '@angular/material/dialog';
import { getI18nPageProvider } from '@core/i18n';
import { PartsFacade } from '@page/parts/core/parts.facade';
import { PartsState } from '@page/parts/core/parts.state';
import { PartsDetailModule } from '@page/parts/detail/parts-detail.module';
import { RelationsModule } from '@shared/modules/relations/relations.module';
import { FormatPartSemanticDataModelToCamelCasePipe } from '@shared/pipes/format-part-semantic-data-model-to-camelcase.pipe';
import { BomLifecycleSettingsService } from '@shared/service/bom-lifecycle-settings.service';
import { SharedModule } from '@shared/shared.module';
import { TemplateModule } from '@shared/template.module';
import { AngularSplitModule } from 'angular-split';
import { PartsRoutingModule } from './parts.routing';
import { PartsComponent } from './presentation/parts.component';
import { RelationComponent } from './presentation/relation/relation.component';

@NgModule({
  declarations: [ PartsComponent, RelationComponent ],
  imports: [ CommonModule, TemplateModule, SharedModule, PartsRoutingModule, RelationsModule, AngularSplitModule, MatDialogModule, PartsDetailModule ],
  providers: [ PartsState, BomLifecycleSettingsService, PartsFacade, FormatPartSemanticDataModelToCamelCasePipe, ...getI18nPageProvider([ 'page.parts', 'partDetail' ]) ],
})
export class PartsModule {
}
