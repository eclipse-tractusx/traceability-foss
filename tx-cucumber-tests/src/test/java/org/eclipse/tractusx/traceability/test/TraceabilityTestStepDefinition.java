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
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.test.tooling.TraceXEnvironmentEnum;
import org.eclipse.tractusx.traceability.test.tooling.rest.RestProvider;
import org.eclipse.tractusx.traceability.test.tooling.rest.response.QualityNotificationIdResponse;
import org.eclipse.tractusx.traceability.test.tooling.rest.response.QualityNotificationResponse;
import org.eclipse.tractusx.traceability.test.validator.InvestigationValidator;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    @And("I create investigation")
    public void iCreateInvestigation() {
        final String assetId = "urn:uuid:7eeeac86-7b69-444d-81e6-655d0f1513bd";
        notificationDescription = "E2E cucumber test at " + Instant.now();
        final Instant targetDate = Instant.now().plus(1, ChronoUnit.DAYS);
        final String severity = "LIFE-THREATENING";

        final QualityNotificationIdResponse idResponse = restProvider.createInvestigation(
                List.of(assetId),
                notificationDescription,
                targetDate,
                severity
        );
        notificationID_TXA = idResponse.id();

        assertThat(idResponse.id()).isNotNull();
    }

    @Given("I create quality investigation")
    public void iCreateQualityInvestigation(DataTable dataTable) {
        final Map<String, String> input = dataTable.asMap();
        final String assetId = "urn:uuid:7eeeac86-7b69-444d-81e6-655d0f1513bd";
        notificationDescription = wrapStringWithTimestamp(input.get("description"));
        final Instant targetDate = Instant.parse(input.get("targetDate"));
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
        InvestigationValidator.validateInvestigation(result, dataTable.asMap());
    }

    @When("I approve quality investigation")
    public void iApproveQualityInvestigation() {
        restProvider.approveInvestigation(getNotificationIdBasedOnEnv());
        waiting1Min();
    }

    @When("I check, if quality investigation has been received")
    public void iCanSeeNotificationWasReceived() {
        final List<QualityNotificationResponse> result = restProvider.getReceivedNotifications();
        final QualityNotificationResponse notification = result.stream().filter(qn -> Objects.equals(qn.getDescription(), notificationDescription)).findFirst().get();
        notificationID_TXB = notification.getId();

        assertThat(notification).isNotNull();
    }

    @When("I acknowledge quality investigation")
    public void iAcknowledgeQualityInvestigation() {
        restProvider.acknowledgeInvestigation(getNotificationIdBasedOnEnv());
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
}
