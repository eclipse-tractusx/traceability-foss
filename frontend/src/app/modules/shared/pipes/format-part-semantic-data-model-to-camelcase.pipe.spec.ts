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

import { TestBed } from '@angular/core/testing';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { SemanticDataModel } from '@page/parts/model/parts.model';
import { PartsAssembler } from '@shared/assembler/parts.assembler';
import {
  FormatPartSemanticDataModelToCamelCasePipe,
} from '@shared/pipes/format-part-semantic-data-model-to-camelcase.pipe';
import { MOCK_part_1 } from '../../../mocks/services/parts-mock/partsAsPlanned/partsAsPlanned.test.model';

describe('FormatPartSemanticDataModelToCamelCasePipe', () => {
  let formatPartSemanticDataModelToCamelCasePipe: FormatPartSemanticDataModelToCamelCasePipe;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        FormatPartSemanticDataModelToCamelCasePipe,
      ],
    });
    formatPartSemanticDataModelToCamelCasePipe = TestBed.inject(FormatPartSemanticDataModelToCamelCasePipe);
  });

  [
    {
      option: SemanticDataModel.BATCH,
      expected: 'Batch',
    },
    {
      option: SemanticDataModel.SERIALPART,
      expected: 'SerialPart',
    },
    {
      option: SemanticDataModel.PARTASPLANNED,
      expected: 'PartAsPlanned',
    },
    {
      option: SemanticDataModel.UNKNOWN,
      expected: 'Unknown',
    },
    {
      option: SemanticDataModel.JUSTINSEQUENCE,
      expected: 'JustInSequence',
    },
  ].forEach(object => {

    it(`should transform semanticDataModel from ${ object.option } Part to ${ object.expected }`, function() {
      let partData = PartsAssembler.assemblePart(MOCK_part_1, MainAspectType.AS_BUILT);
      partData.semanticDataModel = object.option;

      expect(partData.semanticDataModel).toEqual(object.option);

      let transformedPartData = formatPartSemanticDataModelToCamelCasePipe.transform(partData);

      expect(transformedPartData.semanticDataModel).toEqual(object.expected);
    });
  });

  [
    {
      option: SemanticDataModel.BATCH,
      expected: 'Batch',
    },
    {
      option: SemanticDataModel.SERIALPART,
      expected: 'SerialPart',
    },
    {
      option: SemanticDataModel.PARTASPLANNED,
      expected: 'PartAsPlanned',
    },
    {
      option: SemanticDataModel.JUSTINSEQUENCE,
      expected: 'JustInSequence',
    },
    {
      option: SemanticDataModel.UNKNOWN,
      expected: 'Unknown',
    },
  ].forEach(object => {

    it(`should transform semanticDataModel from ${ object.option } Model to ${ object.expected }`, function() {

      let transformedModel = formatPartSemanticDataModelToCamelCasePipe.transformModel(object.option);

      expect(transformedModel).toEqual(object.expected);
    });
  });


});
