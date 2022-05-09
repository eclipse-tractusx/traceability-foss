import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { StepsComponent } from '../components/wizard/steps/steps.component';

describe('StepsComponent', () => {
  let component: StepsComponent;
  let fixture: ComponentFixture<StepsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      declarations: [StepsComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StepsComponent);
    component = fixture.componentInstance;
    component.steps$ = of([
      { stepIndex: 1, isComplete: false, isActive: true, label: 'Investigation Details', isOptional: false },
      { stepIndex: 2, isComplete: false, isActive: false, label: 'Affected Parts', isOptional: true },
      { stepIndex: 3, isComplete: false, isActive: false, label: 'Organizations', isOptional: true },
      { stepIndex: 4, isComplete: false, isActive: false, label: 'Summary', isOptional: true },
    ]);
    component.currentStep$ = of({
      stepIndex: 1,
      isComplete: false,
      isActive: true,
      label: 'Investigation Details',
      isOptional: false,
    });
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
