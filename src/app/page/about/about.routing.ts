import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AboutComponent } from './presentation/about.component';

export /** @type {*} */
const ABOUT_ROUTING: Routes = [{ path: '', pathMatch: 'full', component: AboutComponent }];

@NgModule({
  imports: [RouterModule.forChild(ABOUT_ROUTING)],
  exports: [RouterModule],
})
export class AboutRoutingModule {}
