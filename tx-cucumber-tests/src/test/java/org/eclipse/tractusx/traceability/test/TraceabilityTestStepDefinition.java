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
import org.awaitility.Duration;
import org.eclipse.tractusx.traceability.test.tooling.TraceXEnvironmentEnum;
import org.eclipse.tractusx.traceability.test.tooling.rest.RestProvider;
import org.eclipse.tractusx.traceability.test.tooling.rest.response.QualityNotificationResponse;
import org.eclipse.tractusx.traceability.test.validator.NotificationValidator;
import org.hamcrest.Matchers;
import qualitynotification.base.response.QualityNotificationIdResponse;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.eclipse.tractusx.traceability.test.tooling.NotificationTypeEnum.ALERT;
import static org.eclipse.tractusx.traceability.test.tooling.NotificationTypeEnum.INVESTIGATION;
import static org.eclipse.tractusx.traceability.test.tooling.TraceXEnvironmentEnum.TRACE_X_A;
import static org.eclipse.tractusx.traceability.test.tooling.TraceXEnvironmentEnum.TRACE_X_B;
import static org.eclipse.tractusx.traceability.test.tooling.rest.request.UpdateQualityNotificationStatusRequest.ACCEPTED;
import static org.eclipse.tractusx.traceability.test.tooling.rest.request.UpdateQualityNotificationStatusRequest.ACKNOWLEDGED;
import static org.eclipse.tractusx.traceability.test.tooling.rest.request.UpdateQualityNotificationStatusRequest.DECLINED;
import static org.eclipse.tractusx.traceability.test.validator.TestUtils.normalize;
import static org.eclipse.tractusx.traceability.test.validator.TestUtils.wrapStringWithTimestamp;

@Slf4j
public class TraceabilityTestStepDefinition {

    private RestProvider restProvider;
    private Long notificationID_TXA = null;
    private Long notificationID_TXB = null;
    private String notificationDescription = null;
    private List<AssetAsBuiltResponse> requestedAssets;


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

    @Given("I create quality investigation")
    public void iCreateQualityInvestigation(DataTable dataTable) {
        final Map<String, String> input = normalize(dataTable.asMap());
        final String assetId = "urn:uuid:7eeeac86-7b69-444d-81e6-655d0f1513bd";

        notificationDescription = wrapStringWithTimestamp(input.get("description"));

        final Instant targetDate = input.get("targetDate") == null ? null : Instant.parse(input.get("targetDate"));

        final String severity = input.get("severity");

        final QualityNotificationIdResponse idResponse = restProvider.createNotification(
                List.of(assetId),
                notificationDescription,
                targetDate,
                severity,
                null,
                INVESTIGATION
        );
        notificationID_TXA = idResponse.id();
        assertThat(dataTable).isNotNull();
    }

    @When("I check, if quality investigation has proper values")
    public void iCheckIfQualityInvestigationHasProperValues(DataTable dataTable) {
        await()
                .atMost(Duration.TWO_MINUTES)
                .pollInterval(1, TimeUnit.SECONDS)
                .catchUncaughtExceptions()
                .until(() -> {
                    try {
                        QualityNotificationResponse result = restProvider.getNotification(getNotificationIdBasedOnEnv(), INVESTIGATION);
                        NotificationValidator.assertHasFields(result, normalize(dataTable.asMap()));
                        return true;
                    } catch (AssertionError assertionError) {
                        return false;
                    }
                        }
                );
    }

    @When("I approve quality investigation")
    public void iApproveQualityInvestigation() {
        restProvider.approveNotification(getNotificationIdBasedOnEnv(), INVESTIGATION);
    }

    @When("I cancel quality investigation")
    public void iCancelQualityInvestigation() {
        restProvider.cancelNotification(getNotificationIdBasedOnEnv(), INVESTIGATION);
    }

    @When("I cancel quality alert")
    public void iCancelQualityAlert() {
        restProvider.cancelNotification(getNotificationIdBasedOnEnv(), ALERT);
    }

