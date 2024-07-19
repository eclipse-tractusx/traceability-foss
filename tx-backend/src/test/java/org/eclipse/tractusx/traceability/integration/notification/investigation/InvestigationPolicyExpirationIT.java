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
package org.eclipse.tractusx.traceability.integration.notification.investigation;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import lombok.val;
import notification.request.NotificationSeverityRequest;
import notification.request.NotificationTypeRequest;
import notification.request.StartNotificationRequest;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.repository.AssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.common.request.OwnPageable;
import org.eclipse.tractusx.traceability.common.request.PageableFilterRequest;
import org.eclipse.tractusx.traceability.common.request.SearchCriteriaRequestParam;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AssetsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.DiscoveryFinderSupport;
import org.eclipse.tractusx.traceability.integration.common.support.EdcSupport;
import org.eclipse.tractusx.traceability.integration.common.support.IrsApiSupport;
import org.eclipse.tractusx.traceability.integration.common.support.NotificationApiSupport;
import org.eclipse.tractusx.traceability.integration.common.support.NotificationMessageSupport;
import org.eclipse.tractusx.traceability.integration.common.support.NotificationSupport;
import org.eclipse.tractusx.traceability.integration.common.support.OAuth2ApiSupport;
import org.eclipse.tractusx.traceability.notification.domain.notification.service.NotificationReceiverService;
import org.hamcrest.Matchers;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.SUPERVISOR;

public class InvestigationPolicyExpirationIT extends IntegrationTestSpecification {
    @Autowired
    NotificationReceiverService notificationReceiverService;
    @Autowired
    AssetsSupport assetsSupport;
    @Autowired
    NotificationMessageSupport notificationMessageSupport;
    @Autowired
    NotificationSupport notificationSupport;
    @Autowired
    AssetAsBuiltRepository assetAsBuiltRepository;
    @Autowired
    NotificationApiSupport notificationApiSupport;
    @Autowired
    EdcSupport edcSupport;

    @Autowired
    DiscoveryFinderSupport discoveryFinderSupport;

    @Autowired
    OAuth2ApiSupport oauth2ApiSupport;

    @Autowired
    IrsApiSupport irsApiSupport;

    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldNotApproveInvestigationStatus_whenPolicyIsExpired() throws JoseException, com.fasterxml.jackson.core.JsonProcessingException, FileNotFoundException {
        // given
        irsApiSupport.irsApiReturnsExpiredPolicy();
        irsApiSupport.irsApiCreatesPolicy();
        discoveryFinderSupport.discoveryFinderWillReturnEndpointAddress();
        discoveryFinderSupport.discoveryFinderWillReturnConnectorEndpoints();
        oauth2ApiSupport.oauth2ApiReturnsDtrToken();
        edcSupport.performSupportActionsForAsyncNotificationMessageExecutor();
        edcSupport.edcWillReturnContractDefinitions();
        edcSupport.edcWillRemoveContractDefinition();
        edcSupport.edcWillRemoveNotificationAsset();
        edcSupport.edcWillCreateAsset();
        edcSupport.edcWillCreatePolicyDefinition();
        edcSupport.edcWillRemovePolicyDefinition();
        edcSupport.edcWillCreateContractDefinition();
        List<String> partIds = List.of(
                "urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978", // BPN: BPNL00000003AYRE
                "urn:uuid:0ce83951-bc18-4e8f-892d-48bad4eb67ef"  // BPN: BPNL00000003AXS3
        );
        String description = "at least 15 characters long investigation description";

        assetsSupport.defaultAssetsStored();
        val startInvestigationRequest = StartNotificationRequest.builder()
                .affectedPartIds(partIds)
                .description(description)
                .severity(NotificationSeverityRequest.MINOR)
                .type(NotificationTypeRequest.INVESTIGATION)
                .receiverBpn("BPNL000000000DWF")
                .build();

        // this create will be not used, since irsApiSupport.irsApiReturnsExpiredPolicy(); returns the used policy
        // for this test
        given()
                .contentType(ContentType.JSON)
                .header(oAuth2Support.jwtAuthorization(SUPERVISOR))
                .when()
                .body(ResourceUtils.getFile("classpath:stubs/irs/policies/request_create_policy.json"))
                .post("/api/policies")
                .then()
                .log().all()
                .statusCode(200);

        // when
        val investigationId = notificationApiSupport.createNotificationRequest_withDefaultAssetsStored(oAuth2Support.jwtAuthorization(SUPERVISOR), startInvestigationRequest, 201);


        notificationSupport.assertInvestigationsSize(1);

        given()
                .contentType(ContentType.JSON)
                .header(oAuth2Support.jwtAuthorization(SUPERVISOR))
                .when()
                .post("/api/notifications/{investigationId}/approve", investigationId)
                .then()
                .log().all()
                .statusCode(503);

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(SUPERVISOR))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, Collections.emptyList()), new SearchCriteriaRequestParam(List.of("channel,EQUAL,SENDER,AND"))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .log().all()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1))
                .body("content[0].sendTo", Matchers.is(Matchers.not(Matchers.blankOrNullString())))
                .body("content[0].messages[0].errorMessage", Matchers.is("Failed to negotiate contract agreement: Policy [policy1] has expired."))
                .body("content[0].messages[1].errorMessage", Matchers.is("Failed to negotiate contract agreement: Policy [policy1] has expired."));

        notificationMessageSupport.assertMessageSize(2);

        //finally
        irsApiSupport.irsApiReturnsPolicies();
        given()
                .contentType(ContentType.JSON)
                .header(oAuth2Support.jwtAuthorization(SUPERVISOR))
                .when()
                .body(ResourceUtils.getFile("classpath:stubs/irs/policies/request_create_policy.json"))
                .post("/api/policies")
                .then()
                .log().all()
                .statusCode(200);
    }
}
