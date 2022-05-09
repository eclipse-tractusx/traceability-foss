import { Component, ContentChild, Input, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { StepActionsComponent } from './step-actions.component';
import { StepBodyComponent } from './step-body.component';
import { Step } from './step.model';
import { WizardFacade } from './wizard.facade';

@Component({
  selector: 'app-wizard',
  templateUrl: './wizard.component.html',
  styleUrls: ['./wizard.component.scss'],
})
export class WizardComponent implements OnInit {
  @ContentChild(StepBodyComponent) bodyComponent: StepBodyComponent;
  @ContentChild(StepActionsComponent) actionsComponent: StepActionsComponent;

  @Input() steps: Step[];

  public steps$: Observable<Step[]>;
  public currentStep$: Observable<Step>;

  constructor(private wizardFacade: WizardFacade) {
    this.steps$ = this.wizardFacade.steps$;
    this.currentStep$ = this.wizardFacade.currentStep$;
  }

  ngOnInit(): void {
    this.wizardFacade.setSteps(this.steps);
    this.wizardFacade.setCurrentStep(this.steps[0]);
  }
}
