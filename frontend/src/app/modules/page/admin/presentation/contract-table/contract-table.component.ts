import { Component } from '@angular/core';
import { Pagination } from '@core/model/pagination.model';
import { AdminFacade } from '@page/admin/core/admin.facade';
import { Contract } from '@page/admin/core/admin.model';
import { TableType } from '@shared/components/multi-select-autocomplete/table-type.model';
import { CreateHeaderFromColumns, TableConfig, TableEventConfig } from '@shared/components/table/table.model';
import { NotificationAction } from '@shared/modules/notification/notification-action.enum';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-contract-table',
  templateUrl: './contract-table.component.html',
  styleUrls: [ './contract-table.component.scss' ],
})
export class ContractTableComponent {
  contracts: Observable<Pagination<Contract>>;
  tableConfig: TableConfig;
  selectedContracts: Contract[];
  contractFilter: any;
  pagination: TableEventConfig;

  constructor(private adminFacade: AdminFacade) {
    this.pagination = { page: 0, pageSize: 10, sorting: null };
    this.tableConfig = {
      displayedColumns: [ 'select', 'contractId', 'counterpartyAddress', 'creationDate', 'endDate', 'state' ],
      header: CreateHeaderFromColumns([ 'contractId', 'counterpartyAddress', 'creationDate', 'endDate', 'state' ], 'pageAdmin.contracts'),
      menuActionsConfig: [],
      sortableColumns: {
        select: false,
        contractId: true,
        counterpartyAddress: false,
        creationDate: false,
        endDate: false,
        state: false,
      },
      hasPagination: true,
    };

  }

  public ngOnInit() {
    this.contracts = this.adminFacade.getContracts(0, 10);
  }

  filterActivated(contractFilter: any): void {
    console.log(contractFilter);
    this.contracts = this.adminFacade.getContracts(0, 10, null, contractFilter);
    this.contractFilter = contractFilter;
  }

  public onTableConfigChange(pagination: TableEventConfig): void {
    console.log(pagination);
    this.pagination = pagination;
    this.adminFacade.getContracts(pagination.page, pagination.pageSize, [ pagination.sorting ], this.contractFilter);
  }
  multiSelection(selectedContracts: Contract[]) {
    this.selectedContracts = selectedContracts;
    console.log(this.selectedContracts);
  }

  exportContractsAsCSV() {
    const csvContent = this.convertArrayOfObjectsToCSV(this.selectedContracts);
    this.downloadCSV(csvContent, 'Exported_Contracts.csv');
  }

  convertArrayOfObjectsToCSV(data: any[]) {
    const csvArray = [];
    const headers = Object.keys(data[0]);
    csvArray.push(headers.join(','));

    data.forEach(item => {
      const values = headers.map(header => item[header]);
      csvArray.push(values.join(','));
    });

    return csvArray.join('\n');
  }

  downloadCSV(csvContent: string, fileName: string) {
    const blob = new Blob([ csvContent ], { type: 'text/csv;charset=utf-8;' });

    const link = document.createElement('a');
    if (link.download !== undefined) {
      const url = URL.createObjectURL(blob);
      link.setAttribute('href', url);
      link.setAttribute('download', fileName);
      link.style.visibility = 'hidden';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    }

  }


  protected readonly NotificationAction = NotificationAction;
  protected readonly TableType = TableType;
}
