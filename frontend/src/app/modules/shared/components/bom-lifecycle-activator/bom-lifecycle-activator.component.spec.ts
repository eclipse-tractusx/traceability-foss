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

import {SharedModule} from '@shared/shared.module';
import {BomLifecycleActivatorComponent} from './bom-lifecycle-activator.component';
import {renderComponent} from "@tests/test-render.utils";
import {screen} from "@testing-library/angular";


describe('BomLifecycleActivatorComponent', () => {
    const renderBomLifecycleActivator = () => {
        return renderComponent(`<app-bom-lifecycle-activator></app-bom-lifecycle-activator>`, {
            declarations: [BomLifecycleActivatorComponent],
            imports: [SharedModule]
        });
    };

    it('should render the buttons', async () => {
        await renderBomLifecycleActivator();
        const asBuiltButton = screen.getByTestId("as-built-button");
        const asPlannedButton = screen.getByTestId("as-planned-button");
        expect(asBuiltButton).toBeInTheDocument();
        expect(asPlannedButton).toBeInTheDocument();
    });
});
