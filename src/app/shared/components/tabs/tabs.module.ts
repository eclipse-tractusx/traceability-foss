import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TabsComponent } from './tabs.component';
import { TabItemComponent } from './tab.item.component';
import { TabLabelComponent } from './tab.label.component';
import { TabBodyComponent } from './tab.body.component';
import { TemplateModule } from 'src/app/shared/template.module';

@NgModule({
  declarations: [TabsComponent, TabItemComponent, TabLabelComponent, TabBodyComponent],
  imports: [CommonModule, TemplateModule],
  exports: [TabsComponent, TabItemComponent, TabBodyComponent, TabLabelComponent],
})
export class TabsModule {}
