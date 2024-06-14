import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ApiService } from '@core/api/api.service';
import { AuthService } from '@core/auth/auth.service';
import { environment } from '@env';
import { OperatorType, Policy, PolicyAction, PolicyEntry, PolicyResponseMap } from '@page/policies/model/policy.model';
import { KeycloakService } from 'keycloak-angular';
import { MockPolicyResponseMap } from '../../../mocks/services/policy-mock/policy.model';
import { PolicyService } from './policy.service';

describe('PolicyService', () => {
  let service: PolicyService;
  let httpMock: HttpTestingController;
  let apiUrl: string;
  let authService: AuthService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ HttpClientTestingModule ],
      providers: [ PolicyService, ApiService, KeycloakService, AuthService ],
    });
    service = TestBed.inject(PolicyService);
    httpMock = TestBed.inject(HttpTestingController);
    apiUrl = environment.apiUrl;
    authService = TestBed.inject(AuthService);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get policies', () => {
    spyOn(authService, 'getBearerToken').and.returnValue('your_mocked_token');

    const mockResponse: PolicyResponseMap = MockPolicyResponseMap;

    service.getPolicies().subscribe(response => {
      expect(response).toEqual(Object(mockResponse));
    });

    const req = httpMock.expectOne(`${ apiUrl }/policies`);
    expect(req.request.method).toEqual('GET');
    req.flush(mockResponse);
  });

  it('should get policy by id', () => {
    spyOn(authService, 'getBearerToken').and.returnValue('your_mocked_token');

    const policyId = '123';
    const mockPolicy: Policy = {
      policyId: 'policy123',
      createdOn: '2024-01-01T00:00:00Z',
      validUntil: '2024-12-31T23:59:59Z',
      permissions: [
        {
          action: 'use' as PolicyAction,
          constraint: {
            and: [
              {
                leftOperand: 'left1',
                operator: { '@id': OperatorType.EQ },
                operatorTypeResponse: OperatorType.EQ,
                'odrl:rightOperand': 'right1',
              },
            ],
            or: [
              {
                leftOperand: 'left2',
                operator: { '@id': OperatorType.NEQ },
                operatorTypeResponse: OperatorType.NEQ,
                'odrl:rightOperand': 'right2',
              },
            ],
          },
        },
      ],
    };
    const policyEntry: PolicyEntry = {
      payload: {
        '@context': {
          odrl: 'test',
        },
        '@id': 'entry123',
        policy: mockPolicy,
      },
      validUntil: '2024-01-01T00:00:00Z',
    };

    service.getPolicyById(policyId).subscribe(response => {
      expect(response).toEqual(Object(mockPolicy));
    });

    const req = httpMock.expectOne(`${ apiUrl }/policies/${ policyId }`);
    expect(req.request.method).toEqual('GET');
    req.flush(mockPolicy);
  });

  it('should publish assets', () => {
    spyOn(authService, 'getBearerToken').and.returnValue('your_mocked_token');

    const assetIds = [ 'asset1', 'asset2' ];
    const policyId = '123';
    const mockResponse = {};

    service.publishAssets(assetIds, policyId).subscribe(response => {
      expect(response).toEqual(Object(mockResponse));
    });

    const req = httpMock.expectOne(`${ apiUrl }/assets/publish`);
    expect(req.request.method).toEqual('POST');
    const testObject = '{\"assetIds\":[\"asset1\",\"asset2\"],\"policyId\":\"123\"}';
    expect(JSON.parse(JSON.stringify(req.request.body))).toEqual(testObject);
    req.flush(mockResponse);
  });

  it('should delete policy', () => {
    spyOn(authService, 'getBearerToken').and.returnValue('your_mocked_token');

    const policyId = '123';
    const mockResponse = {};

    service.deletePolicy(policyId).subscribe(response => {
      expect(response).toEqual(Object(mockResponse));
    });

    const req = httpMock.expectOne(`${ apiUrl }/policies${ policyId }`);
    expect(req.request.method).toEqual('DELETE');
    req.flush(mockResponse);
  });

  it('should create policy', () => {
    spyOn(authService, 'getBearerToken').and.returnValue('your_mocked_token');

    const policyEntry: PolicyEntry = {
      payload: {
        '@context': {
          odrl: 'test',
        },
        '@id': 'entry123',
        policy: {
          policyId: 'policy123',
          createdOn: '2024-01-01T00:00:00Z',
          validUntil: '2024-12-31T23:59:59Z',
          permissions: [
            {
              action: 'use' as PolicyAction,
              constraint: {
                and: [
                  {
                    leftOperand: 'left1',
                    operator: { '@id': OperatorType.EQ },
                    operatorTypeResponse: OperatorType.EQ,
                    'odrl:rightOperand': 'right1',
                  },
                ],
                or: [
                  {
                    leftOperand: 'left2',
                    operator: { '@id': OperatorType.NEQ },
                    operatorTypeResponse: OperatorType.NEQ,
                    'odrl:rightOperand': 'right2',
                  },
                ],
              },
            },
          ],
        },
      },
      validUntil: '2024-01-01T00:00:00Z',
    };
    const mockResponse = {};

    service.createPolicy(policyEntry).subscribe(response => {
      expect(response).toEqual(Object(mockResponse));
    });

    const req = httpMock.expectOne(`${ apiUrl }/policies`);
    expect(req.request.method).toEqual('POST');
    expect(req.request.body).toEqual(JSON.stringify(policyEntry));
    req.flush(mockResponse);
  });

  it('should update policy', () => {
    spyOn(authService, 'getBearerToken').and.returnValue('your_mocked_token');

    const policyEntry: PolicyEntry = {
      payload: {
        '@context': {
          odrl: 'test',
        },
        '@id': 'entry123',
        policy: {
          policyId: 'policy123',
          createdOn: '2024-01-01T00:00:00Z',
          validUntil: '2024-12-31T23:59:59Z',
          permissions: [
            {
              action: 'use' as PolicyAction,
              constraint: {
                and: [
                  {
                    leftOperand: 'left1',
                    operator: { '@id': OperatorType.EQ },
                    operatorTypeResponse: OperatorType.EQ,
                    'odrl:rightOperand': 'right1',
                  },
                ],
                or: [
                  {
                    leftOperand: 'left2',
                    operator: { '@id': OperatorType.NEQ },
                    operatorTypeResponse: OperatorType.NEQ,
                    'odrl:rightOperand': 'right2',
                  },
                ],
              },
            },
          ],
        },
      },
      validUntil: '2024-01-01T00:00:00Z',
    };
    const body = {
      policyIds: [ policyEntry.payload.policy.policyId ],
      validUntil: policyEntry.validUntil,
    };
    const mockResponse = {};

    service.updatePolicy(policyEntry).subscribe(response => {
      expect(response).toEqual(Object(mockResponse));
    });

    const req = httpMock.expectOne(`${ apiUrl }/policies`);
    expect(req.request.method).toEqual('PUT');
    expect(req.request.body).toEqual(JSON.stringify(body));
    req.flush(mockResponse);
  });
});
