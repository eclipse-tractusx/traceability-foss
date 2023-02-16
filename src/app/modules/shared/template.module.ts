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

import {
  NgxMatDatetimePickerModule,
  NgxMatNativeDateModule,
  NgxMatTimepickerModule,
} from '@angular-material-components/datetime-picker';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatBadgeModule } from '@angular/material/badge';
import { MatLegacyButtonModule as MatButtonModule } from '@angular/material/legacy-button';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatLegacyCardModule as MatCardModule } from '@angular/material/legacy-card';
import { MatLegacyCheckboxModule as MatCheckboxModule } from '@angular/material/legacy-checkbox';
import { MatLegacyChipsModule as MatChipsModule } from '@angular/material/legacy-chips';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatLegacyDialogModule as MatDialogModule } from '@angular/material/legacy-dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatLegacyFormFieldModule as MatFormFieldModule } from '@angular/material/legacy-form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatLegacyInputModule as MatInputModule } from '@angular/material/legacy-input';
import { MatLegacyListModule as MatListModule } from '@angular/material/legacy-list';
import { MatLegacyMenuModule as MatMenuModule } from '@angular/material/legacy-menu';
import { MatLegacyPaginatorModule as MatPaginatorModule } from '@angular/material/legacy-paginator';
import { MatLegacyProgressBarModule as MatProgressBarModule } from '@angular/material/legacy-progress-bar';
import { MatLegacyProgressSpinnerModule as MatProgressSpinnerModule } from '@angular/material/legacy-progress-spinner';
import { MatLegacyRadioModule as MatRadioModule } from '@angular/material/legacy-radio';
import { MatLegacySelectModule as MatSelectModule } from '@angular/material/legacy-select';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatLegacySlideToggleModule as MatSlideToggleModule } from '@angular/material/legacy-slide-toggle';
import { MatLegacySnackBarModule as MatSnackBarModule } from '@angular/material/legacy-snack-bar';
import { MatSortModule } from '@angular/material/sort';
import { MatLegacyTableModule as MatTableModule } from '@angular/material/legacy-table';
import { MatLegacyTabsModule as MatTabsModule } from '@angular/material/legacy-tabs';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatLegacyTooltipModule as MatTooltipModule } from '@angular/material/legacy-tooltip';
import { MatTreeModule } from '@angular/material/tree';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,

    // Material
    MatButtonModule,
    MatSidenavModule,
    MatListModule,
    MatMenuModule,
    MatIconModule,
    MatCardModule,
    MatToolbarModule,
    MatFormFieldModule,
    MatInputModule,
    MatSlideToggleModule,
    MatTreeModule,
    MatDividerModule,
    MatProgressBarModule,
    MatProgressSpinnerModule,
    MatExpansionModule,
    MatBadgeModule,
    MatNativeDateModule,
    MatDatepickerModule,
    MatButtonToggleModule,
    NgxMatTimepickerModule,
    MatDialogModule,
    MatTableModule,
    MatSelectModule,
    MatCheckboxModule,
    MatPaginatorModule,
    MatSortModule,
    MatRadioModule,
    MatChipsModule,
    NgxMatDatetimePickerModule,
    NgxMatNativeDateModule,
    MatTooltipModule,
    MatSnackBarModule,
  ],
  exports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,

    // Material
    MatButtonModule,
    MatSidenavModule,
    MatListModule,
    MatMenuModule,
    MatIconModule,
    MatCardModule,
    MatToolbarModule,
    MatFormFieldModule,
    MatInputModule,
    MatSlideToggleModule,
    MatTreeModule,
    MatDividerModule,
    MatProgressBarModule,
    MatProgressSpinnerModule,
    MatExpansionModule,
    MatBadgeModule,
    MatNativeDateModule,
    MatDatepickerModule,
    MatButtonToggleModule,
    NgxMatTimepickerModule,
    MatDialogModule,
    MatTableModule,
    MatSelectModule,
    MatCheckboxModule,
    MatPaginatorModule,
    MatSortModule,
    MatRadioModule,
    MatChipsModule,
    NgxMatDatetimePickerModule,
    NgxMatNativeDateModule,
    MatTooltipModule,
    MatTabsModule,
    MatSnackBarModule,
  ],
})
export class TemplateModule {}
