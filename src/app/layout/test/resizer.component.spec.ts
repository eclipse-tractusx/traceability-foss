import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ResizerComponent } from '../presentation/resizer/resizer.component';

describe('ResizerComponent', () => {
  let component: ResizerComponent;
  let fixture: ComponentFixture<ResizerComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      declarations: [ResizerComponent],
    });
    fixture = TestBed.createComponent(ResizerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
