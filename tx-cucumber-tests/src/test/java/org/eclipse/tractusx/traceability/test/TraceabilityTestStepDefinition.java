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

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.test.tooling.TraceXEnvironmentEnum;
import org.eclipse.tractusx.traceability.test.tooling.rest.RestProvider;
import org.eclipse.tractusx.traceability.test.tooling.rest.request.UpdateQualityNotificationStatusRequest;
import org.eclipse.tractusx.traceability.test.tooling.rest.response.QualityNotificationIdResponse;
import org.eclipse.tractusx.traceability.test.tooling.rest.response.QualityNotificationResponse;
import org.eclipse.tractusx.traceability.test.validator.InvestigationValidator;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.tractusx.traceability.test.tooling.TraceXEnvironmentEnum.TRACE_X_A;
import static org.eclipse.tractusx.traceability.test.tooling.TraceXEnvironmentEnum.TRACE_X_B;
import static org.eclipse.tractusx.traceability.test.validator.StringUtils.wrapStringWithTimestamp;

@Slf4j
public class TraceabilityTestStepDefinition {

    private RestProvider restProvider;
    private Long notificationID_TXA = null;
    private Long notificationID_TXB = null;
    private String notificationDescription = null;

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

        final QualityNotificationIdResponse idResponse = restProvider.createInvestigation(
                List.of(assetId),
                notificationDescription,
                targetDate,
                severity
        );
        notificationID_TXA = idResponse.id();
        assertThat(dataTable).isNotNull();
    }

    @When("I check, if quality investigation has proper values")
    public void iCheckIfQualityInvestigationHasProperValues(DataTable dataTable) {
        final QualityNotificationResponse result = restProvider.getInvestigation(getNotificationIdBasedOnEnv());
        InvestigationValidator.validateInvestigation(result, normalize(dataTable.asMap()));
    }

    @When("I approve quality investigation")
    public void iApproveQualityInvestigation() {
        restProvider.approveInvestigation(getNotificationIdBasedOnEnv());
        waiting1Min();
    }

    @When("I cancel quality investigation")
    public void iCancelQualityInvestigation() {
        restProvider.cancelInvestigation(getNotificationIdBasedOnEnv());
        waiting1Min();
    }

    @When("I close quality investigation")
    public void iCloseQualityInvestigation() {
        restProvider.closeInvestigation(getNotificationIdBasedOnEnv());
        waiting1Min();
    }

    @When("I check, if quality investigation has been received")
    public void iCanSeeNotificationWasReceived() {
        final List<QualityNotificationResponse> result = restProvider.getReceivedNotifications();
        final QualityNotificationResponse notification = result.stream().filter(qn -> Objects.equals(qn.getDescription(), notificationDescription)).findFirst().orElseThrow();
        notificationID_TXB = notification.getId();

        assertThat(notification).isNotNull();
    }

    @When("I check, if quality investigation has not been received")
    public void iCanSeeNotificationWasNotReceived() {
        final List<QualityNotificationResponse> result = restProvider.getReceivedNotifications();
        assertThat(result.size()).isEqualTo(0);
    }

    @When("I acknowledge quality investigation")
    public void iAcknowledgeQualityInvestigation() {
        restProvider.updateInvestigation(getNotificationIdBasedOnEnv(), UpdateQualityNotificationStatusRequest.ACKNOWLEDGED);
        waiting1Min();
    }

    @When("I accept quality investigation")
    public void iAcceptQualityInvestigation() {
        restProvider.updateInvestigation(getNotificationIdBasedOnEnv(), UpdateQualityNotificationStatusRequest.ACCEPTED);
        waiting1Min();
    }

    @When("I decline quality investigation")
    public void iDeclineQualityInvestigation() {
        restProvider.updateInvestigation(getNotificationIdBasedOnEnv(), UpdateQualityNotificationStatusRequest.DECLINED);
        waiting1Min();
    }

    public void waiting1Min() {
        try {
            System.out.println("Waiting for 1 Minute for transfer to complete");
            Thread.sleep(50_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);

        }
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

    private Map<String, String> normalize(Map<String, String> input) {
        return input.entrySet().stream().map(entry -> Map.entry(normalizeString(entry.getKey()), normalizeString(entry.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private String normalizeString(String input) {
        Pattern r = Pattern.compile("\"(.+)\"");

        Matcher m = r.matcher(input);

        return m.group(1);
    }
}
