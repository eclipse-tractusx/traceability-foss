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

import { Pagination } from "@core/model/pagination.model";
import { PartsAssembler } from '@shared/assembler/parts.assembler';
import { View } from '@shared/model/view.model';
import { Part } from "../model/parts.model";
import { PartsState } from "./parts.state";
import { mockAssets } from "src/app/mocks/services/parts-mock/partsAsBuilt/partsAsBuilt.test.model";
import { MainAspectType } from "../model/mainAspectType.enum";

describe('PartsState', () => {
  it('setting partsAsBuilt should update partsAsBuilt$', () => {
    const partsState = new PartsState();
    const partsAsBuilt = {
      error: undefined,
      loader: undefined,
      data: PartsAssembler.assembleParts(mockAssets, MainAspectType.AS_BUILT),
    } as View<Pagination<Part>>;
    partsState.partsAsBuilt = partsAsBuilt;
    partsState.partsAsBuilt$.subscribe(parts => expect(parts).toEqual(partsAsBuilt));
  });

  it('setting partsAsPlanned should update partsAsPlanned$', () => {
    const partsState = new PartsState();
    const partsAsPlanned = {
      error: undefined,
      loader: undefined,
      data: PartsAssembler.assembleParts(mockAssets, MainAspectType.AS_PLANNED),
    } as View<Pagination<Part>>;
    partsState.partsAsPlanned = partsAsPlanned;
    partsState.partsAsPlanned$.subscribe(parts => expect(parts).toEqual(partsAsPlanned));
  });

  it('setting partsAsOrdered should update partsAsOrdered$', () => {
    const partsState = new PartsState();
    const partsAsOrdered = {
      error: undefined,
      loader: undefined,
      data: PartsAssembler.assembleParts(mockAssets, MainAspectType.AS_ORDERED),
    } as View<Pagination<Part>>;
    partsState.partsAsOrdered = partsAsOrdered;
    partsState.partsAsOrdered$.subscribe(parts => expect(parts).toEqual(partsAsOrdered));
  });

  it('setting partsAsDesigned should update partsAsDesigned$', () => {
    const partsState = new PartsState();
    const partsAsDesigned = {
      error: undefined,
      loader: undefined,
      data: PartsAssembler.assembleParts(mockAssets, MainAspectType.AS_DESIGNED),
    } as View<Pagination<Part>>;
    partsState.partsAsDesigned = partsAsDesigned;
    partsState.partsAsDesigned$.subscribe(parts => expect(parts).toEqual(partsAsDesigned));
  });

  it('setting partsAsSupported should update partsAsSupported$', () => {
    const partsState = new PartsState();
    const partsAsSupported = {
      error: undefined,
      loader: undefined,
      data: PartsAssembler.assembleParts(mockAssets, MainAspectType.AS_SUPPORTED),
    } as View<Pagination<Part>>;
    partsState.partsAsSupported = partsAsSupported;
    partsState.partsAsSupported$.subscribe(parts => expect(parts).toEqual(partsAsSupported));
  });

  it('setting partsAsRecycled should update partsAsRecycled$', () => {
    const partsState = new PartsState();
    const partsAsRecycled = {
      error: undefined,
      loader: undefined,
      data: PartsAssembler.assembleParts(mockAssets, MainAspectType.AS_RECYCLED),
    } as View<Pagination<Part>>;
    partsState.partsAsRecycled = partsAsRecycled;
    partsState.partsAsRecycled$.subscribe(parts => expect(parts).toEqual(partsAsRecycled));
  });

});
