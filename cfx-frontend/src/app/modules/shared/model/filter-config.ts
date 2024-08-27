import { SemanticDataModel } from '@page/parts/model/parts.model';
import { NotificationStatus } from './notification.model';
import { Severity } from './severity.model';

export class FilterConfigOptions {
  private optionTextSearch = [];
  private severityOptions = [
    {
      display: 'severity.' + Severity.MINOR,
      value: Severity.MINOR,
      checked: false,
      severity: Severity.MINOR,
      iconURL: './assets/images/icons/info.svg',
    },
    {
      display: 'severity.' + Severity.MAJOR,
      value: Severity.MAJOR,
      checked: false,
      severity: Severity.MAJOR,
      iconURL: './assets/images/icons/warning.svg',
    },
    {
      display: 'severity.' + Severity.CRITICAL,
      value: Severity.CRITICAL,
      checked: false,
      severity: Severity.CRITICAL,
      iconURL: './assets/images/icons/error_outline.svg',
    },
    {
      display: 'severity.' + Severity.LIFE_THREATENING,
      value: 'LIFE_THREATENING',
      checked: false,
      severity: Severity.LIFE_THREATENING,
      iconURL: './assets/images/icons/error.svg',
    },
  ];
  //Approved and Requested only exist in the frontend commonInvestigation
  private statusOptions(type: string, isReceivedTable: boolean): any[] {
    const returnList = [];
    const accepted = {
      display: type + '.status.' + NotificationStatus.ACCEPTED,
      value: NotificationStatus.ACCEPTED,
      checked: false,
      displayClass: 'notification-display-status--ACCEPTED',
    };
    const acknowledged = {
      display: type + '.status.' + NotificationStatus.ACKNOWLEDGED,
      value: NotificationStatus.ACKNOWLEDGED,
      checked: false,
      displayClass: 'notification-display-status--ACKNOWLEDGED',
    };
    const canceled = {
      display: type + '.status.' + NotificationStatus.CANCELED,
      value: NotificationStatus.CANCELED,
      checked: false,
      displayClass: 'notification-display-status--CANCELED',
    };
    const closed = {
      display: type + '.status.' + NotificationStatus.CLOSED,
      value: NotificationStatus.CLOSED,
      checked: false,
      displayClass: 'notification-display-status--CLOSED',
    };
    const created = {
      display: type + '.status.' + NotificationStatus.CREATED,
      value: NotificationStatus.CREATED,
      checked: false,
      displayClass: 'notification-display-status--CREATED',
    };
    const declined = {
      display: type + '.status.' + NotificationStatus.DECLINED,
      value: NotificationStatus.DECLINED,
      checked: false,
      displayClass: 'notification-display-status--DECLINED',
    };
    const received = {
      display: type + '.status.' + NotificationStatus.RECEIVED,
      value: NotificationStatus.RECEIVED,
      checked: false,
      displayClass: 'notification-display-status--RECEIVED',
    };
    const sent = {
      display: type + '.status.' + NotificationStatus.SENT,
      value: NotificationStatus.SENT,
      checked: false,
      displayClass: 'notification-display-status--SENT',
    };

    if (isReceivedTable) {
      returnList.push(received, acknowledged, accepted, declined, closed, canceled);
    } else {
      returnList.push(created, sent, acknowledged, accepted, declined, closed, canceled);
    }
    return returnList;
  }
  private semanticDataModelOptions = [
    {
      display: 'semanticDataModels.' + SemanticDataModel.BATCH,
      value: SemanticDataModel.BATCH,
      checked: false,
    },
    {
      display: 'semanticDataModels.' + SemanticDataModel.JUSTINSEQUENCE,
      value: SemanticDataModel.JUSTINSEQUENCE,
      checked: false,
    },
    {
      display: 'semanticDataModels.' + SemanticDataModel.SERIALPART,
      value: SemanticDataModel.SERIALPART,
      checked: false,
    },
    {
      display: 'semanticDataModels.' + SemanticDataModel.UNKNOWN,
      value: SemanticDataModel.UNKNOWN,
      checked: false,
    },
    {
      display: 'semanticDataModels.' + SemanticDataModel.PARTASPLANNED,
      value: SemanticDataModel.PARTASPLANNED,
      checked: false,
    },
  ];

  public readonly filterKeyOptionsNotifications = {
    createdDate: {
      filterKey: 'createdDate',
      isTextSearch: false,
      isDate: true,
      option: this.optionTextSearch,
      maxDate: new Date(),
    },
    description: { filterKey: 'description', isTextSearch: true, option: this.optionTextSearch },
    status: (typeTranslationPath: string, isReceivedTable: boolean) => ({
      filterKey: 'status',
      isTextSearch: false,
      option: this.statusOptions(typeTranslationPath, isReceivedTable),
    }),
    severity: { filterKey: 'severity', isTextSearch: false, option: this.severityOptions },
    targetDate: {
      filterKey: 'targetDate',
      isTextSearch: false,
      isDate: true,
      option: this.optionTextSearch,
      maxDate: null,
    },
    createdBy: { filterKey: 'createdBy', isTextSearch: true, option: this.optionTextSearch },
    sendTo: { filterKey: 'sendTo', isTextSearch: true, option: this.optionTextSearch },
  };

