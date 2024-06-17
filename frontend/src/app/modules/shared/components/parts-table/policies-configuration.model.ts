import { TableFilterConfiguration } from '@shared/components/parts-table/parts-config.model';

export class PoliciesConfigurationModel extends TableFilterConfiguration {
  constructor() {
    const sortableColumns = {
      select: false,
      bpn: false,
      policyName: false,
      policyId: false,
      accessType: false,
      createdOn: false,
      validUntil: false,
      constraints: false,
      menu: false,
    };

    const dateFields = [ 'createdOn', 'validUntil' ];
    const singleSearchFields = [];
    super(sortableColumns, dateFields, singleSearchFields, true);
  }
}
