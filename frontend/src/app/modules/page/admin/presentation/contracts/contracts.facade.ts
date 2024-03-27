import { Injectable } from '@angular/core';
import { Pagination } from '@core/model/pagination.model';
import { Contract } from '@page/admin/core/admin.model';
import { AdminService } from '@page/admin/core/admin.service';
import { ContractsState } from '@page/admin/presentation/contracts/contracts.state';
import { provideDataObject } from '@page/parts/core/parts.helper';
import { TableHeaderSort } from '@shared/components/table/table.model';
import { View } from '@shared/model/view.model';
import { Observable, Subject, Subscription } from 'rxjs';

@Injectable()
export class ContractsFacade {
  private contractsSubscription: Subscription;
  private selectedContractSubscription: Subscription;
  private readonly unsubscribeTrigger = new Subject<void>();


  constructor(private readonly adminService: AdminService,
              private readonly contractsState: ContractsState
  ) {}

  get contracts$(): Observable<View<Pagination<Contract>>> {
    return this.contractsState.contracts$
  }

  public setContracts(page, pageSize = 50, sorting: TableHeaderSort[]): void {
    this.contractsSubscription?.unsubscribe();
    this.contractsSubscription = this.adminService.getContracts(page,pageSize,sorting).subscribe({
      next: data => (this.contractsState.contracts = { data: provideDataObject(data) }),
      error: error => (this.contractsState.contracts = { error})
    })
  }

  get selectedContract$(): Observable<View<Contract>> {
    return this.contractsState.selectedContract$;
  }

  get selectedContract(): Contract {
    return this.contractsState.selectedContract?.data;
  }


  public set selectedContract(contract: Contract) {
    this.contractsState.selectedContract = {data: contract};
  }

  public setSelectedContractById(contractId: string): void {
    this.selectedContractSubscription = this.adminService.getContracts(0, 10, [null,null], {contractId: [contractId]}).subscribe({
      next: data => (this.contractsState.selectedContract = { data: data.content[0] as unknown as Contract}),
      error: error => (this.contractsState.selectedContract = {error})
    })
  }


  public unsubscribeContracts(): void {
    this.contractsSubscription?.unsubscribe();
    this.selectedContractSubscription?.unsubscribe();
    this.unsubscribeTrigger.next();
  }

}
