import { TestBed } from '@angular/core/testing';

import { PolicyService } from './policy.service';

describe('AssetPublisherService', () => {
  let service: PolicyService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PolicyService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
