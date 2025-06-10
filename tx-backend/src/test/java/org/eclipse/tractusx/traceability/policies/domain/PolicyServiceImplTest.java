package org.eclipse.tractusx.traceability.policies.domain;

import org.eclipse.tractusx.traceability.assets.domain.importpoc.exception.PolicyNotFoundException;
import org.eclipse.tractusx.traceability.notification.domain.contract.EdcNotificationContractService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.eclipse.tractusx.irs.edc.client.policy.Policy;
import policies.request.Context;
import policies.request.Payload;
import policies.request.RegisterPolicyRequest;
import policies.request.UpdatePolicyRequest;
import policies.response.CreatePolicyResponse;
import policies.response.IrsPolicyResponse;
import policies.response.PolicyResponse;

import java.time.OffsetDateTime;
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

    // === Helper ===

    private IrsPolicyResponse createPolicyResponse(String policyId) {
        Context context = mock(Context.class); // Context можно мокнуть
        Policy policy = new Policy(); // Можно заполнять нужными значениями при необходимости
        Payload payload = new Payload(context, policyId, policy);
        OffsetDateTime now = OffsetDateTime.now();
        return new IrsPolicyResponse(now, payload);
    }

    // === Nested groups ===

    @Nested
    @DisplayName("getIrsPolicies tests")
    class GetIrsPoliciesTests {

        @Test
        @DisplayName("returns policies map when present")
        void testReturnsPoliciesMap() {
            IrsPolicyResponse policy = createPolicyResponse("mock-policy-id");
            Map<String, List<IrsPolicyResponse>> policies = Map.of("type1", List.of(policy));

            when(policyRepository.getPolicies()).thenReturn(policies);

            Map<String, List<IrsPolicyResponse>> result = policyService.getIrsPolicies();

            assertEquals(policies, result);
            verify(policyRepository).getPolicies();
        }

        @Test
        @DisplayName("returns empty map when none exist")
        void testReturnsEmptyMapWhenNoneExist() {
            when(policyRepository.getPolicies()).thenReturn(Collections.emptyMap());

            Map<String, List<IrsPolicyResponse>> result = policyService.getIrsPolicies();

            assertTrue(result.isEmpty());
            verify(policyRepository).getPolicies();
        }
    }

    @Nested
    @DisplayName("getPolicies tests")
    class GetPoliciesTests {

        @Test
        @DisplayName("returns flattened list with correct content")
        void testReturnsFlattenedListWithCorrectContent() {
            IrsPolicyResponse policy = createPolicyResponse("mock-policy-id");
            Map<String, List<IrsPolicyResponse>> policies = Map.of("type1", List.of(policy));

            when(policyRepository.getPolicies()).thenReturn(policies);

            List<PolicyResponse> result = policyService.getPolicies();

            assertEquals(1, result.size());
            PolicyResponse response = result.get(0);
            assertEquals("mock-policy-id", response.policyId()); // .policyId() — для record

            verify(policyRepository).getPolicies();
        }

        @Test
        @DisplayName("returns empty list when no policies exist")
        void testReturnsEmptyListWhenNoneExist() {
            when(policyRepository.getPolicies()).thenReturn(Collections.emptyMap());

            List<PolicyResponse> result = policyService.getPolicies();

            assertTrue(result.isEmpty());
            verify(policyRepository).getPolicies();
        }

        @Test
        @DisplayName("returns flattened list with multiple policy types")
        void testWithMultiplePolicyTypes() {
            IrsPolicyResponse policy1 = createPolicyResponse("id1");
            IrsPolicyResponse policy2 = createPolicyResponse("id2");

            Map<String, List<IrsPolicyResponse>> policies = Map.of(
                    "type1", List.of(policy1),
                    "type2", List.of(policy2)
            );

            when(policyRepository.getPolicies()).thenReturn(policies);

            List<PolicyResponse> result = policyService.getPolicies();

            assertEquals(2, result.size());
            assertTrue(result.stream().anyMatch(p -> p.policyId().equals("id1")));
            assertTrue(result.stream().anyMatch(p -> p.policyId().equals("id2")));

            verify(policyRepository).getPolicies();
        }
    }

    @Nested
    @DisplayName("getPolicy tests")
    class GetPolicyTests {

        @Test
        @DisplayName("returns policy response when policy exists")
        void testReturnsPolicyResponse() {
            IrsPolicyResponse policy = createPolicyResponse("mock-policy-id");

            when(policyRepository.getPolicy("123")).thenReturn(Map.of("type1", Optional.of(policy)));

            PolicyResponse result = policyService.getPolicy("123");

            assertEquals("mock-policy-id", result.policyId());

            verify(policyRepository).getPolicy("123");
        }

        @Test
        @DisplayName("throws PolicyNotFoundException when policy missing")
        void testThrowsWhenNotFound() {
            when(policyRepository.getPolicy("missing")).thenReturn(Map.of());

            assertThrows(PolicyNotFoundException.class, () -> policyService.getPolicy("missing"));
            verify(policyRepository).getPolicy("missing");
        }

        @Test
        @DisplayName("throws IllegalArgumentException when id is null")
        void testThrowsWhenIdIsNull() {
            assertThrows(IllegalArgumentException.class, () -> policyService.getPolicy(null));
            verifyNoInteractions(policyRepository);
        }

        @Test
        @DisplayName("throws IllegalArgumentException when id is empty")
        void testThrowsWhenIdIsEmpty() {
            assertThrows(IllegalArgumentException.class, () -> policyService.getPolicy(""));
            verifyNoInteractions(policyRepository);
        }
    }

    @Nested
    @DisplayName("createPolicy tests")
    class CreatePolicyTests {

        @Test
        @DisplayName("creates policy and updates EDC")
        void testCreatesPolicyAndUpdatesEdc() {
            RegisterPolicyRequest request = mock(RegisterPolicyRequest.class);
            CreatePolicyResponse response = mock(CreatePolicyResponse.class);

            when(policyRepository.createPolicy(request)).thenReturn(response);

            CreatePolicyResponse result = policyService.createPolicy(request);

            assertEquals(response, result);
            verify(policyRepository).createPolicy(request);
            verify(edcNotificationContractService).updateNotificationContractDefinitions();
        }

        @Test
        @DisplayName("throws IllegalArgumentException when request is null")
        void testThrowsWhenRequestIsNull() {
            assertThrows(IllegalArgumentException.class, () -> policyService.createPolicy(null));
            verifyNoInteractions(policyRepository, edcNotificationContractService);
        }

        @Test
        @DisplayName("handles repository exception gracefully")
        void testHandlesRepositoryFailure() {
            RegisterPolicyRequest request = mock(RegisterPolicyRequest.class);
            when(policyRepository.createPolicy(request)).thenThrow(new RuntimeException("DB error"));

            assertThrows(RuntimeException.class, () -> policyService.createPolicy(request));
            verify(edcNotificationContractService, never()).updateNotificationContractDefinitions();
        }
    }

    @Nested
    @DisplayName("updatePolicy tests")
    class UpdatePolicyTests {

        @Test
        @DisplayName("calls repository and updates EDC")
        void testCallsRepoAndUpdatesEdc() {
            UpdatePolicyRequest request = mock(UpdatePolicyRequest.class);

            policyService.updatePolicy(request);

            verify(policyRepository).updatePolicy(request);
            verify(edcNotificationContractService).updateNotificationContractDefinitions();
        }

        @Test
        @DisplayName("throws IllegalArgumentException when request is null")
        void testThrowsWhenRequestIsNull() {
            assertThrows(IllegalArgumentException.class, () -> policyService.updatePolicy(null));
            verifyNoInteractions(policyRepository, edcNotificationContractService);
        }

        @Test
        @DisplayName("handles repository failure gracefully")
        void testHandlesRepositoryFailure() {
            UpdatePolicyRequest request = mock(UpdatePolicyRequest.class);
            doThrow(new RuntimeException("Update error")).when(policyRepository).updatePolicy(request);

            assertThrows(RuntimeException.class, () -> policyService.updatePolicy(request));
            verify(edcNotificationContractService, never()).updateNotificationContractDefinitions();
        }
    }

    @Nested
    @DisplayName("deletePolicy tests")
    class DeletePolicyTests {

        @Test
        @DisplayName("deletes policy and updates EDC")
        void testDeletesPolicyAndUpdatesEdc() {
            policyService.deletePolicy("123");

            verify(policyRepository).deletePolicy("123");
            verify(edcNotificationContractService).updateNotificationContractDefinitions();
        }

        @Test
        @DisplayName("throws IllegalArgumentException when id is null")
        void testThrowsWhenIdIsNull() {
            assertThrows(IllegalArgumentException.class, () -> policyService.deletePolicy(null));
            verifyNoInteractions(policyRepository, edcNotificationContractService);
        }

        @Test
        @DisplayName("throws IllegalArgumentException when id is empty")
        void testThrowsWhenIdIsEmpty() {
            assertThrows(IllegalArgumentException.class, () -> policyService.deletePolicy(""));
            verifyNoInteractions(policyRepository, edcNotificationContractService);
        }

        @Test
        @DisplayName("handles repository failure gracefully")
        void testHandlesRepositoryFailure() {
            doThrow(new RuntimeException("Delete failed")).when(policyRepository).deletePolicy("123");

            assertThrows(RuntimeException.class, () -> policyService.deletePolicy("123"));
            verify(edcNotificationContractService, never()).updateNotificationContractDefinitions();
        }
    }
}
