package org.eclipse.tractusx.traceability.policies.domain;

import org.eclipse.tractusx.traceability.notification.domain.contract.EdcNotificationContractService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.eclipse.tractusx.irs.edc.client.policy.Policy; // Correct import for Policy
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
    void testGetIrsPoliciesReturnsMap() {
        Map<String, List<IrsPolicyResponse>> policies = new HashMap<>();
        policies.put("type1", List.of(mock(IrsPolicyResponse.class)));

        when(policyRepository.getPolicies()).thenReturn(policies);

        Map<String, List<IrsPolicyResponse>> result = policyService.getIrsPolicies();

        assertEquals(policies, result);
        verify(policyRepository).getPolicies();
    }

    @Test
    void testGetIrsPoliciesReturnsEmptyMapWhenNoneExist() {
        when(policyRepository.getPolicies()).thenReturn(Collections.emptyMap());

        Map<String, List<IrsPolicyResponse>> result = policyService.getIrsPolicies();

        assertTrue(result.isEmpty());
        verify(policyRepository).getPolicies();
    }

    @Test
    void testGetPoliciesReturnsFlattenedList() {
        // Mock nested calls like payload().policyId() and payload().policy()
        Payload payload = mock(Payload.class);
        when(payload.policyId()).thenReturn("mock-policy-id");
        when(payload.policy()).thenReturn(mock(Policy.class)); // required for toResponse()

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
    void testGetPoliciesReturnsEmptyListWhenNoPoliciesExist() {
        when(policyRepository.getPolicies()).thenReturn(Collections.emptyMap());

        List<PolicyResponse> result = policyService.getPolicies();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(policyRepository).getPolicies();
    }

    @Test
    void testGetPolicyReturnsPolicyResponse() {
        Payload payload = mock(Payload.class);
        when(payload.policyId()).thenReturn("mock-policy-id");
        when(payload.policy()).thenReturn(mock(Policy.class));

        IrsPolicyResponse irsPolicy = mock(IrsPolicyResponse.class);
        when(irsPolicy.payload()).thenReturn(payload);

        when(policyRepository.getPolicy("123")).thenReturn(Map.of("type1", Optional.of(irsPolicy)));

        PolicyResponse result = policyService.getPolicy("123");

        assertNotNull(result);
        verify(policyRepository).getPolicy("123");
    }

    @Test
    void testGetPolicyThrowsWhenNotFound() {
        when(policyRepository.getPolicy("missing")).thenReturn(Map.of());

        assertThrows(RuntimeException.class, () -> policyService.getPolicy("missing"));
        verify(policyRepository).getPolicy("missing");
    }

    @Test
    void testGetPolicyThrowsWhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> policyService.getPolicy(null));
    }

    @Test
    void testGetPolicyThrowsWhenIdIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> policyService.getPolicy(""));
    }

    @Test
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
    void testCreatePolicyThrowsWhenRequestIsNull() {
        assertThrows(IllegalArgumentException.class, () -> policyService.createPolicy(null));
    }

    @Test
    void testCreatePolicyHandlesRepositoryFailure() {
        RegisterPolicyRequest request = mock(RegisterPolicyRequest.class);
        when(policyRepository.createPolicy(request)).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> policyService.createPolicy(request));
        verify(edcNotificationContractService, never()).updateNotificationContractDefinitions();
    }

    @Test
    void testUpdatePolicyCallsRepoAndUpdatesEdc() {
        UpdatePolicyRequest request = mock(UpdatePolicyRequest.class);

        policyService.updatePolicy(request);

        verify(policyRepository).updatePolicy(request);
        verify(edcNotificationContractService).updateNotificationContractDefinitions();
    }

    @Test
    void testUpdatePolicyThrowsWhenRequestIsNull() {
        assertThrows(IllegalArgumentException.class, () -> policyService.updatePolicy(null));
    }

    @Test
    void testUpdatePolicyHandlesRepositoryFailure() {
        UpdatePolicyRequest request = mock(UpdatePolicyRequest.class);
        doThrow(new RuntimeException("Update error")).when(policyRepository).updatePolicy(request);

        assertThrows(RuntimeException.class, () -> policyService.updatePolicy(request));
        verify(edcNotificationContractService, never()).updateNotificationContractDefinitions();
    }

    @Test
    void testDeletePolicyCallsRepoAndUpdatesEdc() {
        policyService.deletePolicy("123");

        verify(policyRepository).deletePolicy("123");
        verify(edcNotificationContractService).updateNotificationContractDefinitions();
    }

    @Test
    void testDeletePolicyThrowsWhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> policyService.deletePolicy(null));
    }

    @Test
    void testDeletePolicyThrowsWhenIdIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> policyService.deletePolicy(""));
    }

    @Test
    void testDeletePolicyHandlesRepositoryFailure() {
        doThrow(new RuntimeException("Delete failed")).when(policyRepository).deletePolicy("123");

        assertThrows(RuntimeException.class, () -> policyService.deletePolicy("123"));
        verify(edcNotificationContractService, never()).updateNotificationContractDefinitions();
    }
}
