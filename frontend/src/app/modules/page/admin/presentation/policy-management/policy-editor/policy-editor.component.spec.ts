import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PolicyEditorComponent } from './policy-editor.component';

describe('PolicyEditorComponent', () => {
  let component: PolicyEditorComponent;
  let fixture: ComponentFixture<PolicyEditorComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ PolicyEditorComponent ],
    });
    fixture = TestBed.createComponent(PolicyEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
