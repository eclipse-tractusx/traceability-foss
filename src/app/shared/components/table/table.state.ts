import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { State } from '../../model/state';

/**
 *
 *
 * @export
 * @class TableState
 */
@Injectable()
export class TableState {
  /**
   * Selected asset state
   *
   * @private
   * @type {State<string>}
   * @memberof TableState
   */
  private readonly selectedAsset$: State<string> = new State<string>('');

  /**
   * Selected asset state getter
   *
   * @readonly
   * @type {Observable<string>}
   * @memberof TableState
   */
  get getSelectedAsset$(): Observable<string> {
    return this.selectedAsset$.observable;
  }

  /**
   * Selected asset snapshot getter
   *
   * @readonly
   * @type {string}
   * @memberof TableState
   */
  get getSelectedAssetSnapshot(): string {
    return this.selectedAsset$.snapshot;
  }

  /**
   * Selected asset state setter
   *
   * @param {string} selectedAsset
   * @return {void}
   * @memberof TableState
   */
  public setSelectedAsset(selectedAsset: string): void {
    const previousValue: string = this.selectedAsset$.snapshot;
    if (selectedAsset !== previousValue) {
      this.selectedAsset$.update(selectedAsset);
    }
  }

  /**
   * Selected asset state reset
   *
   * @return {void}
   * @memberof TableState
   */
  public resetSelectedAsset(): void {
    this.selectedAsset$.reset();
  }
}
