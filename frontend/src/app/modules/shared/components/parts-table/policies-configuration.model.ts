import { TableFilterConfiguration } from '@shared/components/parts-table/parts-config.model';

export class PoliciesConfigurationModel extends TableFilterConfiguration {
  constructor() {
    const sortableColumns = {
      select: false,
      policyId: true,
      validUntil: true,
      menu: false,
    };

    const dateFields = [ 'validUntil' ];
    const singleSearchFields = [];
    super(sortableColumns, dateFields, singleSearchFields, true);
  }
}
