import { TestBed } from '@angular/core/testing';

import { AssetPublisherService } from './asset-publisher.service';

describe('AssetPublisherService', () => {
  let service: AssetPublisherService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AssetPublisherService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
