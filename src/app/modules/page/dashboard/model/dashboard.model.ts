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
  mspid: string;
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
