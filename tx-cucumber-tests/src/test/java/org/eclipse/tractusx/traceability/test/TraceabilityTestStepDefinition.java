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

import io.cucumber.java.Before;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.eclipse.tractusx.traceability.test.tooling.rest.RestProvider;
import org.eclipse.tractusx.traceability.test.tooling.TraceXEnvironmentEnum;
import org.eclipse.tractusx.traceability.test.tooling.rest.response.QualityNotificationIdResponse;
import org.eclipse.tractusx.traceability.test.tooling.rest.response.QualityNotificationResponse;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class TraceabilityTestStepDefinition {

    private RestProvider restProvider;

    private Long notificationID = null;
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
        // provide proper asset id from predefined import test data
        final String assetId = "urn:uuid:6b2296cc-26c0-4f38-8a22-092338c36e22";
        notificationDescription = "E2E cucumber test at " + Instant.now() ;
        final Instant targetDate = Instant.now().plus(1, ChronoUnit.DAYS);
        final String severity = "LIFE-THREATENING";

        final QualityNotificationIdResponse idResponse = restProvider.createInvestigation(
                List.of(assetId),
                notificationDescription,
                targetDate,
                severity
        );
        notificationID = idResponse.id();

        assertThat(idResponse.id()).isNotNull();
    }

    @When("I send investigation")
    public void iSendInvestigation() {
        restProvider.approveInvestigation(this.notificationID);
    }

    @When("I can see notification was received")
    public void iCanSeeNotificationWasReceived() {
        final List<QualityNotificationResponse> result = restProvider.getReceivedNotifications();
        final QualityNotificationResponse notification = result.stream().filter(qn -> Objects.equals(qn.getDescription(), notificationDescription)).findFirst().get();
        assertThat(notification).isNotNull();
    }

    @When("I wait for transfer")
    public void waiting50sec() {
        try {
            System.out.println("Waiting for 50 sec..");
            Thread.sleep(50_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);

        }
    }
}
