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
import { State } from 'src/app/shared/model/state';
import { Step } from './step.model';

/**
 *
 *
 * @export
 * @class StepState
 */
@Injectable({
  providedIn: 'root',
})
export class StepState {
  /**
   * Steps state
   *
   * @private
   * @readonly
   * @type {State<Step[]>}
   * @memberof StepState
   */
  private readonly steps$: State<Step[]> = new State<Step[]>([]);

  /**
   * Current step state
   *
   * @private
   * @readonly
   * @type {State<Step>}
   * @memberof StepState
   */
  private readonly currentStep$: State<Step> = new State<Step>(null);

  /**
   * Steps state getter
   *
   * @readonly
   * @type {Observable<Step[]>}
   * @memberof StepState
   */
  get getSteps$(): Observable<Step[]> {
    return this.steps$.observable;
  }

  /**
   * Current step getter
   *
   * @readonly
   * @type {Observable<Step>}
   * @memberof StepState
   */
  get getCurrentStep$(): Observable<Step> {
    return this.currentStep$.observable;
  }

  /**
   * Current step snapshot getter
   *
   * @readonly
   * @type {Step}
   * @memberof StepState
   */
  get currentStepSnapshot(): Step {
    return this.currentStep$.snapshot;
  }

  /**
   * Steps state setter
   *
   * @param {Step[]} steps
   * @return {void}
   * @memberof StepState
   */
  public setSteps(steps: Step[]): void {
    this.steps$.update(steps);
  }

  /**
   * Current step state setter
   *
   * @param {Step} step
   * @return {void}
   * @memberof StepState
   */
  public setCurrentStep(step: Step): void {
    this.currentStep$.update(step);
  }

  /**
   * Move to next step
   *
   * @return {void}
   * @memberof StepState
   */
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

  /**
   * Move to previous step
   *
   * @return {void}
   * @memberof StepState
   */
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

  /**
   * Is last step
   *
   * @return {boolean}
   * @memberof StepState
   */
  public isLastStep(): boolean {
    return this.currentStep$.snapshot.stepIndex === this.steps$.snapshot.length;
  }

  /**
   * Is first step
   *
   * @return {boolean}
   * @memberof StepState
   */
  public isFirstStep(): boolean {
    return this.currentStep$.snapshot.stepIndex === 1;
  }
}
