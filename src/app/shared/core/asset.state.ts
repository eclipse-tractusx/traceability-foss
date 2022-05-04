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

import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { State } from 'src/app/shared/model/state';
import { View } from 'src/app/shared/model/view.model';
import { AssetAssembler } from './asset.assembler';
import { Asset } from '../model/asset.model';

/**
 *
 *
 * @export
 * @class AssetState
 */
@Injectable({
  providedIn: 'root',
})
export class AssetState {
  /**
   * Asset state
   *
   * @private
   * @readonly
   * @type {State<View<Asset>>}
   * @memberof AssetState
   */
  private readonly asset$: State<View<Asset>> = new State<View<Asset>>(undefined);

  /**
   * Asset state getter
   *
   * @readonly
   * @type {Observable<View<Asset>>}
   * @memberof AssetState
   */
  get getAsset$(): Observable<View<Asset>> {
    return this.asset$.observable;
  }

  /**
   * Asset state setter
   *
   * @param {View<Asset>} asset
   * @param {Transaction[]} [assetTransactions]
   * @return {void}
   * @memberof AssetState
   */
  public setAsset(asset: View<Asset>, parent?: Asset, organizations?: string[]): void {
    const newAsset: View<Asset> = {
      data: asset.data && AssetAssembler.assembleAsset(asset.data, parent, organizations),
      loader: asset.loader,
      error: asset.error,
    };

    this.asset$.update(newAsset);
  }

  /**
   * Reset asset state
   *
   * @return {void}
   * @memberof AssetState
   */
  public resetAsset(): void {
    this.asset$.reset();
  }
}
