package org.eclipse.tractusx.traceability.policies.domain;

import org.eclipse.tractusx.traceability.assets.domain.importpoc.exception.PolicyNotFoundException;
import org.eclipse.tractusx.traceability.notification.domain.contract.EdcNotificationContractService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.eclipse.tractusx.irs.edc.client.policy.Policy;
import policies.request.Payload;
import policies.request.RegisterPolicyRequest;
import policies.request.UpdatePolicyRequest;
import policies.response.CreatePolicyResponse;
import policies.response.IrsPolicyResponse;
import policies.response.PolicyResponse;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PolicyServiceImplTest {

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private EdcNotificationContractService edcNotificationContractService;

    @InjectMocks
    private PolicyServiceImpl policyService;

    @Test
    @DisplayName("getIrsPolicies: returns policies map when present")
    void testGetIrsPoliciesReturnsMap() {
        Map<String, List<IrsPolicyResponse>> policies = new HashMap<>();
        policies.put("type1", List.of(mock(IrsPolicyResponse.class)));

        when(policyRepository.getPolicies()).thenReturn(policies);

        Map<String, List<IrsPolicyResponse>> result = policyService.getIrsPolicies();

        assertEquals(policies, result);
        verify(policyRepository).getPolicies();
    }

    @Test
    @DisplayName("getIrsPolicies: returns empty map when none exist")
    void testGetIrsPoliciesReturnsEmptyMapWhenNoneExist() {
        when(policyRepository.getPolicies()).thenReturn(Collections.emptyMap());

        Map<String, List<IrsPolicyResponse>> result = policyService.getIrsPolicies();

        assertTrue(result.isEmpty());
        verify(policyRepository).getPolicies();
    }

    @Test
    @DisplayName("getPolicies: returns flattened list with one policy type")
    void testGetPoliciesReturnsFlattenedList() {
        Payload payload = mock(Payload.class);
        when(payload.policyId()).thenReturn("mock-policy-id");

        Policy policyMock = mock(Policy.class);
        when(policyMock.getPermissions()).thenReturn(Collections.emptyList());
        when(payload.policy()).thenReturn(policyMock);

        IrsPolicyResponse irsPolicy = mock(IrsPolicyResponse.class);
        when(irsPolicy.payload()).thenReturn(payload);

        Map<String, List<IrsPolicyResponse>> policies = Map.of("type1", List.of(irsPolicy));
        when(policyRepository.getPolicies()).thenReturn(policies);

        List<PolicyResponse> result = policyService.getPolicies();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(policyRepository).getPolicies();
    }

    @Test
    @DisplayName("getPolicies: returns flattened list with multiple policy types")
    void testGetPoliciesWithMultipleTypes() {
        Payload payload1 = mock(Payload.class);
        when(payload1.policyId()).thenReturn("id1");
        Policy policy1 = mock(Policy.class);
        when(policy1.getPermissions()).thenReturn(Collections.emptyList());
        when(payload1.policy()).thenReturn(policy1);
        IrsPolicyResponse response1 = mock(IrsPolicyResponse.class);
        when(response1.payload()).thenReturn(payload1);

        Payload payload2 = mock(Payload.class);
        when(payload2.policyId()).thenReturn("id2");
        Policy policy2 = mock(Policy.class);
        when(policy2.getPermissions()).thenReturn(Collections.emptyList());
        when(payload2.policy()).thenReturn(policy2);
        IrsPolicyResponse response2 = mock(IrsPolicyResponse.class);
        when(response2.payload()).thenReturn(payload2);

        Map<String, List<IrsPolicyResponse>> policies = new HashMap<>();
        policies.put("type1", List.of(response1));
        policies.put("type2", List.of(response2));

        when(policyRepository.getPolicies()).thenReturn(policies);

        List<PolicyResponse> result = policyService.getPolicies();

        assertEquals(2, result.size());
        verify(policyRepository).getPolicies();
    }

    @Test
    @DisplayName("getPolicies: returns empty list when no policies exist")
    void testGetPoliciesReturnsEmptyListWhenNoPoliciesExist() {
        when(policyRepository.getPolicies()).thenReturn(Collections.emptyMap());

        List<PolicyResponse> result = policyService.getPolicies();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(policyRepository).getPolicies();
    }

    @Test
    @DisplayName("getPolicy: returns policy response when policy exists")
    void testGetPolicyReturnsPolicyResponse() {
        Payload payload = mock(Payload.class);
        when(payload.policyId()).thenReturn("mock-policy-id");

        Policy policyMock = mock(Policy.class);
        when(policyMock.getPermissions()).thenReturn(Collections.emptyList());
        when(payload.policy()).thenReturn(policyMock);

        IrsPolicyResponse irsPolicy = mock(IrsPolicyResponse.class);
        when(irsPolicy.payload()).thenReturn(payload);

        when(policyRepository.getPolicy("123")).thenReturn(Map.of("type1", Optional.of(irsPolicy)));

        PolicyResponse result = policyService.getPolicy("123");

        assertNotNull(result);
        verify(policyRepository).getPolicy("123");
    }

    @Test
    @DisplayName("getPolicy: throws PolicyNotFoundException when policy missing")
    void testGetPolicyThrowsWhenNotFound() {
        when(policyRepository.getPolicy("missing")).thenReturn(Map.of());

        assertThrows(PolicyNotFoundException.class, () -> policyService.getPolicy("missing"));
        verify(policyRepository).getPolicy("missing");
    }

    @Test
    @DisplayName("getPolicy: throws IllegalArgumentException when id is null")
    void testGetPolicyThrowsWhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> policyService.getPolicy(null));
        verifyNoInteractions(policyRepository);
    }

    @Test
    @DisplayName("getPolicy: throws IllegalArgumentException when id is empty")
    void testGetPolicyThrowsWhenIdIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> policyService.getPolicy(""));
        verifyNoInteractions(policyRepository);
    }

    @Test
    @DisplayName("createPolicy: creates policy and updates EDC")
    void testCreatePolicyUpdatesEdcAndReturnsResponse() {
        RegisterPolicyRequest request = mock(RegisterPolicyRequest.class);
        CreatePolicyResponse response = mock(CreatePolicyResponse.class);

        when(policyRepository.createPolicy(request)).thenReturn(response);

        CreatePolicyResponse result = policyService.createPolicy(request);

        assertEquals(response, result);
        verify(policyRepository).createPolicy(request);
        verify(edcNotificationContractService).updateNotificationContractDefinitions();
    }

    @Test
    @DisplayName("createPolicy: throws IllegalArgumentException when request is null")
    void testCreatePolicyThrowsWhenRequestIsNull() {
        assertThrows(IllegalArgumentException.class, () -> policyService.createPolicy(null));
        verifyNoInteractions(policyRepository, edcNotificationContractService);
    }

    @Test
    @DisplayName("createPolicy: handles repository exception gracefully")
    void testCreatePolicyHandlesRepositoryFailure() {
        RegisterPolicyRequest request = mock(RegisterPolicyRequest.class);
        when(policyRepository.createPolicy(request)).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> policyService.createPolicy(request));
        verify(edcNotificationContractService, never()).updateNotificationContractDefinitions();
    }

    @Test
    @DisplayName("updatePolicy: calls repository and updates EDC")
    void testUpdatePolicyCallsRepoAndUpdatesEdc() {
        UpdatePolicyRequest request = mock(UpdatePolicyRequest.class);

        policyService.updatePolicy(request);

        verify(policyRepository).updatePolicy(request);
        verify(edcNotificationContractService).updateNotificationContractDefinitions();
    }

    @Test
    @DisplayName("updatePolicy: throws IllegalArgumentException when request is null")
    void testUpdatePolicyThrowsWhenRequestIsNull() {
        assertThrows(IllegalArgumentException.class, () -> policyService.updatePolicy(null));
        verifyNoInteractions(policyRepository, edcNotificationContractService);
    }

    @Test
    @DisplayName("updatePolicy: handles repository failure gracefully")
    void testUpdatePolicyHandlesRepositoryFailure() {
        UpdatePolicyRequest request = mock(UpdatePolicyRequest.class);
        doThrow(new RuntimeException("Update error")).when(policyRepository).updatePolicy(request);

        assertThrows(RuntimeException.class, () -> policyService.updatePolicy(request));
        verify(edcNotificationContractService, never()).updateNotificationContractDefinitions();
    }

    @Test
    @DisplayName("deletePolicy: deletes policy and updates EDC")
    void testDeletePolicyCallsRepoAndUpdatesEdc() {
        policyService.deletePolicy("123");

        verify(policyRepository).deletePolicy("123");
        verify(edcNotificationContractService).updateNotificationContractDefinitions();
    }

    @Test
    @DisplayName("deletePolicy: throws IllegalArgumentException when id is null")
    void testDeletePolicyThrowsWhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> policyService.deletePolicy(null));
        verifyNoInteractions(policyRepository, edcNotificationContractService);
    }

    @Test
    @DisplayName("deletePolicy: throws IllegalArgumentException when id is empty")
    void testDeletePolicyThrowsWhenIdIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> policyService.deletePolicy(""));
        verifyNoInteractions(policyRepository, edcNotificationContractService);
    }

    @Test
    @DisplayName("deletePolicy: handles repository failure gracefully")
    void testDeletePolicyHandlesRepositoryFailure() {
        doThrow(new RuntimeException("Delete failed")).when(policyRepository).deletePolicy("123");

        assertThrows(RuntimeException.class, () -> policyService.deletePolicy("123"));
        verify(edcNotificationContractService, never()).updateNotificationContractDefinitions();
    }
}
