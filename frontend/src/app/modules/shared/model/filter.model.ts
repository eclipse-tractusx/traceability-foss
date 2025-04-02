export type FilterValue = {
  value: string;
  strategy: FilterOperator;
}

export type FilterAttribute = {
  value: FilterValue[]
  operator: string;
}

export interface NotificationFilter {
  id?: FilterAttribute;
  description?: FilterAttribute;
  status?: FilterAttribute;
  severity?: FilterAttribute;
  createdDate?: FilterAttribute;
  createdBy?: FilterAttribute;
  createdByName?: FilterAttribute;
  sendTo?: FilterAttribute;
  sendToName?: FilterAttribute;
  reason?: FilterAttribute;
  assetId?: FilterAttribute;
  channel?: FilterAttribute;
  targetDate?: FilterAttribute;
  bpn?: FilterAttribute;
  errorMessage?: FilterAttribute;
  title?: FilterAttribute;
  contractAgreementId?: FilterAttribute;
  type?: FilterAttribute;
}

export interface AssetAsBuiltFilter {
  id?: FilterAttribute,
  idShort?: FilterAttribute,
  name?: FilterAttribute,
  manufacturerName?: FilterAttribute,
  businessPartner?: FilterAttribute,
  partId?: FilterAttribute,
  manufacturerPartId?: FilterAttribute,
  customerPartId?: FilterAttribute,
  contractAgreementId?: FilterAttribute,
  classification?: FilterAttribute,
  nameAtCustomer?: FilterAttribute,
  semanticModelId?: FilterAttribute,
  semanticDataModel?: FilterAttribute,
  manufacturingDate?: FilterAttribute,
  manufacturingCountry?: FilterAttribute,
  owner?: FilterAttribute
}

export interface AssetAsPlannedFilter {
  id?: FilterAttribute,
  idShort?: FilterAttribute,
  name?: FilterAttribute,
  manufacturer?: FilterAttribute,
  businessPartner?: FilterAttribute,
  manufacturerPartId?: FilterAttribute,
  classification?: FilterAttribute,
  contractAgreementId?: FilterAttribute,
  semanticDataModel?: FilterAttribute,
  semanticModelId?: FilterAttribute,
  validityPeriodFrom?: FilterAttribute,
  validityPeriodTo?: FilterAttribute,
  psFunction?: FilterAttribute,
  catenaXSiteId?: FilterAttribute,
  functionValidFrom?: FilterAttribute,
  functionValidUntil?: FilterAttribute,
  owner?: FilterAttribute
}

export interface ContractFilter {
  created?: FilterAttribute;
  id?: FilterAttribute;
  contractId?: FilterAttribute;
  contractType?: FilterAttribute;
}

export enum FilterOperator {
  EQUAL = 'EQUAL',
  AT_LOCAL_DATE = 'AT_LOCAL_DATE',
  STARTS_WITH = 'STARTS_WITH',
  BEFORE_LOCAL_DATE = 'BEFORE_LOCAL_DATE',
  AFTER_LOCAL_DATE = 'AFTER_LOCAL_DATE',
  NOTIFICATION_COUNT_EQUAL = 'NOTIFICATION_COUNT_EQUAL',
  EXCLUDE = 'EXCLUDE',
  GLOBAL = 'GLOBAL'
}

export function getFilterOperatorValue(operator: FilterOperator) {
  return operator as string;
}
