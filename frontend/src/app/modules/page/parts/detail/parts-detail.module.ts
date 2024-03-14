import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {getI18nPageProvider} from '@core/i18n';
import {PartsDetailComponent} from '@page/parts/detail/parts-detail.component';
import {LoadedElementsFacade} from '@shared/modules/relations/core/loaded-elements.facade';
import {LoadedElementsState} from '@shared/modules/relations/core/loaded-elements.state';
import {RelationsModule} from '@shared/modules/relations/relations.module';
import {SharedModule} from '@shared/shared.module';
import {TemplateModule} from '@shared/template.module';

@NgModule({
  declarations: [ PartsDetailComponent ],
  imports: [ CommonModule, TemplateModule, SharedModule, RelationsModule],
  providers: [
    LoadedElementsFacade,
    LoadedElementsState,
    ...getI18nPageProvider([ 'page.parts', 'partDetail' ]),
  ],
  exports: [ PartsDetailComponent ],
})
export class PartsDetailModule {
}
