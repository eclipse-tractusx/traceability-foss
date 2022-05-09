import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { State } from '../../model/state';

@Injectable()
export class TableState {
  private readonly selectedAsset$: State<string> = new State<string>('');

  get getSelectedAsset$(): Observable<string> {
    return this.selectedAsset$.observable;
  }

  get getSelectedAssetSnapshot(): string {
    return this.selectedAsset$.snapshot;
  }

  public setSelectedAsset(selectedAsset: string): void {
    const previousValue: string = this.selectedAsset$.snapshot;
    if (selectedAsset !== previousValue) {
      this.selectedAsset$.update(selectedAsset);
    }
  }

  public resetSelectedAsset(): void {
    this.selectedAsset$.reset();
  }
}
