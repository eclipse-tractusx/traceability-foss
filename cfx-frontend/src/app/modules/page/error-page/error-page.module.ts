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

import { NgModule } from '@angular/core';
import { getI18nPageProvider } from '@core/i18n';
import { CommonModule } from '@angular/common';

import { ErrorPageRoutingModule } from './error-page.routing';
import { ErrorPageComponent } from './presentation/error-page.component';
import { TemplateModule } from '@shared/template.module';
import { SharedModule } from '@shared/shared.module';

@NgModule({
  declarations: [ErrorPageComponent],
  imports: [CommonModule, TemplateModule, SharedModule, ErrorPageRoutingModule],
  providers: [...getI18nPageProvider('page.error-page')],
})
export class ErrorPageModule {}
