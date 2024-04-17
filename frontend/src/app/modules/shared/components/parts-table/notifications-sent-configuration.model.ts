import { TableFilterConfiguration } from '@shared/components/parts-table/parts-config.model';

export class NotificationsSentConfigurationModel extends TableFilterConfiguration {
  constructor() {
    const sortableColumns = {
      description: true,
      title: true,
      status: true,
      createdDate: true,
      severity: true,
      sendTo: true,
      sendToName: true,
      type: true,
      menu: false,
    };

    const dateFields = [ 'createdDate' ];
    const singleSearchFields = [];
    super(sortableColumns, dateFields, singleSearchFields, false);
  }
}
