import { Injectable } from '@angular/core';
import { Pagination } from '@core/model/pagination.model';
import { Policy } from '@page/policies/model/policy.model';
import { State } from '@shared/model/state';
import { View } from '@shared/model/view.model';
import { Observable } from 'rxjs';

@Injectable()
export class PoliciesState {
  private readonly _policies$ = new State<View<Pagination<Policy>>>({ loader: true });
  private readonly _selectedPolicy$: State<View<Policy>> = new State<View<Policy>>({ loader: true });

  public get policies$(): Observable<View<Pagination<Policy>>> {
    return this._policies$.observable;
  }

  public set policies({ data, loader, error }: View<Pagination<Policy>>) {
    const policiesView: View<Pagination<Policy>> = { data, loader, error };
    this._policies$.update(policiesView);
  }

  get selectedPolicy$(): Observable<View<Policy>> {
    return this._selectedPolicy$.observable;
  }

  set selectedPolicy({ data, loader, error }: View<Policy>) {
    const selectedPolicyView: View<Policy> = { data, loader, error };
    this._selectedPolicy$.update(selectedPolicyView);
  }

  get selectedPolicy(): View<Policy> {
    return this._selectedPolicy$.snapshot;
  }

}
