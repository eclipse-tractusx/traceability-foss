import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ApiService } from '@core/api/api.service';
import { AuthService } from '@core/auth/auth.service';
import { KeycloakService } from 'keycloak-angular';

import { PolicyService } from './policy.service';

describe('AssetPublisherService', () => {
  let service: PolicyService;
  let httpTestingController: HttpTestingController;
  let authService: AuthService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [PolicyService, ApiService, KeycloakService, AuthService],
    });
    service = TestBed.inject(PolicyService);
    httpTestingController = TestBed.inject(HttpTestingController);
    authService = TestBed.inject(AuthService);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
