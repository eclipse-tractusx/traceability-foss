/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
