import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TableState } from './table.state';

@Injectable()
export class TableFacade {
  constructor(private tableState: TableState) {}

  get selectedAsset$(): Observable<string> {
    return this.tableState.getSelectedAsset$;
  }

  get selectedAssetSnapshot(): string {
    return this.tableState.getSelectedAssetSnapshot;
  }

  public setSelectedAsset(selectedAsset: string): void {
    this.tableState.setSelectedAsset(selectedAsset);
  }

  public resetSelectedAsset(): void {
    this.tableState.resetSelectedAsset();
  }
}
