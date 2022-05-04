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

import { union } from 'lodash-es';
import { Asset } from '../model/asset.model';

/**
 *
 *
 * @export
 * @class AssetAssembler
 */
export class AssetAssembler {
  /**
   * Asset assembler
   * We are fetching the missing components
   * and add them to the child components list
   *
   * @static
   * @param {Asset} asset
   * @return {Asset}
   * @memberof AssetAssembler
   */
  public static assembleAsset(asset: Asset, parent?: Asset, organizations?: string[]): Asset {
    asset.childComponents = this.isUndefined(asset.childComponents) as Asset[];
    asset.componentsSerialNumbers = this.isUndefined(asset.componentsSerialNumbers) as string[];
    const missingChildren: Asset[] = [];

    if (asset.componentsSerialNumbers.length !== asset.childComponents.length) {
      missingChildren.push(...AssetAssembler.fetchEmptyChildren(asset));
    }

    const mergedChildren: Asset[] = union(missingChildren, asset.childComponents);

    const status = {
      OK: 'checkbox-circle-line',
      MINOR: 'error-warning-line',
      MAJOR: 'alert-line',
      CRITICAL: 'spam-line',
      'LIFE-THREATENING': 'close-circle-line',
    };

    if (asset.customFields) {
      asset.customercontractoneid = asset.customFields.customercontractoneid;
      asset.customeroneid = asset.customFields.customeroneid;
      asset.customeruniqueid = asset.customFields.customeruniqueid;
      asset.manufacturercontractoneid = asset.customFields.manufacturercontractoneid;
      asset.manufactureroneid = asset.customFields.manufactureroneid;
      asset.manufactureruniqueid = asset.customFields.manufactureruniqueid;
      asset.partnamecustomer = asset.customFields.partnamecustomer;
      asset.productioncountrycode = asset.customFields.productioncountrycode;
      asset.qualityalert = asset.customFields.qualityalert;
      asset.qualityType = asset.customFields.qualitytype ? asset.customFields.qualitytype : 'OK';
      asset.businesspartnername = asset.customFields.businesspartnername;
      asset.businesspartnerplantname = asset.customFields.businesspartnerplantname;
      asset.customerpartnername = asset.customFields.customerpartnername;
    }

    if (parent) {
      asset.parents = parent.parents;
    }

    if (organizations && asset.customeroneid) {
      asset.isParentKnown = organizations.some(org => asset.customeroneid.includes(org));
    }

    const icon = status[asset.qualityType];

    if (asset.childComponents.length > 0) {
      if (missingChildren.length > 0) {
        asset.partIcon = 'ic-missing-some-parts';
        asset.partsAvailable = 'Missing some parts';
      } else {
        asset.partIcon = 'ic-all-parts-available';
        asset.partsAvailable = 'All child parts available';
      }
    } else {
      asset.partIcon = 'ic-missing-all-parts';
      asset.partsAvailable = 'All child parts are missing';
    }

    const statusIcon = { status: asset.qualityType, icon };

    return {
      ...asset,
      childComponents: mergedChildren,
      icon,
      statusIcon,
    };
  }

  /**
   * Helper method to fetch the components that are missing
   *
   * @private
   * @static
   * @param {Asset} asset
   * @return {Asset[]}
   * @memberof AssetAssembler
   */
  private static fetchEmptyChildren(asset: Asset): Asset[] {
    const arrayOfMissingComponents: string[] = this.findMissingChildren(
      asset.componentsSerialNumbers,
      asset.childComponents,
    );
    const childComponents: Asset[] = [];
    Object.values(arrayOfMissingComponents).forEach((serialNumber: string) => {
      const newAsset = this.assetBuilder(serialNumber);
      childComponents.push(newAsset);
    });

    return childComponents;
  }

  /**
   * Helper method to replace undefined values
   * with empty arrays
   *
   * @private
   * @static
   * @param {(string[] | Asset[])} value
   * @return {(string[] | Asset[])}
   * @memberof AssetAssembler
   */
  private static isUndefined(value: string[] | Asset[]): string[] | Asset[] {
    if (typeof value === 'undefined') {
      value = [];
    }
    return value;
  }

  /**
   * Helper method to find which components are missing
   *
   * @private
   * @static
   * @param {string[]} serialComponents
   * @param {Asset[]} children
   * @return {string[]}
   * @memberof AssetAssembler
   */
  private static findMissingChildren(serialComponents: string[], children: Asset[]): string[] {
    const arrayOfChildren = [];
    children.forEach(value => arrayOfChildren.push(value.serialNumberCustomer));
    return serialComponents.filter(serial => !arrayOfChildren.includes(serial));
  }

  /**
   * Missing asset builder
   *
   * @private
   * @static
   * @param {string} serialNumber
   * @return {Asset}
   * @memberof AssetAssembler
   */
  private static assetBuilder(serialNumber: string): Asset {
    return {
      serialNumberManufacturer: serialNumber,
      qualityStatus: '',
      status: '',
      manufacturer: '',
      productionCountryCodeManufacturer: '',
      partNameManufacturer: 'Missing part',
      partNumberManufacturer: '',
      partNumberCustomer: '',
      serialNumberCustomer: serialNumber,
      productionDateGmt: '',
      mspid: '',
      serialNumberType: '',
      icon: '',
      statusIcon: { status: '', icon: '' },
      partIcon: '',
    };
  }
}