    @When("I close quality investigation")
    public void iCloseQualityInvestigation() {
        restProvider.closeNotification(getNotificationIdBasedOnEnv(), INVESTIGATION);
    }

    @When("I close quality alert")
    public void iCloseQualityAlert() {
        restProvider.closeNotification(getNotificationIdBasedOnEnv(), ALERT);
    }

    @When("I check, if quality investigation has been received")
    public void iCanSeeNotificationWasReceived() {
        System.out.println("searching for notificationDescription: " + notificationDescription);
        final QualityNotificationResponse notification = await()
                .atMost(Duration.TWO_MINUTES)
                .pollInterval(1, TimeUnit.SECONDS)
                .until(() -> {
                    final List<QualityNotificationResponse> result = restProvider.getReceivedNotifications(INVESTIGATION);
                            result.stream().map(QualityNotificationResponse::getDescription).forEach(System.out::println);
                            return result.stream().filter(qn -> Objects.equals(qn.getDescription(), notificationDescription)).findFirst().orElse(null);
                        }, Matchers.notNullValue()
                );

        notificationID_TXB = notification.getId();

        assertThat(notification).isNotNull();
    }

    @When("I check, if quality investigation has not been received")
    public void iCanSeeInvestigationWasNotReceived() {
        final List<QualityNotificationResponse> result = restProvider.getReceivedNotifications(INVESTIGATION);
        Optional<QualityNotificationResponse> first = result.stream()
                .filter(qualityNotificationResponse -> Objects.equals(qualityNotificationResponse.getId(), getNotificationIdBasedOnEnv()))
                .findFirst();
        assertThat(first.isEmpty()).isTrue();
    }

    @When("I check, if quality alert has not been received")
    public void iCanSeeAlertWasNotReceived() {
        final List<QualityNotificationResponse> result = restProvider.getReceivedNotifications(ALERT);
        Optional<QualityNotificationResponse> first = result.stream()
                .filter(qualityNotificationResponse -> Objects.equals(qualityNotificationResponse.getId(), getNotificationIdBasedOnEnv()))
                .findFirst();
        assertThat(first.isEmpty()).isTrue();
    }

    @When("I acknowledge quality investigation")
    public void iAcknowledgeQualityInvestigation() {
        restProvider.updateNotification(INVESTIGATION, getNotificationIdBasedOnEnv(), ACKNOWLEDGED, "");
    }

    @When("I accept quality investigation")
    public void iAcceptQualityInvestigation(DataTable dataTable) {
        String reason = normalize(dataTable.asMap()).get("reason");
        System.out.println("reason: " + reason);
        restProvider.updateNotification(INVESTIGATION, getNotificationIdBasedOnEnv(), ACCEPTED, reason);
    }

    @When("I decline quality investigation")
    public void iDeclineQualityInvestigation(DataTable dataTable) {
        String reason = normalize(dataTable.asMap()).get("reason");
        System.out.println("reason: " + reason);
        restProvider.updateNotification(INVESTIGATION, getNotificationIdBasedOnEnv(), DECLINED, reason);
    }

