import { Component, TemplateRef, ViewChild } from '@angular/core';

@Component({
  selector: 'app-step-actions',
  template: '<ng-template><ng-content></ng-content></ng-template>',
})
export class StepActionsComponent {
  @ViewChild(TemplateRef) actionsContent: TemplateRef<unknown>;
}
