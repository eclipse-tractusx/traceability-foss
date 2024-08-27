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

import { Component, OnDestroy, OnInit, QueryList, ViewChildren } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { OtherPartsFacade } from '@page/other-parts/core/other-parts.facade';
import { CustomerPartsComponent } from '@page/other-parts/presentation/customer-parts/customer-parts.component';
import { SupplierPartsComponent } from '@page/other-parts/presentation/supplier-parts/supplier-parts.component';
import { resetMultiSelectionAutoCompleteComponent } from '@page/parts/core/parts.helper';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { BomLifecycleSize } from '@shared/components/bom-lifecycle-activator/bom-lifecycle-activator.model';
import { ToastService } from '@shared/components/toasts/toast.service';
import { PartDetailsFacade } from '@shared/modules/part-details/core/partDetails.facade';
import { BomLifecycleSettingsService, UserSettingView } from '@shared/service/bom-lifecycle-settings.service';
import { StaticIdService } from '@shared/service/staticId.service';

@Component({
    selector: 'app-other-parts',
    templateUrl: './other-parts.component.html',
    styleUrls: ['./other-parts.component.scss'],
})
export class OtherPartsComponent implements OnDestroy, OnInit {
    public selectedTab = 0;
    public showStartInvestigationArray = [true, false];

    public readonly supplierTabLabelId = this.staticIdService.generateId('OtherParts.supplierTabLabel');
    public readonly customerTabLabelId = this.staticIdService.generateId('OtherParts.customerTabLabel');

    public searchFormGroup = new FormGroup({});
    public searchControl: FormControl;
    @ViewChildren(SupplierPartsComponent) supplierPartsComponents: QueryList<SupplierPartsComponent>;
    @ViewChildren(CustomerPartsComponent) customerPartsComponents: QueryList<CustomerPartsComponent>;

    constructor(
        private readonly otherPartsFacade: OtherPartsFacade,
        private readonly partDetailsFacade: PartDetailsFacade,
        private readonly staticIdService: StaticIdService,
        public userSettings: BomLifecycleSettingsService,
        public toastService: ToastService,
    ) { }

    ngOnInit(): void {
        this.searchFormGroup.addControl('partSearch', new FormControl([]));
        this.searchControl = this.searchFormGroup.get('partSearch') as unknown as FormControl;
    }

    public bomLifecycleSize: BomLifecycleSize = this.userSettings.getSize(UserSettingView.OTHER_PARTS);

    public ngOnDestroy(): void {
        this.otherPartsFacade.unsubscribeParts();
    }

    triggerPartSearch() {
        this.resetFilterAndShowToast();

        const searchValue = this.searchFormGroup.get('partSearch').value;

        for (const supplierPartsComponent of this.supplierPartsComponents) {
            supplierPartsComponent.updateSupplierParts(searchValue);
        }
        for (const customerPartsComponent of this.customerPartsComponents) {
            customerPartsComponent.updateCustomerParts(searchValue);
        }
    }

    private resetFilterAndShowToast() {
        const oneFilterSet = false;

        const resetComponents = (
            components: QueryList<SupplierPartsComponent> | QueryList<CustomerPartsComponent>,
        ) => {
            for (const component of components) {
                const filterIsSet = resetMultiSelectionAutoCompleteComponent(component.partsTableComponents, oneFilterSet);
                if (filterIsSet) {
                    this.toastService.info('parts.input.global-search.toastInfoTitle', 'parts.input.global-search.toastInfo');
                }
            }
        };

        resetComponents(this.supplierPartsComponents);
        resetComponents(this.customerPartsComponents);
    }

    public onTabChange({ index }: MatTabChangeEvent): void {
        this.selectedTab = index;
        this.partDetailsFacade.selectedPart = null;
    }

    public handleTableActivationEvent(bomLifecycleSize: BomLifecycleSize) {
        this.bomLifecycleSize = bomLifecycleSize;
    }

    protected readonly MainAspectType = MainAspectType;
    protected readonly UserSettingView = UserSettingView;
}
