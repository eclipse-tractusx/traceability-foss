import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { TemplateModule } from 'src/app/modules/shared/template.module';
import { TabBodyComponent } from './tab.body.component';
import { TabItemComponent } from './tab.item.component';
import { TabLabelComponent } from './tab.label.component';
import { TabsComponent } from './tabs.component';

@NgModule({
  declarations: [TabsComponent, TabItemComponent, TabLabelComponent, TabBodyComponent],
  imports: [CommonModule, TemplateModule],
  exports: [TabsComponent, TabItemComponent, TabBodyComponent, TabLabelComponent],
})
export class TabsModule {}
