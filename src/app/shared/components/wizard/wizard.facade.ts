/*
 * Copyright 2021 The PartChain Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { delay } from 'rxjs/operators';
import { Step } from './step.model';
import { StepState } from './step.state';

/**
 *
 *
 * @export
 * @class WizardFacade
 */
@Injectable({
  providedIn: 'root',
})
export class WizardFacade {
  /**
   * @constructor WizardFacade (DI)
   * @param {StepState} stepState
   * @memberof WizardFacade
   */
  constructor(private stepState: StepState) {}

  /**
   * Steps state getter
   *
   * @readonly
   * @type {Observable<Step[]>}
   * @memberof WizardFacade
   */
  get steps$(): Observable<Step[]> {
    return this.stepState.getSteps$.pipe(delay(0));
  }

  /**
   * Current step state getter
   *
   * @readonly
   * @type {Observable<Step>}
   * @memberof WizardFacade
   */
  get currentStep$(): Observable<Step> {
    return this.stepState.getCurrentStep$;
  }

  /**
   * Current step snapshot getter
   *
   * @readonly
   * @type {Step}
   * @memberof WizardFacade
   */
  get currentStepSnapshot(): Step {
    return this.stepState.currentStepSnapshot;
  }

  /**
   * Steps state setter
   *
   * @param {Step[]} steps
   * @return {void}
   * @memberof WizardFacade
   */
  public setSteps(steps: Step[]): void {
    this.stepState.setSteps(steps);
  }

  /**
   * Current step state setter
   *
   * @param {Step} currentStep
   * @return {void}
   * @memberof WizardFacade
   */
  public setCurrentStep(currentStep: Step): void {
    this.stepState.setCurrentStep(currentStep);
  }

  /**
   * Move to next step
   *
   * @return {void}
   * @memberof WizardFacade
   */
  public onNextStep(): void {
    this.stepState.moveToNextStep();
  }

  /**
   * Move to previous step
   *
   * @return {void}
   * @memberof WizardFacade
   */
  public onPreviousStep(): void {
    this.stepState.moveToPreviousStep();
  }

  /**
   * Is last step
   *
   * @return {boolean}
   * @memberof WizardFacade
   */
  public isLastStep(): boolean {
    return this.stepState.isLastStep();
  }

  /**
   * Is first step
   *
   * @return {boolean}
   * @memberof WizardFacade
   */
  public isFirstStep(): boolean {
    return this.stepState.isFirstStep();
  }
}