  public readonly filterKeyOptionsAssets = {
    filter: { filterKey: 'Filter', headerKey: 'Filter', isTextSearch: true, option: this.optionTextSearch },
    id: { filterKey: 'id', headerKey: 'filterId', isTextSearch: true, option: this.optionTextSearch },
    idShort: { filterKey: 'idShort', headerKey: 'filterIdShort', isTextSearch: true, option: this.optionTextSearch },
    nameAtManufacturer: {
      filterKey: 'nameAtManufacturer',
      headerKey: 'filterName',
      isTextSearch: true,
      option: this.optionTextSearch,
      column: 'name',
    },
    manufacturerName: {
      filterKey: 'manufacturerName',
      headerKey: 'filterManufacturer',
      isTextSearch: true,
      option: this.optionTextSearch,
      column: 'manufacturer',
    },
    manufacturerPartId: {
      filterKey: 'manufacturerPartId',
      headerKey: 'filterManufacturerPartId',
      isTextSearch: true,
      option: this.optionTextSearch,
    },
    customerPartId: {
      filterKey: 'customerPartId',
      headerKey: 'filterCustomerPartId',
      isTextSearch: true,
      option: this.optionTextSearch,
    }, // --> semanticModel.customerPartId
    classification: {
      filterKey: 'classification',
      headerKey: 'filterClassification',
      isTextSearch: true,
      option: this.optionTextSearch,
    },
    nameAtCustomer: {
      filterKey: 'nameAtCustomer',
      headerKey: 'filterNameAtCustomer',
      isTextSearch: true,
      option: this.optionTextSearch,
    }, // --> semanticModel.nameAtCustomer
    semanticModelId: {
      filterKey: 'semanticModelId',
      headerKey: 'filterSemanticModelId',
      isTextSearch: true,
      option: this.optionTextSearch,
    },
    semanticDataModel: {
      filterKey: 'semanticDataModel',
      headerKey: 'filterSemanticDataModel',
      isTextSearch: false,
      option: this.semanticDataModelOptions,
    },
    manufacturingDate: {
      filterKey: 'manufacturingDate',
      headerKey: 'filterManufacturingDate',
      isTextSearch: false,
      isDate: true,
      maxDate: new Date(),
      option: this.optionTextSearch,
    },
    manufacturingCountry: {
      filterKey: 'manufacturingCountry',
      headerKey: 'filterManufacturingCountry',
      isTextSearch: true,
      option: this.optionTextSearch,
    },
    activeAlerts: {
      filterKey: 'qualityAlertsInStatusActive',
      headerKey: 'filterActiveAlerts',
      isTextSearch: true,
      option: this.optionTextSearch,
      column: 'activeAlerts',
    },
    activeInvestigations: {
      filterKey: 'qualityInvestigationsInStatusActive',
      headerKey: 'filterActiveInvestigations',
      isTextSearch: true,
      option: this.optionTextSearch,
      column: 'activeInvestigations',
    },
    validityPeriodFrom: {
      filterKey: 'validityPeriodFrom',
      headerKey: 'filterValidityPeriodFrom',
      isTextSearch: false,
      isDate: true,
      option: this.optionTextSearch,
      maxDate: new Date(),
    },
    validityPeriodTo: {
      filterKey: 'validityPeriodTo',
      headerKey: 'filterValidityPeriodTo',
      isTextSearch: false,
      isDate: true,
      option: this.optionTextSearch,
      maxDate: null,
    },
    psFunction: {
      filterKey: 'function',
      headerKey: 'filterPsFunction',
      isTextSearch: true,
      option: this.optionTextSearch,
      column: 'psFunction',
    },
    catenaXSiteId: {
      filterKey: 'catenaXSiteId',
      headerKey: 'filterCatenaXSiteId',
      isTextSearch: true,
      option: this.optionTextSearch,
    },
    functionValidFrom: {
      filterKey: 'functionValidFrom',
      headerKey: 'filterFunctionValidFrom',
      isTextSearch: false,
      isDate: true,
      option: this.optionTextSearch,
      maxDate: new Date(),
    },
    functionValidUntil: {
      filterKey: 'functionValidUntil',
      headerKey: 'filterFunctionValidUntil',
      isTextSearch: false,
      isDate: true,
      option: this.optionTextSearch,
      maxDate: null,
    },
  };
}
