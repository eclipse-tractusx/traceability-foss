import { TableFilterConfiguration } from '@shared/components/parts-table/parts-config.model';

export class NotificationsReceivedConfigurationModel extends TableFilterConfiguration {
  constructor() {
    const sortableColumns = {
      select: false,
      createdBy: true,
      createdByName: true,
      createdDate: true,
      description: true,
      severity: true,
      status: true,
      title: true,
      type: true,
      menu: false,
    };

    const dateFields = [ 'createdDate' ];
    const singleSearchFields = [];
    super(sortableColumns, dateFields, singleSearchFields, true);
  }
}
