import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

export /** @type {*} */
const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    loadChildren: () => import('../page/dashboard/dashboard.module').then(m => m.DashboardModule),
  },

  {
    path: 'dashboard',
    loadChildren: () => import('../page/dashboard/dashboard.module').then(m => m.DashboardModule),
    data: { breadcrumb: 'Home' },
  },

  {
    path: 'about',
    loadChildren: () => import('../page/about/about.module').then(m => m.AboutModule),
    data: { breadcrumb: 'About' },
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class LayoutRoutingModule {}
