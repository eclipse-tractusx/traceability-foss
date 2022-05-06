import { AssetsPerPlant } from '../../../app/page/dashboard/model/assets-per-plant.model';
import {
  Dashboard,
  QualityAlertCount,
  QualityAlertCountByTime,
} from '../../../app/page/dashboard/model/dashboard.model';

const AssetsCountPerCountryAndSupplier: AssetsPerPlant = {
  assetsCount: 100,
  coordinates: [10, 20],
  countryCode: 'DE',
  manufacturer: 'Daimler',
  country: 'Germany',
};

const qualityAlertCount: Record<string, QualityAlertCount[]> = {
  MOCK: [
    {
      mspid: 'MOCK',
      qualityType: 'Good',
      alertCount: '0',
      totalAssetsCount: '10',
      totalAlertCount: '0',
    },
  ],
};

const qualityAlertCountByTime: Record<string, QualityAlertCountByTime[]> = {
  MOCK: [
    {
      alertDate: '2022-05-06T06:14:58.148Z',
      qualityType: 'Good',
      alertCount: '0',
      totalAssetsCount: '10',
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
