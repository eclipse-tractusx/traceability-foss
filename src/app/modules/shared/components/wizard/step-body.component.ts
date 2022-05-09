import { Component, TemplateRef, ViewChild } from '@angular/core';

@Component({
  selector: 'app-step-body',
  template: '<ng-template><ng-content></ng-content></ng-template>',
})
export class StepBodyComponent {
  @ViewChild(TemplateRef) bodyContent: TemplateRef<unknown>;
}
