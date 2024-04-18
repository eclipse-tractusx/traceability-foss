import { TestBed } from '@angular/core/testing';

import { SharedPartService } from './shared-part.service';

describe('SharedAssetIdService', () => {
  let service: SharedPartService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SharedPartService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
