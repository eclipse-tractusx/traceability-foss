import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  CanActivateChild,
  CanDeactivate,
  CanLoad,
  Router,
  RouterStateSnapshot,
  UrlTree,
} from '@angular/router';
import { Observable } from 'rxjs';
import { realm } from '../api/api.service.properties';
import { UserService } from './user.service';

/**
 *
 *
 * @export
 * @class RoleGuard
 * @implements {CanActivate}
 * @implements {CanActivateChild}
 * @implements {CanDeactivate<unknown>}
 * @implements {CanLoad}
 */
@Injectable({
  providedIn: 'root',
})
export class RoleGuard implements CanActivate, CanActivateChild, CanDeactivate<unknown>, CanLoad {
  constructor(private userService: UserService, private router: Router) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot,
  ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    const url: string = state.url;
    return this.validateUserRole(next, url);
  }

  canActivateChild(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot,
  ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return this.canActivate(next, state);
  }

  canDeactivate(): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return true;
  }

  canLoad(): Observable<boolean> | Promise<boolean> | boolean {
    return true;
  }

  validateUserRole(route: ActivatedRouteSnapshot, url: string): boolean {
    const roles: string[] = this.userService.getRoles();
    const hasSomeRole: boolean = route.data.role.some((role: string) => roles.includes(role));
    if (route.data.role && !hasSomeRole) {
      void this.router.navigate([realm]);
      return false;
    }
    return true;
  }
}
