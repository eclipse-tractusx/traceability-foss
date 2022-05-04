/*
 * Copyright 2021 The PartChain Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { SvgIconsModule } from '@ngneat/svg-icon';
import { SharedModule } from '../shared/shared.module';
import { TemplateModule } from '../shared/template.module';
import { icons } from './../shared/shared-icons.module';
import { LayoutRoutingModule } from './layout.routing';
import { NavBarComponent } from './presentation/nav-bar/nav-bar.component';
import { PrivateLayoutComponent } from './presentation/private-layout/private-layout.component';
import { ResizerComponent } from './presentation/resizer/resizer.component';
import { SidebarComponent } from './presentation/sidebar/sidebar.component';
import { FooterComponent } from './presentation/footer/footer.component';
import { SpinnerOverlayComponent } from './presentation/spinner-overlay/presentation/spinner-overlay.component';

/**
 *
 *
 * @export
 * @class LayoutModule
 */
@NgModule({
  declarations: [
    PrivateLayoutComponent,
    NavBarComponent,
    ResizerComponent,
    SidebarComponent,
    FooterComponent,
    SpinnerOverlayComponent,
  ],
  imports: [CommonModule, LayoutRoutingModule, TemplateModule, SharedModule, SvgIconsModule.forChild(icons)],
})
export class LayoutModule {}
