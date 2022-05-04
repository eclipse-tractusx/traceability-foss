import { HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiService } from '../../core/api/api.service';
import { Asset, AssetResponse } from '../model/asset.model';

/**
 *
 *
 * @export
 * @class AssetService
 */
@Injectable({
  providedIn: 'root',
})
export class AssetService {
  constructor(private apiService: ApiService) {}

  public getAsset(serialNumberCustomer: string): Observable<Asset> {
    return this.apiService
      .getBy<AssetResponse>(
        '/get-asset-detail?',
        new HttpParams().set('serialNumberCustomer', encodeURIComponent(serialNumberCustomer)),
      )
      .pipe(map((asset: AssetResponse) => asset.data));
  }

  public getParent(serialNumberCustomer: string): Observable<Asset> {
    return this.apiService
      .getBy<AssetResponse>(
        '/get-asset-parent?',
        new HttpParams().set('serialNumberCustomer', encodeURIComponent(serialNumberCustomer)),
      )
      .pipe(map((asset: AssetResponse) => asset.data));
  }
}
