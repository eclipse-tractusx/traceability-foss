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
import {Component, EventEmitter, Output} from '@angular/core';
import {
    BomLifecycleConfig,
    BomLifecycleSize
} from "@shared/components/bom-lifecycle-activator/bom-lifecycle-activator.model";

@Component({
    selector: 'app-bom-lifecycle-activator',
    templateUrl: './bom-lifecycle-activator.component.html',
    styleUrls: ['./bom-lifecycle-activator.component.scss']
})
export class BomLifecycleActivatorComponent {
    @Output() buttonClickEvent = new EventEmitter<BomLifecycleSize>();

    bomLifecycleConfig: BomLifecycleConfig = {
        asBuiltActive: true,
        asPlannedActive: true,
    }


    toggleAsPlanned() {
        this.bomLifecycleConfig.asPlannedActive = !this.bomLifecycleConfig.asPlannedActive;
        this.emitBomLifecycleState();
    }

    toggleAsBuilt() {
        // If the other button is also inactive, prevent this one from being deactivated
        this.bomLifecycleConfig.asBuiltActive = !this.bomLifecycleConfig.asBuiltActive;
        this.emitBomLifecycleState();
    }

    emitBomLifecycleState() {
        let size: BomLifecycleSize;
        if (this.bomLifecycleConfig.asPlannedActive && this.bomLifecycleConfig.asBuiltActive) {
            size = {
                asBuiltSize: 50,
                asPlannedSize: 50
            }
        } else if (this.bomLifecycleConfig.asPlannedActive) {
            size = {
                asBuiltSize: 0,
                asPlannedSize: 100
            }
        } else {
            size = {
                asBuiltSize: 100,
                asPlannedSize: 0
            }
        }
        this.buttonClickEvent.emit(size);
    }
}
