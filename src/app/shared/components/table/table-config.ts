/*
 * Copyright 2021 The PartChain Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 *
 *
 * @export
 * @class TableConfig
 */
export class TableConfig {
  /**
   * @constructor Creates an instance of TableConfig.
   * @param {boolean} [isTableEditable=false]
   * @param {{ emptyStateReason?: string }} [emptyState]
   * @memberof TableConfig
   */
  constructor(private isTableEditable = false, private emptyState?: { emptyStateReason?: string }) {}

  /**
   * Is table read only
   *
   * @readonly
   * @type {boolean}
   * @memberof TableConfig
   */
  get isReadOnly(): boolean {
    return !this.isTableEditable;
  }

  /**
   * Table editable reason getter
   *
   * @type {(string | undefined)}
   * @memberof TableConfig
   */
  get emptyStateReason(): string | undefined {
    if (!this.emptyState) {
      return undefined;
    }
    return this.emptyState.emptyStateReason;
  }

  /**
   * Table options getter
   *
   * @readonly
   * @type {{ emptyStateReason?: string }}
   * @memberof TableConfig
   */
  get options(): { emptyStateReason?: string } {
    if (this.emptyState) {
      return this.emptyState;
    }
    return {};
  }

  /**
   * Table options setter
   *
   * @param {{ emptyStateReason?: string }} value
   * @memberof TableConfig
   */
  set options(value: { emptyStateReason?: string }) {
    this.emptyState = value;
  }
}
