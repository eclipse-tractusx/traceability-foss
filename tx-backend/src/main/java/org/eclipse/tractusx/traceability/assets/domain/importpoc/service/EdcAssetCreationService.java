/********************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package org.eclipse.tractusx.traceability.assets.domain.importpoc.service;

import assets.importpoc.ConstraintResponse;
import assets.importpoc.ConstraintsResponse;
import assets.importpoc.PermissionResponse;
import assets.importpoc.PolicyResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.irs.edc.client.asset.EdcAssetService;
import org.eclipse.tractusx.irs.edc.client.asset.model.OdrlContext;
import org.eclipse.tractusx.irs.edc.client.asset.model.exception.CreateEdcAssetException;
import org.eclipse.tractusx.irs.edc.client.asset.model.exception.EdcAssetAlreadyExistsException;
import org.eclipse.tractusx.irs.edc.client.contract.model.EdcOperator;
import org.eclipse.tractusx.irs.edc.client.contract.model.exception.CreateEdcContractDefinitionException;
import org.eclipse.tractusx.irs.edc.client.contract.service.EdcContractDefinitionService;
import org.eclipse.tractusx.irs.edc.client.policy.model.EdcCreatePolicyDefinitionRequest;
import org.eclipse.tractusx.irs.edc.client.policy.model.EdcPolicy;
import org.eclipse.tractusx.irs.edc.client.policy.model.EdcPolicyPermission;
import org.eclipse.tractusx.irs.edc.client.policy.model.EdcPolicyPermissionConstraint;
import org.eclipse.tractusx.irs.edc.client.policy.model.EdcPolicyPermissionConstraintExpression;
import org.eclipse.tractusx.irs.edc.client.policy.model.exception.CreateEdcPolicyDefinitionException;
import org.eclipse.tractusx.irs.edc.client.policy.model.exception.EdcPolicyDefinitionAlreadyExists;
import org.eclipse.tractusx.irs.edc.client.policy.service.EdcPolicyDefinitionService;
import org.eclipse.tractusx.traceability.assets.application.importpoc.PolicyService;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.exception.CreateEdcResourcesException;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EdcAssetCreationService {
    private static final String REGISTRY_ASSET_ID = "registry-asset";
    private final EdcAssetService edcDtrAssetService;
    private final EdcPolicyDefinitionService edcDtrPolicyDefinitionService;
    private final EdcContractDefinitionService edcDtrContractDefinitionService;
    private final TraceabilityProperties traceabilityProperties;
    private final PolicyService policyService;
    @Value("${registry.urlWithPath}")
    String registryUrlWithPath = null;

    public String createDtrAndSubmodelAssets(String policyId) {
        PolicyResponse policy = policyService.getPolicyById(policyId);
        String createdPolicyId;
        try {
            createdPolicyId = edcDtrPolicyDefinitionService.createAccessPolicy(mapToEdcPolicyRequest(policy));
            log.info("DTR Policy Id created :{}", createdPolicyId);
        } catch (CreateEdcPolicyDefinitionException e) {
            throw new CreateEdcResourcesException(e);
        } catch (EdcPolicyDefinitionAlreadyExists e) {
            createdPolicyId = policyId;
        }


        String dtrAssetId;
        try {
            dtrAssetId = edcDtrAssetService.createDtrAsset(registryUrlWithPath, REGISTRY_ASSET_ID);
            log.info("DTR Asset Id created :{}", dtrAssetId);
        } catch (CreateEdcAssetException e) {
            throw new CreateEdcResourcesException(e);
        } catch (EdcAssetAlreadyExistsException e) {
            dtrAssetId = REGISTRY_ASSET_ID;
        }


        String dtrContractId;
        try {
            dtrContractId = edcDtrContractDefinitionService.createContractDefinition(dtrAssetId, createdPolicyId);
            log.info("DTR Contract Id created :{}", dtrContractId);
        } catch (CreateEdcContractDefinitionException e) {
            throw new CreateEdcResourcesException(e);
        }


        String submodelAssetId;
        String submodelAssetIdToCreate = "urn:uuid:" + UUID.randomUUID();
        try {
            submodelAssetId = edcDtrAssetService.createSubmodelAsset(traceabilityProperties.getSubmodelBase() + "/api/submodel", submodelAssetIdToCreate);
            log.info("Submodel Asset Id created :{}", submodelAssetId);
        } catch (CreateEdcAssetException e) {
            throw new CreateEdcResourcesException(e);
        } catch (EdcAssetAlreadyExistsException e) {
            submodelAssetId = submodelAssetIdToCreate;
        }


        String submodelContractId;
        try {
            submodelContractId = edcDtrContractDefinitionService.createContractDefinition(submodelAssetId, createdPolicyId);
        } catch (CreateEdcContractDefinitionException e) {
            throw new CreateEdcResourcesException(e);
        }
        log.info("Submodel Contract Id created :{}", submodelContractId);
        return submodelAssetId;
    }

    private EdcCreatePolicyDefinitionRequest mapToEdcPolicyRequest(PolicyResponse policy) {
        OdrlContext odrlContext = OdrlContext.builder().odrl("http://www.w3.org/ns/odrl/2/").build();
        EdcPolicy edcPolicy = EdcPolicy.builder().odrlPermissions(mapToPermissions(policy.permissions())).type("Policy").build();
        return EdcCreatePolicyDefinitionRequest.builder()
                .policyDefinitionId(policy.policyId())
                .policy(edcPolicy)
                .odrlContext(odrlContext)
                .type("PolicyDefinitionRequestDto")
                .build();
    }

    private List<EdcPolicyPermission> mapToPermissions(List<PermissionResponse> permissions) {
        return permissions.stream().map(permission -> EdcPolicyPermission.builder()
                .action(permission.action().name())
                .edcPolicyPermissionConstraints(mapToConstraint(permission.constraints()))
                .build()
        ).toList();
    }

    private EdcPolicyPermissionConstraint mapToConstraint(ConstraintsResponse constraintsResponse) {
        return EdcPolicyPermissionConstraint.builder()
                .type("AtomicConstraint")
                .orExpressions(mapToConstraintExpression(constraintsResponse.or()))
                .build();
    }

    private List<EdcPolicyPermissionConstraintExpression> mapToConstraintExpression(List<ConstraintResponse> constraints) {
        return constraints.stream().map(constraint -> EdcPolicyPermissionConstraintExpression.builder()
                        .type("Constraint")
                        .leftOperand(constraint.leftOperand())
                        .rightOperand(constraint.rightOperand())
                        .operator(EdcOperator.builder()
                                .operatorId("odrl:" + constraint.operatorTypeResponse().getCode())
                                .build())
                        .build())
                .toList();
    }
}
