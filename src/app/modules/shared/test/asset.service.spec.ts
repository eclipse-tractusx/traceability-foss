import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { KeycloakService } from 'keycloak-angular';
import { AuthService } from 'src/app/modules/core/auth/auth.service';
import { AssetService } from '../service/asset.service';

describe('AssetService', () => {
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
  };

  beforeEach(() =>
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [KeycloakService, { provide: AuthService, useValue: userData }],
    }),
  );

  it('should be created', () => {
    const service: AssetService = TestBed.get(AssetService);
    expect(service).toBeTruthy();
  });
});
