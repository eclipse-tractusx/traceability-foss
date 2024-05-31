import { TableFilterConfiguration } from '@shared/components/parts-table/parts-config.model';

export class PoliciesConfigurationModel extends TableFilterConfiguration {
  constructor() {
    const sortableColumns = {
      select: false,
      bpn: true,
      policyName: true,
      policyId: true,
      accessType: true,
      createdOn: true,
      validUntil: true,
      constraints: true,
      menu: false,
    };

    const dateFields = [ 'createdOn', 'validUntil' ];
    const singleSearchFields = [];
    super(sortableColumns, dateFields, singleSearchFields, true);
  }
}
