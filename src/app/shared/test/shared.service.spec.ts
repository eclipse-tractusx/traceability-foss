import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { KeycloakService } from 'keycloak-angular';
import { AuthService } from 'src/app/core/auth/auth.service';

import { SharedService } from '../core/shared.service';

describe('SharedService', () => {
  let service: SharedService;

  const userData = {
    getUserData() {
      return {
        username: 'Lion',
        firstname: 'Lion',
        surname: 'Lion',
        email: 'lion@email.com',
        mspid: 'Lion',
        realm_access: { roles: ['admin'] },
      };
    },

    getMspid() {
      return;
    },

    getUrl() {
      return;
    },
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [KeycloakService, { provide: AuthService, useValue: userData }],
    });
    service = TestBed.inject(SharedService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
