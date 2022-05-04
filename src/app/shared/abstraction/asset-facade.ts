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
import { forkJoin, Observable } from 'rxjs';
import { delay } from 'rxjs/operators';
import { SharedService } from 'src/app/shared/core/shared.service';
import { View } from 'src/app/shared/model/view.model';
import { Asset } from '../model/asset.model';
import { AssetService } from '../core/asset.service';
import { AssetState } from '../core/asset.state';
import { NotificationService } from '../components/notifications/notification.service';
import { LayoutFacade } from './layout-facade';
import { ClipboardService } from 'ngx-clipboard';
import { realm } from 'src/app/core/api/api.service.properties';
import { MatDialog } from '@angular/material/dialog';

/**
 *
 *
 * @export
 * @class AssetFacade
 */
@Injectable({
  providedIn: 'root',
})
export class AssetFacade {
  /**
   * @constructor AssetFacade (DI)
   * @param {AssetState} assetState
   * @param {AssetService} assetService
   * @param {SharedService} sharedService
   * @param {NotificationService} notificationService
   * @param {LayoutFacade} layoutFacade
   * @param {ClipboardService} clipboardService
   * @param {MatDialog} dialog
   * @memberof AssetFacade
   */
  constructor(
    private assetState: AssetState,
    private assetService: AssetService,
    private sharedService: SharedService,
    private notificationService: NotificationService,
    private layoutFacade: LayoutFacade,
    private clipboardService: ClipboardService,
    private dialog: MatDialog,
  ) {}

  /**
   * Asset state getter
   *
   * @readonly
   * @type {Observable<View<Asset>>}
   * @memberof AssetFacade
   */
  get asset$(): Observable<View<Asset>> {
    return this.assetState.getAsset$.pipe(delay(0));
  }

  /**
   * Is object empty
   *
   * @param {unknown} object
   * @return {boolean}
   * @memberof AssetFacade
   */
  public isEmpty(object: unknown): boolean {
    return this.sharedService.isEmpty(object);
  }

  /**
   * Asset setter
   *
   * @param {string} serialNumberCustomer
   * @return {void}
   * @memberof AssetFacade
   */
  public setAsset(serialNumberCustomer: string): void {
    this.assetState.setAsset({ loader: true });
    forkJoin([
      this.assetService.getAsset(serialNumberCustomer),
      this.assetService.getParent(serialNumberCustomer),
    ]).subscribe(
      (assetWithOrgs: [Asset, Asset]) => {
        const [asset, parent] = assetWithOrgs;
        const organizations = this.layoutFacade.organizationsSnapshot || [];

        !this.sharedService.isEmpty(asset)
          ? this.assetState.setAsset({ data: asset }, parent, organizations)
          : this.assetState.setAsset({ error: new Error('Part not found') });
      },
      error => this.assetState.setAsset({ error }),
    );
  }

  /**
   * Get asset detail
   *
   * @param {string} serialNumberCustomer
   * @return {Observable<Asset>}
   * @memberof AssetFacade
   */
  public getAsset(serialNumberCustomer: string): Observable<Asset> {
    return this.assetService.getAsset(serialNumberCustomer);
  }

  /**
   * Reset asset state
   *
   * @return {void}
   * @memberof AssetFacade
   */
  public resetAsset(): void {
    this.assetState.resetAsset();
  }

  /**
   * Shorten serial number helper method
   *
   * @param {string} serialNumber
   * @return {string}
   * @memberof AssetFacade
   */
  public shortenSerialNumber(serialNumber: string): string {
    if (serialNumber) {
      return serialNumber.length > 23
        ? `${serialNumber.substring(0, 10)} ... ${serialNumber.substring(serialNumber.length - 10)}`
        : serialNumber;
    }
  }

  /**
   * Copy to clipboard helper method
   *
   * @param {string} serialNumber
   * @return {void}
   * @memberof AssetFacade
   */
  public copyToClipboard(serialNumber: string): void {
    this.clipboardService.copyFromContent(serialNumber);
    this.notificationService.success('Copied to clipboard: ' + this.shortenSerialNumber(serialNumber));
  }

  /**
   * Error notification helper method
   *
   * @param {string} notification
   * @return {void}
   * @memberof AssetFacade
   */
  public errorNotification(notification: string): void {
    this.notificationService.error(notification);
  }

  /**
   * MSPID Comparison
   *
   * @param {Asset} asset
   * @return {boolean}
   * @memberof AssetFacade
   */
  public isMspidSameAsLoggedOne(asset: Asset): boolean {
    return realm[1] === asset.mspid.toLocaleLowerCase();
  }
}
