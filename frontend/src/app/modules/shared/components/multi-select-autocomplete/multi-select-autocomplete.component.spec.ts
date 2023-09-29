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


import {renderComponent} from "@tests/test-render.utils";
import {SharedModule} from "@shared/shared.module";

import {
    MultiSelectAutocompleteComponent
} from "@shared/components/multi-select-autocomplete/multi-select-autocomplete.component";
import {SemanticDataModel} from "@page/parts/model/parts.model";

describe('MultiSelectAutocompleteComponent', () => {

    const renderMultiSelectAutoCompleteComponent = () => {
        const placeholder = "test";
        const options = [SemanticDataModel.PARTASPLANNED, SemanticDataModel.BATCH];


        return renderComponent(MultiSelectAutocompleteComponent, {
            imports: [SharedModule],
            providers: [],
            componentProperties: {placeholder: placeholder, options: options},
        });
    };

    it('should create the component', async () => {
        const {fixture} = await renderMultiSelectAutoCompleteComponent();
        const {componentInstance} = fixture;
        expect(componentInstance).toBeTruthy();
    });

});
