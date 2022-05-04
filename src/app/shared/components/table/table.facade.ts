import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TableState } from './table.state';

/**
 *
 *
 * @export
 * @class TableFacade
 */
@Injectable()
export class TableFacade {
  /**
   * @constructor TableFacade
   * @param {TableState} tableState
   * @memberof TableFacade
   */
  constructor(private tableState: TableState) {}

  /**
   * Selected asset state getter
   *
   * @readonly
   * @type {Observable<string>}
   * @memberof TableFacade
   */
  get selectedAsset$(): Observable<string> {
    return this.tableState.getSelectedAsset$;
  }

  /**
   * Selected asset snapshot getter
   *
   * @readonly
   * @type {string}
   * @memberof TableFacade
   */
  get selectedAssetSnapshot(): string {
    return this.tableState.getSelectedAssetSnapshot;
  }

  /**
   * Selected asset state setter
   *
   * @param {string} selectedAsset
   * @return {void}
   * @memberof TableFacade
   */
  public setSelectedAsset(selectedAsset: string): void {
    this.tableState.setSelectedAsset(selectedAsset);
  }

  /**
   * Selected asset state reset
   *
   * @return {void}
   * @memberof TableFacade
   */
  public resetSelectedAsset(): void {
    this.tableState.resetSelectedAsset();
  }
}
