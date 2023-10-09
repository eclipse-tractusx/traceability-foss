import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CountryFlagGeneratorComponent } from './country-flag-generator.component';

describe('CountryFlagGeneratorComponent', () => {
  let component: CountryFlagGeneratorComponent;
  let fixture: ComponentFixture<CountryFlagGeneratorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CountryFlagGeneratorComponent ],
    })
      .compileComponents();

    fixture = TestBed.createComponent(CountryFlagGeneratorComponent);
    component = fixture.componentInstance;
    component.countryCode = 'DEU';
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
