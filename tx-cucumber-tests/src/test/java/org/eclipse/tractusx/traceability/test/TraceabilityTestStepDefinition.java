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

package org.eclipse.tractusx.traceability.test;

import assets.response.asbuilt.AssetAsBuiltResponse;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import notification.request.UpdateNotificationStatusRequest;
import notification.response.NotificationIdResponse;
import notification.response.NotificationResponse;
import org.awaitility.Durations;
import org.eclipse.tractusx.traceability.test.exception.MissingStepDefinitionException;
import org.eclipse.tractusx.traceability.test.tooling.NotificationTypeEnum;
import org.eclipse.tractusx.traceability.test.tooling.TraceXEnvironmentEnum;
import org.eclipse.tractusx.traceability.test.tooling.rest.RestProvider;
import org.eclipse.tractusx.traceability.test.validator.NotificationValidator;
import org.hamcrest.Matchers;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.eclipse.tractusx.traceability.test.tooling.TraceXEnvironmentEnum.TRACE_X_A;
import static org.eclipse.tractusx.traceability.test.tooling.TraceXEnvironmentEnum.TRACE_X_B;
import static org.eclipse.tractusx.traceability.test.validator.TestUtils.normalize;
import static org.eclipse.tractusx.traceability.test.validator.TestUtils.wrapStringWithUUID;

@Slf4j
public class TraceabilityTestStepDefinition {

    private RestProvider restProvider;
    private Long notificationID_TXA = null;
    private Long notificationID_TXB = null;
    protected static final String BPN_TXA = System.getProperty("txa.bpn");
    protected static final String BPN_TXB = System.getProperty("txb.bpn");
    private String notificationDescription = null;
    private List<AssetAsBuiltResponse> requestedAssets;
    private List<String> testAssets;


    @ParameterType("TRACE_X_A|TRACE_X_B")
    public TraceXEnvironmentEnum TraceXEnvironmentEnum(String environment) {
        return TraceXEnvironmentEnum.valueOf(environment);
    }

    @Before
    public void setup() {
        restProvider = new RestProvider();
    }

    @Given("I am logged into {TraceXEnvironmentEnum} application")
    public void iAmLoggedIntoApplication(TraceXEnvironmentEnum environment) {
        restProvider.loginToEnvironment(environment);
    }

    @Given("I use assets with ids {string}")
    public void iUseAssetWithIds(String assetIds) {
        testAssets = Arrays.stream(assetIds.split(",")).toList();
    }

    @Given("I create quality notification")
    public void iCreateQualityNotification(DataTable dataTable) {
        final Map<String, String> input = normalize(dataTable.asMap());

        if (isEmpty(testAssets)) {
            throw MissingStepDefinitionException.missingAssetDefinition();
        }

        notificationDescription = wrapStringWithUUID(input.get("description"));

        final Instant targetDate = input.get("targetDate") == null ? null : Instant.parse(input.get("targetDate"));

        final String severity = input.get("severity");

        final NotificationTypeEnum type = NotificationTypeEnum.valueOf(input.get("type"));

        final String title = input.get("title");

        final NotificationIdResponse idResponse = restProvider.createNotification(
                testAssets,
                notificationDescription,
                targetDate,
                severity,
                BPN_TXB,
                title,
                type
        );
        notificationID_TXA = idResponse.id();
        assertThat(dataTable).isNotNull();
    }

    @When("I edit quality notification")
    public void iEditQualityNotification(DataTable dataTable) {
        final Map<String, String> input = normalize(dataTable.asMap());

        if (isEmpty(testAssets)) {
            throw MissingStepDefinitionException.missingAssetDefinition();
        }

        notificationDescription = wrapStringWithUUID(input.get("description"));

        final Instant targetDate = input.get("targetDate") == null ? null : Instant.parse(input.get("targetDate"));

        final String severity = input.get("severity");
        final String title = input.get("title");

        restProvider.editNotification(
                getNotificationIdBasedOnEnv(),
                testAssets,
                notificationDescription,
                targetDate,
                severity,
                title,
                BPN_TXB

        );
        notificationID_TXA = getNotificationIdBasedOnEnv();
        assertThat(dataTable).isNotNull();
    }

