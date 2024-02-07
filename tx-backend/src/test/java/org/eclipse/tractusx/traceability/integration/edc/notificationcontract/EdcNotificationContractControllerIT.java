/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.integration.edc.notificationcontract;

import io.restassured.http.ContentType;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.EdcSupport;
import org.hamcrest.Matchers;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.*;
import static org.hamcrest.Matchers.blankString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class EdcNotificationContractControllerIT extends IntegrationTestSpecification {

    @Autowired
    EdcSupport edcSupport;

    /*
     * TODO (Pooja):
     * - Review and update PreAuthorize condition in EDCNotificationContract Controller as per Cofinity requirement
     * - According to Cofinity requirement, only ADMIN has permission
     * - According to Upstream requirement, ADMIN and SUPERVISOR have permission
     */
    //@Test
    void shouldCreateEdcContract() throws JoseException {
        // given
        edcSupport.edcWillCreateNotificationAsset();
        edcSupport.edcWillCreatePolicyDefinition();
        edcSupport.edcWillCreateContractDefinition();

        // then
        given()
                .contentType(ContentType.JSON)
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(
                        """
                                {
                                "notificationType" : "QUALITY_INVESTIGATION",
                                "notificationMethod" : "RECEIVE"
                                }
                                """
                )
                .when()
                .post("/api/edc/notification/contract")
                .then()
                .statusCode(201)
                .body("notificationAssetId", Matchers.is(not(blankString())))
                .body("accessPolicyId", Matchers.is(not(blankString())))
                .body("contractDefinitionId", Matchers.is(not(blankString())));

        edcSupport.verifyCreateNotificationAssetEndpointCalledTimes(1);
        edcSupport.verifyCreatePolicyDefinitionEndpointCalledTimes(1);
        edcSupport.verifyCreateContractDefinitionEndpointCalledTimes(1);

        edcSupport.verifyDeleteNotificationAssetEndpointCalledTimes(0);
        edcSupport.verifyDeletePolicyDefinitionEndpointCalledTimes(0);
        edcSupport.verifyDeleteContractDefinitionEndpointCalledTimes(0);
    }

    /*
     * TODO (Pooja):
     * - Review and update PreAuthorize condition in EDCNotificationContract Controller as per Cofinity requirement
     * - According to Cofinity requirement, only ADMIN has permission
     * - According to Upstream requirement, ADMIN and SUPERVISOR have permission
     */
    //@Test
    void shouldNotCreateEdcContractWhenNotificationAssetCreationFailed() throws JoseException {
        // given
        edcSupport.edcWillFailToCreateNotificationAsset();

        // then
        given()
                .contentType(ContentType.JSON)
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(
                        """
                                {
                                "notificationType" : "QUALITY_INVESTIGATION",
                                "notificationMethod" : "RECEIVE"
                                }
                                """
                )
                .when()
                .post("/api/edc/notification/contract")
                .then()
                .statusCode(500)
                .body("message", Matchers.is("Failed to create notification contract."));

        edcSupport.verifyCreateNotificationAssetEndpointCalledTimes(1);
        edcSupport.verifyCreatePolicyDefinitionEndpointCalledTimes(0);
        edcSupport.verifyCreateContractDefinitionEndpointCalledTimes(0);

        edcSupport.verifyDeleteNotificationAssetEndpointCalledTimes(0);
        edcSupport.verifyDeletePolicyDefinitionEndpointCalledTimes(0);
        edcSupport.verifyDeleteContractDefinitionEndpointCalledTimes(0);
    }

    /*
     * TODO (Pooja):
     * - Review and update PreAuthorize condition in EDCNotificationContract Controller as per Cofinity requirement
     * - According to Cofinity requirement, only ADMIN has permission
     * - According to Upstream requirement, ADMIN and SUPERVISOR have permission
     */
    //@Test
    void shouldNotCreateEdcContractAndDoRollbackWhenPolicyDefinitionCreationFailed() throws JoseException {
        // given
        edcSupport.edcWillCreateNotificationAsset();
        edcSupport.edcWillFailToCreatePolicyDefinition();

        edcSupport.edcWillRemoveNotificationAsset();

        // when/then
        given()
                .contentType(ContentType.JSON)
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(
                        """
                                {
                                "notificationType" : "QUALITY_INVESTIGATION",
                                "notificationMethod" : "RECEIVE"
                                }
                                """
                )
                .when()
                .post("/api/edc/notification/contract")
                .then()
                .statusCode(500)
                .body("message", Matchers.is("Failed to create notification contract."));

        edcSupport.verifyCreateNotificationAssetEndpointCalledTimes(1);
        edcSupport.verifyCreatePolicyDefinitionEndpointCalledTimes(1);
        edcSupport.verifyCreateContractDefinitionEndpointCalledTimes(0);

        edcSupport.verifyDeleteNotificationAssetEndpointCalledTimes(1);
        edcSupport.verifyDeletePolicyDefinitionEndpointCalledTimes(0);
        edcSupport.verifyDeleteContractDefinitionEndpointCalledTimes(0);
    }

    /*
     * TODO (Pooja):
     * - Review and update PreAuthorize condition in EDCNotificationContract Controller as per Cofinity requirement
     * - According to Cofinity requirement, only ADMIN has permission
     * - According to Upstream requirement, ADMIN and SUPERVISOR have permission
     */
    //@Test
    void shouldNotCreateEdcContractAndDoRollbackWhenContractDefinitionCreationFailed() throws JoseException {
        // given
        edcSupport.edcWillCreateNotificationAsset();
        edcSupport.edcWillCreatePolicyDefinition();
        edcSupport.edcWillFailToCreateContractDefinition();

        edcSupport.edcWillRemovePolicyDefinition();
        edcSupport.edcWillRemoveNotificationAsset();

        // then
        given()
                .contentType(ContentType.JSON)
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(
                        """
                                {
                                "notificationType" : "QUALITY_INVESTIGATION",
                                "notificationMethod" : "RECEIVE"
                                }
                                """
                )
                .when()
                .post("/api/edc/notification/contract")
                .then()
                .statusCode(500)
                .body("message", Matchers.is("Failed to create notification contract."));

        edcSupport.verifyCreateNotificationAssetEndpointCalledTimes(1);
        edcSupport.verifyCreatePolicyDefinitionEndpointCalledTimes(1);
        edcSupport.verifyCreateContractDefinitionEndpointCalledTimes(1);

        edcSupport.verifyDeleteNotificationAssetEndpointCalledTimes(1);
        edcSupport.verifyDeletePolicyDefinitionEndpointCalledTimes(1);
        edcSupport.verifyDeleteContractDefinitionEndpointCalledTimes(0);
    }

    /*
     * TODO (Pooja):
     * - Review and update PreAuthorize condition in EDCNotificationContract Controller as per Cofinity requirement
     * - According to Cofinity requirement, only ADMIN has permission
     * - According to Upstream requirement, ADMIN and SUPERVISOR have permission
     */
    //@Test
    void shouldNotCreateEdcContractWithoutAuthentication() {
        given()
                .contentType(ContentType.JSON)
                .body(
                        """
                                {
                                "notificationType" : "QUALITY_INVESTIGATION",
                                "notificationMethod" : "RECEIVE"
                                }
                                """
                )
                .when()
                .post("/api/edc/notification/contract")
                .then()
                .statusCode(401);
    }

    /*
     * TODO (Pooja):
     * - Review and update PreAuthorize condition in EDCNotificationContract Controller as per Cofinity requirement
     * - According to Cofinity requirement, only ADMIN has permission
     * - According to Upstream requirement, ADMIN and SUPERVISOR have permission
     */
    //@Test
    void shouldNotCreateEdcContractHavingSupervisorRole() throws JoseException {
        given()
                .contentType(ContentType.JSON)
                .header(oAuth2Support.jwtAuthorization(SUPERVISOR))
                .body(
                        """
                                {
                                "notificationType" : "QUALITY_INVESTIGATION",
                                "notificationMethod" : "RECEIVE"
                                }
                                """
                )
                .when()
                .post("/api/edc/notification/contract")
                .then()
                .statusCode(403);
    }

    /*
     * TODO (Pooja):
     * - Review and update PreAuthorize condition in EDCNotificationContract Controller as per Cofinity requirement
     * - According to Cofinity requirement, only ADMIN has permission
     * - According to Upstream requirement, ADMIN and SUPERVISOR have permission
     */
    //@Test
    void shouldNotCreateEdcContractHavingUserRole() throws JoseException {
        given()
                .contentType(ContentType.JSON)
                .header(oAuth2Support.jwtAuthorization(USER))
                .body(
                        """
                                {
                                "notificationType" : "QUALITY_INVESTIGATION",
                                "notificationMethod" : "RECEIVE"
                                }
                                """
                )
                .when()
                .post("/api/edc/notification/contract")
                .then()
                .statusCode(403);
    }

    /*
     * TODO (Pooja):
     * - Review and update PreAuthorize condition in EDCNotificationContract Controller as per Cofinity requirement
     * - According to Cofinity requirement, only ADMIN has permission
     * - According to Upstream requirement, ADMIN and SUPERVISOR have permission
     */
    //@ParameterizedTest
    @MethodSource("invalidRequests")
    void shouldNotCreateEdcContractWithInvalidRequest(
            final String notificationType,
            final String notificationMethod
    ) throws JoseException {
        given()
                .contentType(ContentType.JSON)
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(

                        """
                                {
                                "notificationType" : $notificationType,
                                "notificationMethod" : $notificationMethod
                                }
                                """.replace("$notificationType", notificationType)
                                .replace("$notificationMethod", notificationMethod)
                )
                .post("/api/edc/notification/contract")
                .then()
                .statusCode(400);
    }

    /*
     * TODO (Pooja):
     * - Review and update PreAuthorize condition in EDCNotificationContract Controller as per Cofinity requirement
     * - According to Cofinity requirement, only ADMIN has permission
     * - According to Upstream requirement, ADMIN and SUPERVISOR have permission
     */
    // @Test
    void shouldNotCreateEdcContractForQualityAlertBecauseItsNotYetImplemented() throws JoseException {
        given()
                .contentType(ContentType.JSON)
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(
                        """
                                {
                                "notificationType" : "QUALITY_ALERT",
                                "notificationMethod" : "RECEIVE"
                                }
                                """
                )
                .post("/api/edc/notification/contract")
                .then()
                .statusCode(500)
                .body("message", Matchers.is("Failed to create notification contract."));
    }

    private static Stream<Arguments> invalidRequests() {
        return Stream.of(
                arguments("null", "null"),
                arguments("", "null"),
                arguments("null", ""),
                arguments("", ""),
                arguments(" ", " "),
                arguments("\"invalid-notification-type\"", "\"RECEIVE\""),
                arguments("\"QUALITY_INVESTIGATION\"", "\"invalid-notification-method\"")
        );
    }
}
