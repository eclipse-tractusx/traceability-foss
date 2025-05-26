/********************************************************************************
 * Copyright (c) 2023, 2025 Contributors to the Eclipse Foundation
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
import { SemanticDataModelInCamelCase } from '@page/parts/model/parts.model';
import { FormatPartlistSemanticDataModelToCamelCasePipe } from '@shared/pipes/format-partlist-semantic-data-model-to-camelcase.pipe';


interface Part {
  semanticDataModel: string;
  [key: string]: any;
}

describe('FormatPartlistSemanticDataModelToCamelCasePipe', () => {
  let pipe: FormatPartlistSemanticDataModelToCamelCasePipe;

  beforeEach(() => {
    pipe = new FormatPartlistSemanticDataModelToCamelCasePipe();
  });

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should convert known semanticDataModel values to camelCase equivalents', () => {
    const input: Part[] = [
      { semanticDataModel: 'BATCH' },
      { semanticDataModel: 'serialPart' },
      { semanticDataModel: 'PartAsPlanned' },
      { semanticDataModel: 'justInSequence' },
      { semanticDataModel: 'TombstoneAsBuilt' },
      { semanticDataModel: 'tombstoneasplanned' },
    ];

    const result = pipe.transform(input);

    expect(result[0].semanticDataModel).toBe(SemanticDataModelInCamelCase.BATCH);
    expect(result[1].semanticDataModel).toBe(SemanticDataModelInCamelCase.SERIALPART);
    expect(result[2].semanticDataModel).toBe(SemanticDataModelInCamelCase.PARTASPLANNED);
    expect(result[3].semanticDataModel).toBe(SemanticDataModelInCamelCase.JUSTINSEQUENCE);
    expect(result[4].semanticDataModel).toBe(SemanticDataModelInCamelCase.TOMBSTONEASBUILT);
    expect(result[5].semanticDataModel).toBe(SemanticDataModelInCamelCase.TOMBSTONEASPLANNED);
  });

  it('should convert unknown semanticDataModel values to "unknown"', () => {
    const input: Part[] = [
      { semanticDataModel: 'randomModel' },
      { semanticDataModel: 'anotherUnknown' },
    ];

    const result = pipe.transform(input);

    result.forEach(part => {
      expect(part.semanticDataModel).toBe(SemanticDataModelInCamelCase.UNKNOWN);
    });
  });

  it('should not alter other properties in the part object', () => {
    const input: Part[] = [
      { semanticDataModel: 'batch', id: 1, name: 'Test Part' }
    ];

    const result = pipe.transform(input);

    expect(result[0].id).toBe(1);
    expect(result[0].name).toBe('Test Part');
    expect(result[0].semanticDataModel).toBe(SemanticDataModelInCamelCase.BATCH);
  });

  it('should handle empty input arrays', () => {
    const result = pipe.transform([]);
    expect(result).toEqual([]);
  });
});
