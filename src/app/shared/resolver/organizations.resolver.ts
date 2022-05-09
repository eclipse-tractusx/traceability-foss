import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';
import { Observable } from 'rxjs';
import { LayoutFacade } from '../abstraction/layout-facade';

@Injectable()
export class OrganizationsResolver implements Resolve<string[]> {
  constructor(private layoutFacade: LayoutFacade) {}

  resolve(): string[] | Observable<string[]> | Promise<string[]> {
    this.layoutFacade.setOrganizations();
    return this.layoutFacade.organizations$;
  }
}
