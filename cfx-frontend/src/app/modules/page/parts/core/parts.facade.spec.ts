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

import { Pagination } from '@core/model/pagination.model';
import { PartsFacade } from '@page/parts/core/parts.facade';
import { PartsState } from '@page/parts/core/parts.state';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { AssetAsBuiltFilter, AssetAsDesignedFilter, AssetAsOrderedFilter, AssetAsPlannedFilter, AssetAsRecycledFilter, AssetAsSupportedFilter, Part } from '@page/parts/model/parts.model';
import { PartsAssembler } from '@shared/assembler/parts.assembler';
import { PartsService } from '@shared/service/parts.service';
import { waitFor } from '@testing-library/angular';
import { BehaviorSubject, firstValueFrom, of, throwError } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import {
    mockAssetList,
    mockAssets
} from '../../../../mocks/services/parts-mock/partsAsPlanned/partsAsPlanned.test.model';

describe('Parts facade', () => {
    let partsFacade: PartsFacade, partsState: PartsState, partsServiceMok: PartsService;

    beforeEach(() => {

        partsServiceMok = {
            getPart: (id, type) => new BehaviorSubject(mockAssetList[id]).pipe(map(part => PartsAssembler.assemblePart(part, MainAspectType.AS_BUILT))),
            getPartsAsBuilt: (_page, _pageSize, _sorting, assetAsBuiltFilter) =>
                of(mockAssets).pipe(map(parts => PartsAssembler.assembleParts(parts, MainAspectType.AS_BUILT))),
            getPartsAsPlanned: (_page, _pageSize, _sorting, assetAsPlannedFilter) =>
                of(mockAssets).pipe(map(parts => PartsAssembler.assembleParts(parts, MainAspectType.AS_PLANNED))),
            getPartsAsDesigned: (_page, _pageSize, _sorting, assetAsDesignedFilter) =>
                of(mockAssets).pipe(map(parts => PartsAssembler.assembleParts(parts, MainAspectType.AS_DESIGNED))),
            getPartsAsSupported: (_page, _pageSize, _sorting, assetAsSupportedFilter) =>
                of(mockAssets).pipe(map(parts => PartsAssembler.assembleParts(parts, MainAspectType.AS_SUPPORTED))),
            getPartsAsOrdered: (_page, _pageSize, _sorting, assetAsOrderedFilter) =>
                of(mockAssets).pipe(map(parts => PartsAssembler.assembleParts(parts, MainAspectType.AS_ORDERED))),
            getPartsAsRecycled: (_page, _pageSize, _sorting, assetAsRecycledFilter) =>
                of(mockAssets).pipe(map(parts => PartsAssembler.assembleParts(parts, MainAspectType.AS_RECYCLED))),
        } as PartsService;

        partsState = new PartsState();
        partsFacade = new PartsFacade(partsServiceMok, partsState);
    });

    describe('setParts', () => {
        it('should set parts if request is successful', async () => {
            const serviceSpy = spyOn(partsServiceMok, 'getPartsAsBuilt').and.returnValue(
                of<Pagination<Part>>(PartsAssembler.assembleParts(mockAssets, MainAspectType.AS_BUILT)),
            );
            partsFacade.setPartsAsBuilt(0, 10);

            await waitFor(() => expect(serviceSpy).toHaveBeenCalledTimes(1));
            await waitFor(() => expect(serviceSpy).toHaveBeenCalledWith(0, 10, [], undefined, undefined));

            const parts = await firstValueFrom(partsState.partsAsBuilt$);
            await waitFor(() =>
                expect(parts).toEqual({
                    error: undefined,
                    loader: undefined,
                    data: PartsAssembler.assembleParts(mockAssets, MainAspectType.AS_BUILT),
                }),
            );
        });

        it('should set partsAsBuiltSubscription if setPartsAsBuilt is called', async () => {
            const filter = { id: '123' } as AssetAsBuiltFilter;
            partsFacade.setPartsAsBuilt(0, 10, [], filter);
            expect(partsFacade['partsAsBuiltSubscription']).toBeTruthy();
        });

        it('should set partsAsPlannedSubscription if setPartsAsPlanned is called', async () => {
            const filter = { id: '123' } as AssetAsPlannedFilter;
            partsFacade.setPartsAsPlanned(0, 10, [], filter);
            expect(partsFacade['partsAsPlannedSubscription']).toBeTruthy();
        });

        it('should set partsAsDesignedSubscription if setPartsAsDesigned is called', async () => {
            const filter = { id: '123' } as AssetAsDesignedFilter;
            partsFacade.setPartsAsDesigned(0, 10, [], filter);
            expect(partsFacade['partsAsDesignedSubscription']).toBeTruthy();
        });

        it('should set partsAsOrderedSubscription if setPartsAsOrdered is called', async () => {
            const filter = { id: '123' } as AssetAsOrderedFilter;
            partsFacade.setPartsAsOrdered(0, 10, [], filter);
            expect(partsFacade['partsAsOrderedSubscription']).toBeTruthy();
        });

        it('should set partsAsSupportedSubscription if setPartsAsSupported is called', async () => {
            const filter = { id: '123' } as AssetAsSupportedFilter;
            partsFacade.setPartsAsSupported(0, 10, [], filter);
            expect(partsFacade['partsAsSupportedSubscription']).toBeTruthy();
        });

        it('should set partsAsRecycledSubscription if setPartsAsRecycled is called', async () => {
            const filter = { id: '123' } as AssetAsRecycledFilter;
            partsFacade.setPartsAsRecycled(0, 10, [], filter);
            expect(partsFacade['partsAsRecycledSubscription']).toBeTruthy();
        });

        it('should set parts including filter if request is successful', async () => {
            const serviceSpy = spyOn(partsServiceMok, 'getPartsAsBuilt').and.returnValue(
                of<Pagination<Part>>(PartsAssembler.assembleParts(mockAssets, MainAspectType.AS_BUILT)),
            );
            const filter = { id: '123' } as AssetAsBuiltFilter;
            partsFacade.setPartsAsBuilt(0, 10, [], filter);

            await waitFor(() => expect(serviceSpy).toHaveBeenCalledTimes(1));
            await waitFor(() => expect(serviceSpy).toHaveBeenCalledWith(0, 10, [], filter, undefined));

            const parts = await firstValueFrom(partsState.partsAsBuilt$);
            await waitFor(() =>
                expect(parts).toEqual({
                    error: undefined,
                    loader: undefined,
                    data: PartsAssembler.assembleParts(mockAssets, MainAspectType.AS_BUILT),
                }),
            );
        });

        it('should set partsasplanned including filter if request is successful', async () => {
            const serviceSpy = spyOn(partsServiceMok, 'getPartsAsPlanned').and.returnValue(
                of<Pagination<Part>>(PartsAssembler.assembleParts(mockAssets, MainAspectType.AS_PLANNED)),
            );
            const filter = { id: '123' } as AssetAsPlannedFilter;
            partsFacade.setPartsAsPlanned(0, 10, [], filter);

            await waitFor(() => expect(serviceSpy).toHaveBeenCalledTimes(1));
            await waitFor(() => expect(serviceSpy).toHaveBeenCalledWith(0, 10, [], filter, undefined));

            const parts = await firstValueFrom(partsState.partsAsPlanned$);
            await waitFor(() =>
                expect(parts).toEqual({
                    error: undefined,
                    loader: undefined,
                    data: PartsAssembler.assembleParts(mockAssets, MainAspectType.AS_PLANNED),
                }),
            );
        });

        it('should set partsAsDesigned including filter if request is successful', async () => {
            const serviceSpy = spyOn(partsServiceMok, 'getPartsAsDesigned').and.returnValue(
                of<Pagination<Part>>(PartsAssembler.assembleParts(mockAssets, MainAspectType.AS_DESIGNED)),
            );
            const filter = { id: '123' } as AssetAsDesignedFilter;
            partsFacade.setPartsAsDesigned(0, 10, [], filter);

            await waitFor(() => expect(serviceSpy).toHaveBeenCalledTimes(1));
            await waitFor(() => expect(serviceSpy).toHaveBeenCalledWith(0, 10, [], filter, undefined));

            const parts = await firstValueFrom(partsState.partsAsDesigned$);
            await waitFor(() =>
                expect(parts).toEqual({
                    error: undefined,
                    loader: undefined,
                    data: PartsAssembler.assembleParts(mockAssets, MainAspectType.AS_DESIGNED),
                }),
            );
        });

        it('should set partsAsOrdered including filter if request is successful', async () => {
            const serviceSpy = spyOn(partsServiceMok, 'getPartsAsOrdered').and.returnValue(
                of<Pagination<Part>>(PartsAssembler.assembleParts(mockAssets, MainAspectType.AS_ORDERED)),
            );
            const filter = { id: '123' } as AssetAsOrderedFilter;
            partsFacade.setPartsAsOrdered(0, 10, [], filter);

            await waitFor(() => expect(serviceSpy).toHaveBeenCalledTimes(1));
            await waitFor(() => expect(serviceSpy).toHaveBeenCalledWith(0, 10, [], filter, undefined));

            const parts = await firstValueFrom(partsState.partsAsOrdered$);
            await waitFor(() =>
                expect(parts).toEqual({
                    error: undefined,
                    loader: undefined,
                    data: PartsAssembler.assembleParts(mockAssets, MainAspectType.AS_ORDERED),
                }),
            );
        });

        it('should set partsAsSupported including filter if request is successful', async () => {
            const serviceSpy = spyOn(partsServiceMok, 'getPartsAsSupported').and.returnValue(
                of<Pagination<Part>>(PartsAssembler.assembleParts(mockAssets, MainAspectType.AS_SUPPORTED)),
            );
            const filter = { id: '123' } as AssetAsSupportedFilter;
            partsFacade.setPartsAsSupported(0, 10, [], filter);

            await waitFor(() => expect(serviceSpy).toHaveBeenCalledTimes(1));
            await waitFor(() => expect(serviceSpy).toHaveBeenCalledWith(0, 10, [], filter, undefined));

            const parts = await firstValueFrom(partsState.partsAsSupported$);
            await waitFor(() =>
                expect(parts).toEqual({
                    error: undefined,
                    loader: undefined,
                    data: PartsAssembler.assembleParts(mockAssets, MainAspectType.AS_SUPPORTED),
                }),
            );
        });

        it('should set partsAsRecycled including filter if request is successful', async () => {
            const serviceSpy = spyOn(partsServiceMok, 'getPartsAsRecycled').and.returnValue(
                of<Pagination<Part>>(PartsAssembler.assembleParts(mockAssets, MainAspectType.AS_RECYCLED)),
            );
            const filter = { id: '123' } as AssetAsRecycledFilter;
            partsFacade.setPartsAsRecycled(0, 10, [], filter);

            await waitFor(() => expect(serviceSpy).toHaveBeenCalledTimes(1));
            await waitFor(() => expect(serviceSpy).toHaveBeenCalledWith(0, 10, [], filter, undefined));

            const parts = await firstValueFrom(partsState.partsAsRecycled$);
            await waitFor(() =>
                expect(parts).toEqual({
                    error: undefined,
                    loader: undefined,
                    data: PartsAssembler.assembleParts(mockAssets, MainAspectType.AS_RECYCLED),
                }),
            );
        });

        it('should not set parts if request fails', async () => {
            const spyData = new BehaviorSubject(null).pipe(switchMap(_ => throwError(() => new Error('error'))));
            spyOn(partsServiceMok, 'getPartsAsPlanned').and.returnValue(spyData);

            partsFacade.setPartsAsPlanned(0, 10);

            const parts = await firstValueFrom(partsState.partsAsPlanned$);
            await waitFor(() => expect(parts).toEqual({ data: undefined, loader: undefined, error: new Error('error') }));
        });
    });
});
