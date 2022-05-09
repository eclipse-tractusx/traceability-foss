import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';
import { Observable } from 'rxjs';
import { LayoutFacade } from 'src/app/modules/shared/abstraction/layout-facade';
import { Mspid } from 'src/app/modules/shared/model/mspid.model';

@Injectable()
export class MspidsResolver implements Resolve<Mspid[]> {
  constructor(private layoutFacade: LayoutFacade) {}

  resolve(): Mspid[] | Observable<Mspid[]> | Promise<Mspid[]> {
    const mspids = this.layoutFacade.mspidsSnapshot;
    return !mspids.length ? this.layoutFacade.getMspidRequest() : mspids;
  }
}
