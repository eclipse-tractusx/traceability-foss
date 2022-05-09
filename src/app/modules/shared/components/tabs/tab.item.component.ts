import { Component, ContentChild, Input } from '@angular/core';
import { TabBodyComponent } from './tab.body.component';
import { TabLabelComponent } from './tab.label.component';

@Component({
  selector: 'app-tab-item',
  template: '<ng-content></ng-content>',
})
export class TabItemComponent {
  @Input() label: string;
  @Input() isActive: boolean;

  @ContentChild(TabBodyComponent) bodyComponent: TabBodyComponent;
  @ContentChild(TabLabelComponent) labelComponent: TabLabelComponent;
}
