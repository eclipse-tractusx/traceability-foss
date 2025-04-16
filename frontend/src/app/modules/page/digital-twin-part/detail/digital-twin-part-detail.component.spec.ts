import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DigitalTwinPartDetailComponent } from './digital-twin-part-detail.component';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { DigitalTwinPartFacade } from '../core/digital-twin-part.facade';
import { DigitalTwinPartDetailResponse } from '../model/digitalTwinPart.model';
import { DigitalTwinPartAssembler } from '@shared/assembler/digital-twin-part.assembler';

describe('DigitalTwinPartDetailComponent', () => {
  let component: DigitalTwinPartDetailComponent;
  let fixture: ComponentFixture<DigitalTwinPartDetailComponent>;
  let mockFacade: jasmine.SpyObj<DigitalTwinPartFacade>;
  let mockActivatedRoute: Partial<ActivatedRoute>;

  const mockDetail: DigitalTwinPartDetailResponse = {
    aasId: 'aas-123',
    globalAssetId: 'gaid-456',
    bpn: 'BPNL000000',
    digitalTwinType: 'PART_INSTANCE' as any,
    aasExpirationDate: new Date(2025, 3, 15, 9, 0, 0), // Note: Month is 0-based in JavaScript Date
    assetExpirationDate: new Date(2025, 3, 20, 8, 0, 0), // Note: Month is 0-based in JavaScript Date
    aasTTL: 120,
    nextLookup: new Date(2025, 3, 16, 9, 0, 0), // Note: Month is 0-based in JavaScript Date
    assetTTL: 180,
    nextSync: new Date(2025, 3, 17, 9, 0, 0), // Note: Month is 0-based in JavaScript Date
    actor: 'test-actor'
  };

  const mockAssembled = {
    registryData: {
      aasId: 'aas-123',
      ttl: '120',
      nextLookup: '2025-04-16 09:00:00',
      lastUpdatedAt: '2025-04-15 09:00:00',
      createdAt: '2025-04-15 09:00:00',
      actor: 'test-actor'
    },
    irsData: {
      globalAssetId: 'gaid-456',
      assetTTl: '180',
      nextSync: '2025-04-17 09:00:00',
      assetExpirationDate: '2025-04-20 08:00:00',
      actor: 'test-actor'
    }
  };

  beforeEach(async () => {
    mockFacade = jasmine.createSpyObj('DigitalTwinPartFacade', ['getDigitalTwinPartDetail']);
    const mockActivatedRoute = {
        snapshot: {
          paramMap: {
            get: (key: string) => (key === 'aasId' ? 'aas-123' : null),
            has: (key: string) => key === 'aasId',
            getAll: () => [],
            keys: ['aasId']
          }
        }
      };

    await TestBed.configureTestingModule({
      declarations: [DigitalTwinPartDetailComponent],
      providers: [
        { provide: DigitalTwinPartFacade, useValue: mockFacade },
        { provide: ActivatedRoute, useValue: mockActivatedRoute }
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DigitalTwinPartDetailComponent);
    component = fixture.componentInstance;

    spyOn(DigitalTwinPartAssembler, 'assembleDetailView').and.returnValue(mockAssembled);
    mockFacade.getDigitalTwinPartDetail.and.returnValue(of(mockDetail));

    fixture.detectChanges(); // triggers ngOnInit
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should load aasId from route and call facade', () => {
    expect(component.aasId).toBe('aas-123');
    expect(mockFacade.getDigitalTwinPartDetail).toHaveBeenCalledWith('aas-123');
  });

  it('should assign detail, registryData and irsData', () => {
    expect(component.detail).toEqual(mockDetail);
    expect(component.registryData).toEqual(mockAssembled.registryData);
    expect(component.irsData).toEqual(mockAssembled.irsData);
  });
});
