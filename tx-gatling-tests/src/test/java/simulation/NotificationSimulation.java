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
package simulation;

import io.gatling.javaapi.core.CoreDsl;
import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class NotificationSimulation extends Simulation {
    private static final String BASE_URL = System.getenv("BASE_URL");
    private static final String X_API_KEY = System.getenv("TECHNICAL_API_KEY");
    FeederBuilder.FileBased<Object> feeder = CoreDsl.jsonFile("requests/post_notifications.json").random();

    HttpProtocolBuilder httpProtocol = http
            .baseUrl(BASE_URL)
            .acceptHeader("application/json");

    ScenarioBuilder scenario = scenario("Notification sending")
            .feed(feeder)
            .exec(
                    http("POST /notifications")
                            .post("/notifications")
                            .header("X-API-KEY", X_API_KEY)
                            .body(StringBody("#{request.jsonStringify()}")).asJson()
                            .check(status().is(200)) // Check that the response status is 200 OK
            );

    {
        setUp(
                scenario.injectOpen(constantUsersPerSec(2).during(2).randomized())
        ).protocols(httpProtocol);
    }

}
