import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PageNotFoundComponent } from '../page/page-not-found/presentation/page-not-found.component';
import { realm } from './api/api.service.properties';
import { AuthGuard } from './auth/auth.guard';
import { PrivateLayoutComponent } from './layout/private-layout/private-layout.component';

export /** @type {*} */
const routes: Routes = [
  {
    path: realm !== null ? realm : '',
    component: PrivateLayoutComponent,
    canActivate: [AuthGuard],
    data: { breadcrumb: 'Home' },
    loadChildren: () => import('./layout/layout.module').then(m => m.LayoutModule),
  },
  {
    path: '**',
    component: PageNotFoundComponent,
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload', relativeLinkResolution: 'legacy' })],
  exports: [RouterModule],
  providers: [AuthGuard],
})
export class CoreRoutingModule {}
