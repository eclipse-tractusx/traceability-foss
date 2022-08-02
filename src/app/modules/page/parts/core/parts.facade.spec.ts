/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { PartsFacade } from '@page/parts/core/parts.facade';
import { PartsState } from '@page/parts/core/parts.state';
import { Part } from '@page/parts/model/parts.model';
import { PartsAssembler } from '@shared/assembler/parts.assembler';
import { PartsService } from '@shared/service/parts.service';
import { waitFor } from '@testing-library/angular';
import { BehaviorSubject, of, throwError } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import {
  MOCK_part_1,
  MOCK_part_2,
  MOCK_part_3,
  mockAssetList,
  mockAssets,
} from '../../../../mocks/services/parts-mock/parts.model';

describe('Parts facade', () => {
  let partsFacade: PartsFacade, partsState: PartsState, partsServiceMok: PartsService;

  beforeEach(() => {
    partsServiceMok = {
      getPart: id => new BehaviorSubject(mockAssetList[id]).pipe(map(part => PartsAssembler.assemblePart(part))),
      getParts: (_page, _pageSize, _sorting) => of(mockAssets).pipe(map(parts => PartsAssembler.assembleParts(parts))),
    } as PartsService;

    partsState = new PartsState();
    partsFacade = new PartsFacade(partsServiceMok, partsState);
  });

  describe('setParts', () => {
    it('should set parts if request is successful', async () => {
      const serviceSpy = jest.spyOn(partsServiceMok, 'getParts');
      const stateSpy = jest.spyOn(partsState, 'parts', 'set');
      partsFacade.setParts(0, 10);

      await waitFor(() => expect(serviceSpy).toHaveBeenCalledTimes(1));
      await waitFor(() => expect(serviceSpy).toHaveBeenCalledWith(0, 10, null));
      await waitFor(() => expect(stateSpy).toHaveBeenCalledWith({ data: PartsAssembler.assembleParts(mockAssets) }));
    });

    it('should not set parts if request fails', async () => {
      const serviceSpy = jest.spyOn(partsServiceMok, 'getParts');
      const stateSpy = jest.spyOn(partsState, 'parts', 'set');
      const spyData = new BehaviorSubject(null).pipe(switchMap(_ => throwError(() => new Error('error'))));
      serviceSpy.mockReturnValue(spyData);
      partsFacade.setParts(0, 10);

      await waitFor(() => expect(stateSpy).toHaveBeenCalledWith({ error: new Error('error') }));
    });
  });

  describe('setSelectedParts', () => {
    it('should set and update selected Parts', async () => {
      const stateSpy = jest.spyOn(partsState, 'selectedParts', 'set');
      partsFacade.setSelectedParts(PartsAssembler.assemblePart(MOCK_part_1).children);
      const childPart_1 = PartsAssembler.assemblePart(MOCK_part_2);
      const childPart_2 = PartsAssembler.assemblePart(MOCK_part_3);
      await waitFor(() =>
        expect(stateSpy.mock.calls).toEqual([
          [[{ id: 'MOCK_part_2' }, { id: 'MOCK_part_3' }]],
          [[childPart_1, { id: 'MOCK_part_3' }]],
          [[childPart_1, childPart_2]],
        ]),
      );
    });

    it('should set and update selected Parts even if it fails', async () => {
      const serviceSpy = jest.spyOn(partsServiceMok, 'getPart');
      const stateSpy = jest.spyOn(partsState, 'selectedParts', 'set');
      const spyData = new BehaviorSubject(null).pipe(switchMap(_ => throwError(() => new Error('error'))));
      serviceSpy.mockReturnValue(spyData);

      partsFacade.setSelectedParts(PartsAssembler.assemblePart(MOCK_part_1).children);

      await waitFor(() =>
        expect(stateSpy.mock.calls).toEqual([
          [[{ id: 'MOCK_part_2' }, { id: 'MOCK_part_3' }]],
          [[{ id: 'MOCK_part_2', error: true }, { id: 'MOCK_part_3' }]],
          [
            [
              { id: 'MOCK_part_2', error: true },
              { id: 'MOCK_part_3', error: true },
            ],
          ],
        ]),
      );
    });
  });

  describe('removeSelectedPart', () => {
    it('should remove item from selected list', async () => {
      const stateSpy = jest.spyOn(partsState, 'selectedParts', 'set');
      partsFacade.setSelectedParts([MOCK_part_2.id]);
      await waitFor(() => expect(stateSpy).toHaveBeenCalledTimes(2));

      partsFacade.removeSelectedPart({ id: MOCK_part_2.id } as Part);
      await waitFor(() => expect(stateSpy).toHaveBeenCalledTimes(3));
      await waitFor(() => expect(stateSpy).toHaveBeenLastCalledWith([]));
    });
  });

  describe('addItemToSelection', () => {
    it('should add item to existing list', async () => {
      const stateSpy = jest.spyOn(partsState, 'selectedParts', 'set');
      partsFacade.setSelectedParts([MOCK_part_2.id]);
      await waitFor(() => expect(stateSpy).toHaveBeenCalledTimes(2));

      partsFacade.addItemToSelection({ id: MOCK_part_3.id } as Part);
      await waitFor(() => expect(stateSpy).toHaveBeenCalledTimes(4));

      const part_2 = PartsAssembler.assemblePart(MOCK_part_2);
      const part_3 = PartsAssembler.assemblePart(MOCK_part_3);
      await waitFor(() => expect(stateSpy).toHaveBeenLastCalledWith([part_2, part_3]));
    });
  });
});
