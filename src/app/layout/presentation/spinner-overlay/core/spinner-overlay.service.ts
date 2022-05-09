import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { State } from 'src/app/shared/model/state';

@Injectable({
  providedIn: 'root',
})
export class SpinnerOverlayService {
  private _isOverlayShowing$: State<boolean> = new State<boolean>(false);

  get isOverlayShowing$(): Observable<boolean> {
    return this._isOverlayShowing$.observable;
  }

  set isOverlayShowing(isShowing: boolean) {
    this._isOverlayShowing$.update(isShowing);
  }
}
