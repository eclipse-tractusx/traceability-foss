import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { delay } from 'rxjs/operators';
import { Step } from './step.model';
import { StepState } from './step.state';

@Injectable({
  providedIn: 'root',
})
export class WizardFacade {
  constructor(private stepState: StepState) {}

  get steps$(): Observable<Step[]> {
    return this.stepState.getSteps$.pipe(delay(0));
  }

  get currentStep$(): Observable<Step> {
    return this.stepState.getCurrentStep$;
  }

  get currentStepSnapshot(): Step {
    return this.stepState.currentStepSnapshot;
  }

  public setSteps(steps: Step[]): void {
    this.stepState.setSteps(steps);
  }

  public setCurrentStep(currentStep: Step): void {
    this.stepState.setCurrentStep(currentStep);
  }

  public onNextStep(): void {
    this.stepState.moveToNextStep();
  }

  public onPreviousStep(): void {
    this.stepState.moveToPreviousStep();
  }

  public isLastStep(): boolean {
    return this.stepState.isLastStep();
  }

  public isFirstStep(): boolean {
    return this.stepState.isFirstStep();
  }
}
