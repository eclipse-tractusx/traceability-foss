import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { State } from 'src/app/modules/shared/model/state';
import { Step } from './step.model';

@Injectable({
  providedIn: 'root',
})
export class StepState {
  private readonly steps$: State<Step[]> = new State<Step[]>([]);
  private readonly currentStep$: State<Step> = new State<Step>(null);

  get getSteps$(): Observable<Step[]> {
    return this.steps$.observable;
  }

  get getCurrentStep$(): Observable<Step> {
    return this.currentStep$.observable;
  }

  get currentStepSnapshot(): Step {
    return this.currentStep$.snapshot;
  }

  public setSteps(steps: Step[]): void {
    this.steps$.update(steps);
  }

  public setCurrentStep(step: Step): void {
    this.currentStep$.update(step);
  }

  public moveToNextStep(): void {
    const step = this.currentStep$.snapshot;
    const index = step.stepIndex;
    const lastStep = this.steps$.snapshot[index - 1];
    const afterStep = this.steps$.snapshot[index];
    lastStep.isActive = false;
    lastStep.isComplete = true;
    afterStep.isActive = true;
    if (index < this.steps$.snapshot.length) {
      this.currentStep$.update(this.steps$.snapshot[index]);
    }
  }

  public moveToPreviousStep(): void {
    const step = this.currentStep$.snapshot;
    const index = step.stepIndex;
    const previous = this.steps$.snapshot[index - 2];
    const current = this.steps$.snapshot[index - 1];
    previous.isComplete = false;
    previous.isActive = true;
    current.isActive = false;
    if (index > 1) {
      this.currentStep$.update(this.steps$.snapshot[index - 2]);
    }
  }

  public isLastStep(): boolean {
    return this.currentStep$.snapshot.stepIndex === this.steps$.snapshot.length;
  }

  public isFirstStep(): boolean {
    return this.currentStep$.snapshot.stepIndex === 1;
  }
}
