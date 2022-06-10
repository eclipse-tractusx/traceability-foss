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

import { environment } from '@env';
import { AssetsPerPlant } from '@page/dashboard/model/assets-per-plant.model';
import { Dashboard, QualityAlertCount, QualityAlertCountByTime } from '@page/dashboard/model/dashboard.model';

const AssetsCountPerCountryAndSupplier: AssetsPerPlant = {
  assetsCount: 100,
  coordinates: [10, 20],
  countryCode: 'DE',
  manufacturer: 'Daimler',
  country: 'Germany',
};

const qualityAlertCount: Record<string, QualityAlertCount[]> = {
  [environment.defaultRealm.toUpperCase()]: [
    {
      qualityType: 'Good',
      alertCount: '0',
      totalAssetsCount: '3',
      totalAlertCount: '0',
    },
  ],
};

const qualityAlertCountByTime: Record<string, QualityAlertCountByTime[]> = {
  [environment.defaultRealm.toUpperCase()]: [
    {
      alertDate: '2022-05-06T06:14:58.148Z',
      qualityType: 'Good',
      alertCount: '0',
      totalAssetsCount: '3',
      totalAlertCount: '0',
    },
  ],
};

export const mockDashboard: Dashboard = {
  assetsCount: 100,
  ownAssetsCount: 50,
  otherAssetsCount: 30,
  qualityAlertTotalCount: 0,
  AssetsCountPerCountryAndSupplier,
  qualityAlertCount,
  qualityAlertCountByTime,
};
