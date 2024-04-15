import { TestBed } from '@angular/core/testing';

import { SharedPartIdsService } from './shared-part-ids.service';

describe('SharedAssetIdService', () => {
  let service: SharedPartIdsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SharedPartIdsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
