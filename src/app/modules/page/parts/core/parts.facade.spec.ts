/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
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
import { Part } from '@page/parts/model/parts.model';
import { PartsAssembler } from '@shared/assembler/parts.assembler';
import { PartsService } from '@shared/service/parts.service';
import { waitFor } from '@testing-library/angular';
import { BehaviorSubject, firstValueFrom, of, throwError } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import {
  MOCK_part_1,
  MOCK_part_2,
  MOCK_part_3,
  mockAssetList,
  mockAssets,
} from '../../../../mocks/services/parts-mock/parts.test.model';

describe('Parts facade', () => {
  let partsFacade: PartsFacade, partsState: PartsState, partsServiceMok: PartsService;

  beforeEach(() => {
    partsServiceMok = {
      getPart: id => new BehaviorSubject(mockAssetList[id]).pipe(map(part => PartsAssembler.assemblePart(part))),
      getMyParts: (_page, _pageSize, _sorting) =>
        of(mockAssets).pipe(map(parts => PartsAssembler.assembleParts(parts))),
    } as PartsService;

    partsState = new PartsState();
    partsFacade = new PartsFacade(partsServiceMok, partsState);
  });

  describe('setParts', () => {
    it('should set parts if request is successful', async () => {
      const serviceSpy = spyOn(partsServiceMok, 'getMyParts').and.returnValue(
        of<Pagination<Part>>(PartsAssembler.assembleParts(mockAssets)),
      );
      partsFacade.setMyParts(0, 10);

      await waitFor(() => expect(serviceSpy).toHaveBeenCalledTimes(1));
      await waitFor(() => expect(serviceSpy).toHaveBeenCalledWith(0, 10, null));

      const parts = await firstValueFrom(partsState.myParts$);
      await waitFor(() =>
        expect(parts).toEqual({ error: undefined, loader: undefined, data: PartsAssembler.assembleParts(mockAssets) }),
      );
    });

    it('should not set parts if request fails', async () => {
      const spyData = new BehaviorSubject(null).pipe(switchMap(_ => throwError(() => new Error('error'))));
      spyOn(partsServiceMok, 'getMyParts').and.returnValue(spyData);

      partsFacade.setMyParts(0, 10);

      const parts = await firstValueFrom(partsState.myParts$);
      await waitFor(() => expect(parts).toEqual({ data: undefined, loader: undefined, error: new Error('error') }));
    });
  });

  describe('setSelectedParts', () => {
    it('should set and update selected Parts', async () => {
      partsFacade.setSelectedParts(PartsAssembler.assemblePart(MOCK_part_1).children);
      const childPart_1 = PartsAssembler.assemblePart(MOCK_part_2);
      const childPart_2 = PartsAssembler.assemblePart(MOCK_part_3);

      await waitFor(() => expect(partsState.selectedParts).toEqual([childPart_1, childPart_2]));
    });

    it('should set and update selected Parts even if it fails', async () => {
      const spyData = new BehaviorSubject(null).pipe(switchMap(_ => throwError(() => new Error('error'))));
      const serviceSpy = spyOn(partsServiceMok, 'getPart').and.returnValue(spyData);

      partsFacade.setSelectedParts(PartsAssembler.assemblePart(MOCK_part_1).children);
      await waitFor(() =>
        expect(partsState.selectedParts).toEqual([
          { id: 'MOCK_part_2', error: true },
          { id: 'MOCK_part_3', error: true },
        ] as Part[]),
      );
    });
  });

  describe('removeSelectedPart', () => {
    it('should remove item from selected list', async () => {
      partsFacade.setSelectedParts([MOCK_part_2.id]);
      await waitFor(() => expect(partsState.selectedParts).toEqual([PartsAssembler.assemblePart(MOCK_part_2)]));

      partsFacade.removeSelectedPart({ id: MOCK_part_2.id } as Part);
      await waitFor(() => expect(partsState.selectedParts).toEqual([]));
    });
  });

  describe('addItemToSelection', () => {
    it('should add item to existing list', async () => {
      const part_2 = PartsAssembler.assemblePart(MOCK_part_2);
      const part_3 = PartsAssembler.assemblePart(MOCK_part_3);

      partsFacade.setSelectedParts([MOCK_part_2.id]);
      await waitFor(() => expect(partsState.selectedParts).toEqual([part_2]));

      partsFacade.addItemToSelection({ id: MOCK_part_3.id } as Part);
      await waitFor(() => expect(partsState.selectedParts).toEqual([part_2, part_3]));
    });
  });
});
