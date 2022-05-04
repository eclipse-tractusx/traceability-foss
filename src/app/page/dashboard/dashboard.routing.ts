import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DashboardComponent } from './presentation/dashboard.component';

export /** @type {*} */
const DASHBOARD_ROUTING: Routes = [{ path: '', pathMatch: 'full', component: DashboardComponent }];

@NgModule({
  imports: [RouterModule.forChild(DASHBOARD_ROUTING)],
  exports: [RouterModule],
})
export class DashboardRoutingModule {}
