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
import {
  AutocompleteStrategy,
  channelOfNotification,
  isAsBuilt,
} from '@shared/components/multi-select-autocomplete/autocomplete-strategy';
import { NotificationChannel, TableType } from '@shared/components/multi-select-autocomplete/table-type.model';
import { I18NextService } from 'angular-i18next';

describe('AutocompleteStrategy', () => {
  let strategy: AutocompleteStrategy;
  let i18nextServiceSpy: jasmine.SpyObj<I18NextService>;

  class TestAutocompleteStrategy extends AutocompleteStrategy {
    constructor(i18nextService: I18NextService) {
      super(i18nextService);
    }

    retrieveSuggestionValues() {
      return [];
    }
  }

  beforeEach(() => {
    i18nextServiceSpy = jasmine.createSpyObj('I18NextService', [ 't' ]) as jasmine.SpyObj<I18NextService> & {
      t: jasmine.Spy<(key: string, options?: any) => any>;
    };

    strategy = new TestAutocompleteStrategy(i18nextServiceSpy);
  });

  describe('getTranslatedSearchValue', () => {
    it('should return matched original keys from reverse map', () => {
      const mockTranslations = {
        name: 'Name',
        location: 'Location',
        type: 'Type',
      } as any;
      i18nextServiceSpy.t.and.returnValue(mockTranslations);

      const result = strategy.getTranslatedSearchValue('Loc', 'columns');

      expect(result).toEqual([ 'location' ]);
    });

    it('should return ["n/a"] if no match is found', () => {
      const mockTranslations = {
        name: 'Name',
        location: 'Location',
        type: 'Type',
      } as any;
      i18nextServiceSpy.t.and.returnValue(mockTranslations);

      const result = strategy.getTranslatedSearchValue('Unknown', 'columns');

      expect(result).toEqual([ 'n/a' ]);
    });

    it('should return [searchElement] if no valid map is returned', () => {
      i18nextServiceSpy.t.and.returnValue(null);

      const result = strategy.getTranslatedSearchValue('Test', 'columns');

      expect(result).toEqual([ 'Test' ]);
    });
  });

  describe('createReverseLookup', () => {
    it('should return reversed object', () => {
      const original = {
        name: 'Name',
        type: 'Type',
      };

      const reversed = strategy.createReverseLookup(original);

      expect(reversed).toEqual({
        'Name': 'name',
        'Type': 'type',
      });
    });
  });

  describe('findMatchingKeysFromTranslation', () => {
    it('should return matching keys', () => {
      const reverseMap = {
        'Name': 'name',
        'Type': 'type',
        'Location': 'location',
      };

      const result = strategy.findMatchingKeysFromTranslation('loc', reverseMap);

      expect(result).toEqual([ 'location' ]);
    });

    it('should return ["n/a"] if nothing matches', () => {
      const reverseMap = {
        'Name': 'name',
        'Type': 'type',
      };

      const result = strategy.findMatchingKeysFromTranslation('xyz', reverseMap);

      expect(result).toEqual([ 'n/a' ]);
    });
  });
});


describe('Autocomplete Strategies', () => {

  it('should determine isAsBuilt correctly', async () => {
    expect(isAsBuilt(TableType.AS_BUILT_OWN)).toBeTrue();
    expect(isAsBuilt(TableType.AS_PLANNED_OWN)).toBeFalse();
  });

  it('should determine channel of notification', async () => {
    expect(channelOfNotification(TableType.SENT_NOTIFICATION)).toBe(NotificationChannel.SENDER);
    expect(channelOfNotification(TableType.RECEIVED_NOTIFICATION)).toBe(NotificationChannel.RECEIVER);
  });
});
