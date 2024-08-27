import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { I18NEXT_NAMESPACE_RESOLVER } from 'angular-i18next';
import { ImprintComponent } from './presentation/imprint.component';

export /** @type {*} */
  const IMPRINT_ROUTING: Routes = [
    {
      path: '',
      pathMatch: 'full',
      component: ImprintComponent,
      data: { i18nextNamespaces: ['page.imprint'] },
      resolve: { i18next: I18NEXT_NAMESPACE_RESOLVER },
    },
  ];

@NgModule({
  imports: [RouterModule.forChild(IMPRINT_ROUTING)],
  exports: [RouterModule],
})
export class ImprintRoutingModule { }
