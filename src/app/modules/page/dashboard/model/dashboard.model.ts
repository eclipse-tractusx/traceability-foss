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

import { AssetsPerPlant } from './assets-per-plant.model';

export type HistogramType = {
  date: string;
  total: number;
  MINOR: number;
  MAJOR: number;
  CRITICAL: number;
  'LIFE-THREATENING': number;
  month: number;
  year: number;
  week: number;
};

export interface ReceivedAlertType {
  type: string;
  total: number;
  color: string;
}

export interface QualityAlertCount {
  qualityType: string;
  alertCount: string;
  totalAssetsCount: string;
  totalAlertCount: string;
}

export interface QualityAlertCountByTime {
  alertDate: string;
  qualityType: string;
  alertCount: string;
  totalAssetsCount: string;
  totalAlertCount: string;
}

export interface Dashboard {
  AssetsCountPerCountryAndSupplier: AssetsPerPlant;
  assetsCount: number;
  ownAssetsCount: number;
  otherAssetsCount: number;
  qualityAlertCount: Record<string, QualityAlertCount[]>;
  qualityAlertCountByTime: Record<string, QualityAlertCountByTime[]>;
  qualityAlertTotalCount: number;
}
