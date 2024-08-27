import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { getI18nPageProvider } from '@core/i18n';
import { SharedModule } from '@shared/shared.module';
import { TemplateModule } from '@shared/template.module';
import { ImprintRoutingModule } from './imprint.routing';
import { ImprintComponent } from './presentation/imprint.component';

@NgModule({
  declarations: [ImprintComponent],
  imports: [CommonModule, TemplateModule, SharedModule, ImprintRoutingModule],
  providers: [...getI18nPageProvider('page.imprint')],
})
export class ImprintModule { }
