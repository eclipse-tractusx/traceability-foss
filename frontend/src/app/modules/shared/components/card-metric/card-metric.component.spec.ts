import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SharedModule } from '@shared/shared.module';

import { CardMetricComponent } from './card-metric.component';

describe('CardMetricComponent', () => {
  let component: CardMetricComponent;
  let fixture: ComponentFixture<CardMetricComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CardMetricComponent ],
      imports: [ SharedModule ],
    })
      .compileComponents();

    fixture = TestBed.createComponent(CardMetricComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
