import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LayoutFacade } from 'src/app/modules/shared/abstraction/layout-facade';
import { PrivateLayoutComponent } from '../private-layout/private-layout.component';

describe('PrivateLayoutComponent', () => {
  let component: PrivateLayoutComponent;
  let fixture: ComponentFixture<PrivateLayoutComponent>;

  const layoutFacade = {
    setMspids() {
      return;
    },

    setAlerts() {
      return;
    },
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      declarations: [PrivateLayoutComponent],
      providers: [{ provide: LayoutFacade, useValue: layoutFacade }],
    });
    fixture = TestBed.createComponent(PrivateLayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
