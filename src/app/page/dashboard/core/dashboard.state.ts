import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { View } from 'src/app/shared/model/view.model';
import { State } from '../../../shared/model/state';

@Injectable()
export class DashboardState {
  private readonly _numberOfParts$: State<View<number>> = new State<View<number>>({ loader: true });

  get numberOfParts$(): Observable<View<number>> {
    return this._numberOfParts$.observable;
  }

  public setNumberOfParts(assets: View<number>): void {
    this._numberOfParts$.update(assets);
  }
}