    @When("I check, if quality notification has proper values")
    public void iCheckIfQualityNotificationHasProperValues(DataTable dataTable) {
        await()
                .atMost(Durations.FIVE_MINUTES)
                .pollInterval(1, TimeUnit.SECONDS)
                .ignoreExceptions()
                .until(() -> {
                            try {
                                NotificationResponse result = restProvider.getNotification(getNotificationIdBasedOnEnv());
                                NotificationValidator.assertHasFields(result, normalize(dataTable.asMap()));
                                return true;
                            } catch (AssertionError assertionError) {
                                assertionError.printStackTrace();
                                return false;
                            }
                        }
                );
    }

    @When("I approve quality notification")
    public void iApproveQualityNotification() {
        restProvider.approveNotification(getNotificationIdBasedOnEnv());
    }

    @When("I cancel quality notification")
    public void iCancelQualityNotification() {
        restProvider.cancelNotification(getNotificationIdBasedOnEnv());
    }

    @When("I close quality notification")
    public void iCloseQualityNotification() {
        restProvider.closeNotification(getNotificationIdBasedOnEnv());
    }


    @When("I check, if quality notification has been received")
    public void iCanSeeNotificationWasReceived() {
        System.out.println("searching for notificationDescription: " + notificationDescription);
        final NotificationResponse notification = await()
                .atMost(Durations.FIVE_MINUTES)
                .pollInterval(1, TimeUnit.SECONDS)
                .until(() -> {
                            final List<NotificationResponse> result = restProvider.getReceivedNotifications();
                            result.stream().map(NotificationResponse::getDescription).forEach(System.out::println);
                            return result.stream().filter(qn -> Objects.equals(qn.getDescription(), notificationDescription)).findFirst().orElse(null);
                        }, Matchers.notNullValue()
                );

        notificationID_TXB = notification.getId();

        assertThat(notification).isNotNull();
    }

    @When("I check, if quality notification has not been received")
    public void iCanSeeNotificationWasNotReceived() {
        final List<NotificationResponse> result = restProvider.getReceivedNotifications();
        Optional<NotificationResponse> first = result.stream()
                .filter(qualityNotificationResponse -> Objects.equals(qualityNotificationResponse.getId(), getNotificationIdBasedOnEnv()))
                .findFirst();
        assertThat(first).isNotPresent();
    }

    @When("I acknowledge quality notification")
    public void iAcknowledgeQualityNotification() {
        restProvider.updateNotification(getNotificationIdBasedOnEnv(), UpdateNotificationStatusRequest.ACKNOWLEDGED, "");
    }

    @When("I accept quality notification")
    public void iAcceptQualityNotification(DataTable dataTable) {
        String reason = normalize(dataTable.asMap()).get("reason");
        System.out.println("reason: " + reason);
        restProvider.updateNotification(getNotificationIdBasedOnEnv(), UpdateNotificationStatusRequest.ACCEPTED, reason);
    }

    @When("I decline quality notification")
    public void iDeclineQualityNotification(DataTable dataTable) {
        String reason = normalize(dataTable.asMap()).get("reason");
        System.out.println("reason: " + reason);
        restProvider.updateNotification(getNotificationIdBasedOnEnv(), UpdateNotificationStatusRequest.DECLINED, reason);
    }

    private Long getNotificationIdBasedOnEnv() {
        if (restProvider.getCurrentEnv().equals(TRACE_X_A)) {
            return notificationID_TXA;
        }
        if (restProvider.getCurrentEnv().equals(TRACE_X_B)) {
            return notificationID_TXB;
        }
        throw new UnsupportedOperationException("First need to Log In");
    }

    @And("I request assets with {string}")
    public void iRequestAssetsWith(String ownerFilter) {
        requestedAssets = restProvider.getAssets(ownerFilter);
    }

    @Then("I check, if only assets with {string} are responded")
    public void iCheckIfOnlyAssetsWithOwnerFilterAreResponded(String ownerFilter) {
        requestedAssets.forEach(asset -> assertThat(ownerFilter).isEqualTo(asset.getOwner().toString()));
    }

}
