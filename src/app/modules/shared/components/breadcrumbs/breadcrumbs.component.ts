/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

// ToDo: May need to rework this component

import { Component } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { distinctUntilChanged, filter } from 'rxjs/operators';
import { LayoutFacade } from '../../abstraction/layout-facade';
import { BreadCrumbModel } from './breadcrumb.model';

@Component({
  selector: 'app-breadcrumbs',
  templateUrl: './breadcrumbs.component.html',
  styleUrls: ['./breadcrumbs.component.scss'],
})
export class BreadcrumbsComponent {
  public breadcrumbs: BreadCrumbModel[] = [];

  constructor(private router: Router, private activatedRoute: ActivatedRoute, private layoutFacade: LayoutFacade) {
    this.router.events
      .pipe(
        filter(event => event instanceof NavigationEnd),
        distinctUntilChanged(),
      )
      .subscribe({ next: () => (this.breadcrumbs = this.createBreadcrumbs(this.activatedRoute.root)) });
  }

  public navigate(url: string, index: number): void {
    if (index < this.breadcrumbs.length - 1) {
      this.router.navigate([url]).then();
    }
  }

  private createBreadcrumbs(route: ActivatedRoute, url = '', breadcrumbs: BreadCrumbModel[] = []): BreadCrumbModel[] {
    // If no routeConfig is available we are on the root path
    let label = route.routeConfig && route.routeConfig.data ? route.routeConfig.data.breadcrumb : '';
    let path = route.routeConfig && route.routeConfig.data ? route.routeConfig.path : '';

    // If the route is dynamic route such as ':id', remove it
    const lastRoutePart = path.split('/').pop();
    const isDynamicRoute = lastRoutePart.startsWith(':');

    if (isDynamicRoute && !!route.snapshot) {
      const paramName = lastRoutePart.split(':')[1];

      path = path.replace(lastRoutePart, route.snapshot.params[paramName]);
      label = route.snapshot.params[paramName];
    }

    // In the routeConfig the complete path is not available,
    // so we rebuild it each time
    const nextUrl = path ? `${url}/${path}` : url;

    const breadcrumb: BreadCrumbModel = {
      label,
      url: nextUrl,
    };

    // TODO: Don't know if this will work for future implementations
    // We must set the breadcrumb label on the component and if we leave the route, we must put it back to empty
    if (breadcrumb.url && breadcrumb.label === '' && this.layoutFacade.breadcrumbLabel) {
      breadcrumb.label = this.layoutFacade.breadcrumbLabel;
    }

    // translate breadcrumb
    if (breadcrumb.label) {
      // each breadcrumb label should be registered in common translation under "routing" key
      breadcrumb.label = `routing.${breadcrumb.label}`;
    }

    // Only adding route with non-empty label
    const newBreadcrumbs = breadcrumb.label ? [...breadcrumbs, breadcrumb] : [...breadcrumbs];
    if (route.firstChild) {
      // If we are not on our current path yet,
      // there will be more children to look after, to build our breadcrumb
      return this.createBreadcrumbs(route.firstChild, nextUrl, newBreadcrumbs);
    }
    return newBreadcrumbs;
  }
}
