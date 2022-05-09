import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { Step } from 'src/app/modules/shared/components/wizard/step.model';
import { WizardFacade } from '../wizard.facade';

@Component({
  selector: 'app-steps',
  templateUrl: './steps.component.html',
  styleUrls: ['./steps.component.scss'],
})
export class StepsComponent {
  public steps$: Observable<Step[]>;
  public currentStep$: Observable<Step>;

  constructor(private wizardFacade: WizardFacade) {
    this.steps$ = this.wizardFacade.steps$;
    this.currentStep$ = this.wizardFacade.currentStep$;
  }
}
