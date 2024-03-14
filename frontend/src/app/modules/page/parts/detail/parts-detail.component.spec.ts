import {ComponentFixture, TestBed} from '@angular/core/testing';

import {PartsDetailComponent} from './parts-detail.component';

describe('PartsDetailComponent', () => {
  let component: PartsDetailComponent;
  let fixture: ComponentFixture<PartsDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PartsDetailComponent]
    });
    fixture = TestBed.createComponent(PartsDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
