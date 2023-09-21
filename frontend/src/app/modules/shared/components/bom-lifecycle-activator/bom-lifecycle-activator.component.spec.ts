/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

import {ComponentFixture, TestBed} from '@angular/core/testing';
import {BomLifecycleActivatorComponent} from './bom-lifecycle-activator.component';
import {BomLifecycleState} from "@shared/components/bom-lifecycle-activator/bom-lifecycle-activator.model";


describe('BomLifecycleActivatorComponent', () => {
    let component: BomLifecycleActivatorComponent;
    let fixture: ComponentFixture<BomLifecycleActivatorComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            declarations: [BomLifecycleActivatorComponent]
        })
            .compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(BomLifecycleActivatorComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should initialize both buttons as active', () => {
        expect(component.bomLifecycleConfig.asPlannedActive).toBeTrue();
        expect(component.bomLifecycleConfig.asBuiltActive).toBeTrue();
    });

    it('should not toggle off "As Planned" if "As Built" is already off', () => {
        component.bomLifecycleConfig.asBuiltActive = false;
        component.toggleAsPlanned();
        expect(component.bomLifecycleConfig.asPlannedActive).toBeTrue();
    });

    it('should not toggle off "As Built" if "As Planned" is already off', () => {
        component.bomLifecycleConfig.asPlannedActive = false;
        component.toggleAsBuilt();
        expect(component.bomLifecycleConfig.asBuiltActive).toBeTrue();
    });

    it('should emit the correct BomLifecycleState', () => {
        spyOn(component.buttonClickEvent, 'emit');

        component.toggleAsPlanned();  // Turns off "As Planned"
        expect(component.buttonClickEvent.emit).toHaveBeenCalledWith(BomLifecycleState.ASBUILT);

        component.toggleAsBuilt(); // Turns off "As Built"
        expect(component.buttonClickEvent.emit).toHaveBeenCalledWith(BomLifecycleState.ASPLANNED);

        component.toggleAsBuilt(); // Turns on "As Built"
        component.toggleAsPlanned(); // Turns on "As Planned"
        expect(component.buttonClickEvent.emit).toHaveBeenCalledWith(BomLifecycleState.BOTH);
    });
});
