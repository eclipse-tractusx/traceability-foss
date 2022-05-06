import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { PrivateLayoutComponent } from './layout/presentation/private-layout/private-layout.component';
import { PageNotFoundComponent } from './page/page-not-found/presentation/page-not-found.component';
import { realm } from './core/api/api.service.properties';
import { AuthGuard } from './core/auth/auth.guard';

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
export class AppRoutingModule {}
