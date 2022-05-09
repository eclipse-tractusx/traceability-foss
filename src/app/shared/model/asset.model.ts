export interface CustomField {
  field: string;
  value: string;
}

export interface Asset {
  manufacturer: string;
  productionCountryCodeManufacturer: string;
  partNameManufacturer: string;
  partNumberManufacturer?: string;
  partNumberCustomer?: string;
  serialNumberManufacturer: string;
  serialNumberCustomer?: string;
  qualityStatus: string;
  status: string;
  productionDateGmt: string;
  childComponents?: Asset[];
  componentsSerialNumbers?: string[];
  icon?: string;
  partIcon?: string;
  statusIcon?: { status: string; icon: string };
  partsAvailable?: string;
  parents?: Asset[];
  isParentKnown?: boolean;
  mspid: string;
  manufacturerLine?: string;
  manufacturerPlant?: string;
  serialNumberType: string;
  parentSerialNumberManufacturer?: string;
  isAffected?: string;
  qualityType?: string;
  customercontractoneid?: string;
  customeroneid?: string;
  customeruniqueid?: string;
  manufacturercontractoneid?: string;
  manufactureroneid?: string;
  manufactureruniqueid?: string;
  partnamecustomer?: string;
  productioncountrycode?: string;
  qualityalert?: string;
  businesspartnername?: string;
  businesspartnerplantname?: string;
  customerpartnername?: string;
  customFields?: Record<string, string>;
}

export interface AssetResponse {
  data: Asset;
  status: number;
}
