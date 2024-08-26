import { Injectable } from '@angular/core';
import { Pagination } from '@core/model/pagination.model';
import { Contract } from '@page/admin/core/admin.model';
import { State } from '@shared/model/state';
import { View } from '@shared/model/view.model';
import { Observable } from 'rxjs';

@Injectable()
export class ContractsState {
  private readonly _contracts$ = new State<View<Pagination<Contract>>>({loader: true})
  private readonly _selectedContract$: State<View<Contract>> = new State<View<Contract>>( { loader: true});

  public get contracts$(): Observable<View<Pagination<Contract>>> {
    return this._contracts$.observable;
  }

  public set contracts({data, loader, error}: View<Pagination<Contract>>) {
    const contractsView: View<Pagination<Contract>> = {data, loader, error};
    this._contracts$.update(contractsView);
  }

  get selectedContract$(): Observable<View<Contract>> {
    return this._selectedContract$.observable;
  }

  set selectedContract({data, loader, error}: View<Contract>) {
    const selectedContractView: View<Contract> = { data, loader, error};
    this._selectedContract$.update(selectedContractView);
  }

  get selectedContract(): View<Contract> {
    return this._selectedContract$.snapshot;
  }

}
