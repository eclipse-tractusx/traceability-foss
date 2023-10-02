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

import {OtherPartsFacade} from '@page/other-parts/core/other-parts.facade';
import {OtherPartsService} from '@page/other-parts/core/other-parts.service';
import {OtherPartsState} from '@page/other-parts/core/other-parts.state';
import {MainAspectType} from '@page/parts/model/mainAspectType.enum';
import {PartsAssembler} from '@shared/assembler/parts.assembler';
import {PartsService} from '@shared/service/parts.service';
import {waitFor} from '@testing-library/angular';
import {firstValueFrom, of} from 'rxjs';
import {map} from 'rxjs/operators';
import {mockAssets,} from '../../../../mocks/services/parts-mock/partsAsPlanned/partsAsPlanned.test.model';

describe('OtherPartsFacade', () => {
    let otherPartsFacade: OtherPartsFacade, otherPartsState: OtherPartsState, partsServiceMok: PartsService,
        otherPartsServiceMock: OtherPartsService;

    beforeEach(() => {
        otherPartsServiceMock = {
            getOtherPartsAsBuilt: (_page, _pageSize, _sorting, _owner) =>
                of(mockAssets).pipe(map(parts => PartsAssembler.assembleParts(parts, MainAspectType.AS_BUILT))),
            getOtherPartsAsPlanned: (_page, _pageSize, _sorting, _owner) =>
                of(mockAssets).pipe(map(parts => PartsAssembler.assembleParts(parts, MainAspectType.AS_PLANNED))),
        } as OtherPartsService;

        otherPartsState = new OtherPartsState();
        otherPartsFacade = new OtherPartsFacade(otherPartsServiceMock, otherPartsState);
    });

    describe('setActiveInvestigationForParts', () => {
        it('should set parts if request is successful', async () => {

            const otherParts = PartsAssembler.assembleOtherParts(mockAssets, MainAspectType.AS_BUILT);
            otherPartsState.supplierPartsAsBuilt = {data: otherParts};

            otherParts.content = otherParts.content.map(part => {
                const activeInvestigation = otherParts.content.some(currentPart => currentPart.id === part.id);
                return {...part, activeInvestigation};
            });

            const parts = await firstValueFrom(otherPartsState.supplierPartsAsBuilt$);
            await waitFor(() =>
                expect(parts).toEqual({
                    error: undefined,
                    loader: undefined,
                    data: otherParts,
                }),
            );
        });

        it('should not set parts if no data in state', async () => {

            const parts = await firstValueFrom(otherPartsState.supplierPartsAsBuilt$);
            await waitFor(() =>
                expect(parts).toEqual({
                    loader: true,
                }),
            );
        });
    });
});
