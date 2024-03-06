import { Component } from '@angular/core';
import { Pagination } from '@core/model/pagination.model';
import { AdminFacade } from '@page/admin/core/admin.facade';
import { Contract } from '@page/admin/core/admin.model';
import { CreateHeaderFromColumns, TableConfig } from '@shared/components/table/table.model';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-contract-table',
  templateUrl: './contract-table.component.html',
  styleUrls: [ './contract-table.component.scss' ],
})
export class ContractTableComponent {
  contracts: Observable<Pagination<Contract>>;
  tableConfig: TableConfig

  constructor(private adminFacade: AdminFacade) {
    this.tableConfig = {
      displayedColumns: ["contractId", "counterpartyAddress", "creationDate", "endDate", "state"],
      header: CreateHeaderFromColumns(["contractId", "counterpartyAddress", "creationDate", "endDate", "state"], "pageAdmin.contracts"),
      menuActionsConfig: [],
      sortableColumns: {contractId: true},
      hasPagination: true
    }

  }

  public ngOnInit() {
    this.contracts = this.adminFacade.getContracts(0,10);

    //this.contracts.subscribe(next => console.log(next))
  }

  ngAfterViewInit() {

  }

  exportContractsAsCsv() {

  }
}
