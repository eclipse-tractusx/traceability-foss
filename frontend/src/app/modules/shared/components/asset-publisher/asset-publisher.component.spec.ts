import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AssetPublisherComponent } from './asset-publisher.component';

describe('AssetPublisherComponent', () => {
  let component: AssetPublisherComponent;
  let fixture: ComponentFixture<AssetPublisherComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AssetPublisherComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AssetPublisherComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
