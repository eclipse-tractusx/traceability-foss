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


import { SemanticDataModel, SemanticDataModelInCamelCase } from '@page/parts/model/parts.model';
import { FormatPartSemanticDataModelToCamelCasePipe } from '@shared/pipes/format-part-semantic-data-model-to-camelcase.pipe';
interface Part {
  semanticDataModel: string;
  [key: string]: any;
}
describe('FormatPartSemanticDataModelToCamelCasePipe', () => {
  let pipe: FormatPartSemanticDataModelToCamelCasePipe;

  beforeEach(() => {
    pipe = new FormatPartSemanticDataModelToCamelCasePipe();
  });

  it('should create the pipe instance', () => {
    expect(pipe).toBeTruthy();
  });

  describe('transform()', () => {
    it('should convert semanticDataModel to camel case for BATCH', () => {
      const part: Part = { semanticDataModel: SemanticDataModel.BATCH } as Part;
      const result = pipe.transform(part);
      expect(result.semanticDataModel).toBe(SemanticDataModelInCamelCase.BATCH);
    });

    it('should convert semanticDataModel to camel case for SERIALPART', () => {
      const part: Part = { semanticDataModel: 'serialpart' } as Part;
      const result = pipe.transform(part);
      expect(result.semanticDataModel).toBe(SemanticDataModelInCamelCase.SERIALPART);
    });

    it('should convert semanticDataModel to camel case for PARTASPLANNED', () => {
      const part: Part = { semanticDataModel: 'PartAsPlanned' } as Part;
      const result = pipe.transform(part);
      expect(result.semanticDataModel).toBe(SemanticDataModelInCamelCase.PARTASPLANNED);
    });

    it('should convert semanticDataModel to camel case for JUSTINSEQUENCE', () => {
      const part: Part = { semanticDataModel: 'JustInSequence' } as Part;
      const result = pipe.transform(part);
      expect(result.semanticDataModel).toBe(SemanticDataModelInCamelCase.JUSTINSEQUENCE);
    });

    it('should return UNKNOWN for unrecognized model value', () => {
      const part: Part = { semanticDataModel: 'UnmappedModel' } as Part;
      const result = pipe.transform(part);
      expect(result.semanticDataModel).toBe(SemanticDataModelInCamelCase.UNKNOWN);
    });

    it('should preserve all other properties of the part', () => {
      const part: Part = {
        id: '1',
        semanticDataModel: SemanticDataModel.BATCH,
        idShort: 'short',
        manufacturerName: 'BMW',
        manufacturerPartId: 'MPID',
        nameAtManufacturer: 'PartName',
        businessPartner: 'BP',
        semanticModelId: 'modelId',
        qualityType: 'Ok',
        children: [],
        van: 'VAN123',
        owner: 'OEM',
        classification: 'ClassA',
        mainAspectType: 'SomeAspect',
        catenaXSiteId: 'Site1',
        psFunction: 'Function',
        sentActiveAlerts: [],
        sentActiveInvestigations: [],
        receivedActiveAlerts: [],
        receivedActiveInvestigations: []
      } as unknown as Part;

      const result = pipe.transform(part);
      expect(result.id).toBe('1');
      expect(result.manufacturerName).toBe('BMW');
      expect(result.semanticDataModel).toBe(SemanticDataModelInCamelCase.BATCH);
    });
  });

  describe('transformModel()', () => {
    it('should convert SemanticDataModel enums to camelCase equivalents', () => {
      expect(pipe.transformModel(SemanticDataModel.BATCH)).toBe(SemanticDataModelInCamelCase.BATCH);
      expect(pipe.transformModel(SemanticDataModel.SERIALPART)).toBe(SemanticDataModelInCamelCase.SERIALPART);
      expect(pipe.transformModel(SemanticDataModel.PARTASPLANNED)).toBe(SemanticDataModelInCamelCase.PARTASPLANNED);
      expect(pipe.transformModel(SemanticDataModel.JUSTINSEQUENCE)).toBe(SemanticDataModelInCamelCase.JUSTINSEQUENCE);
    });

    it('should return UNKNOWN for undefined or unrecognized semantic data model', () => {
      expect(pipe.transformModel('nonexistent')).toBe(SemanticDataModelInCamelCase.UNKNOWN);
    });
  });
});