    @When("I decline quality alert")
    public void iDeclineQualityAlert(DataTable dataTable) {
        String reason = normalize(dataTable.asMap()).get("reason");
        System.out.println("reason: " + reason);
        restProvider.updateNotification(ALERT, getNotificationIdBasedOnEnv(), DECLINED, reason);
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

    @And("I create quality investigation with two parts")
    public void iCreateQualityInvestigationWithTwoParts(DataTable dataTable) {
        final Map<String, String> input = normalize(dataTable.asMap());
        final String[] assetId = {
                "urn:uuid:5205f736-8fc2-4585-b869-6bf36842369a",
                "urn:uuid:7eeeac86-7b69-444d-81e6-655d0f1513bd"
        };
        notificationDescription = wrapStringWithTimestamp(input.get("description"));

        final Instant targetDate = input.get("targetDate") == null ? null : Instant.parse(input.get("targetDate"));

        final String severity = input.get("severity");

        final QualityNotificationIdResponse idResponse = restProvider.createNotification(
                List.of(assetId),
                notificationDescription,
                targetDate,
                severity,
                null,
                INVESTIGATION
        );
        notificationID_TXA = idResponse.id();
        assertThat(dataTable).isNotNull();
    }

    @And("I create quality alert with two parts")
    public void iCreateQualityAlertWithTwoParts(DataTable dataTable) {
        final Map<String, String> input = normalize(dataTable.asMap());
        final String[] assetId = {
                "urn:uuid:5205f736-8fc2-4585-b869-6bf36842369a",
                "urn:uuid:7eeeac86-7b69-444d-81e6-655d0f1513bd"
        };
        notificationDescription = wrapStringWithTimestamp(input.get("description"));

        final Instant targetDate = input.get("targetDate") == null ? null : Instant.parse(input.get("targetDate"));

        final String severity = input.get("severity");

        final QualityNotificationIdResponse idResponse = restProvider.createNotification(
                List.of(assetId),
                notificationDescription,
                targetDate,
                severity,
                "BPNL00000003CNKC",
                ALERT
        );
        notificationID_TXA = idResponse.id();
        assertThat(dataTable).isNotNull();
    }


    @Given("I create quality alert")
    public void iCreateQualityAlert(DataTable dataTable) {

        final Map<String, String> input = normalize(dataTable.asMap());
        final String assetId = "urn:uuid:5205f736-8fc2-4585-b869-6bf36842369a";

        notificationDescription = wrapStringWithTimestamp(input.get("description"));

        final Instant targetDate = input.get("targetDate") == null ? null : Instant.parse(input.get("targetDate"));

        final String severity = input.get("severity");

        final QualityNotificationIdResponse idResponse = restProvider.createNotification(
                List.of(assetId),
                notificationDescription,
                targetDate,
                severity,
                "BPNL00000003CNKC",
                ALERT
        );
        notificationID_TXA = idResponse.id();
        assertThat(dataTable).isNotNull();
    }

    @When("I check, if quality alert has proper values")
    public void iCheckIfQualityAlertHasProperValues(DataTable dataTable) {
        await()
                .atMost(Duration.TWO_MINUTES)
                .pollInterval(1, TimeUnit.SECONDS)
                .ignoreExceptions()
                .until(() -> {
                    try {
                        QualityNotificationResponse result = restProvider.getNotification(getNotificationIdBasedOnEnv(), ALERT);
                        NotificationValidator.assertHasFields(result, normalize(dataTable.asMap()));
                        return true;
                    } catch (AssertionError assertionError) {
                        return false;
                    }
                        }
                );
    }


    @When("I approve quality alert")
    public void iApproveQualityAlert() {
        restProvider.approveNotification(getNotificationIdBasedOnEnv(), ALERT);
    }


    @When("I check, if quality alert has been received")
    public void iCanSeeQualityAlertWasReceived() {
        System.out.println("searching for notificationDescription: " + notificationDescription);
        final QualityNotificationResponse notification = await()
                .atMost(Duration.TWO_MINUTES)
                .pollInterval(1, TimeUnit.SECONDS)
                .until(() -> {
                            final List<QualityNotificationResponse> result = restProvider.getReceivedNotifications(ALERT);
                            result.stream().map(QualityNotificationResponse::getDescription).forEach(System.out::println);
                            return result.stream().filter(qn -> Objects.equals(qn.getDescription(), notificationDescription)).findFirst().orElse(null);
                        }, Matchers.notNullValue()
                );

        notificationID_TXB = notification.getId();

        assertThat(notification).isNotNull();
    }

    @When("I acknowledge quality alert")
    public void iAcknowledgeQualityAlert() {
        restProvider.updateNotification(ALERT, getNotificationIdBasedOnEnv(), ACKNOWLEDGED, "");
    }


    @When("I accept quality alert")
    public void iAcceptQualityAlert(DataTable dataTable) {
        String reason = normalize(dataTable.asMap()).get("reason");
        System.out.println("reason: " + reason);
        restProvider.updateNotification(ALERT, getNotificationIdBasedOnEnv(), ACCEPTED, reason);
    }
}
