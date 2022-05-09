import { HttpClient, HttpHandler } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { KeycloakService } from 'keycloak-angular';
import { AuthService } from '../auth/auth.service';
import { UserService } from '../user/user.service';

describe('UserService', () => {
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

    getUrl() {
      return;
    },

    getMspid() {
      return;
    },
  };
  beforeEach(() =>
    TestBed.configureTestingModule({
      providers: [KeycloakService, { provide: AuthService, useValue: userData }, HttpClient, HttpHandler],
    }),
  );

  it('should be created', () => {
    const service: UserService = TestBed.get(UserService);
    expect(service).toBeTruthy();
  });
});
