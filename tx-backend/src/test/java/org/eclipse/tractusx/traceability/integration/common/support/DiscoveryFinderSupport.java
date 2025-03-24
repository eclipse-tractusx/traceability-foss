/********************************************************************************
 * Copyright (c) 2023, 2024 Contributors to the Eclipse Foundation
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
package org.eclipse.tractusx.traceability.integration.common.support;

import lombok.extern.slf4j.Slf4j;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.semantics.Action.status;
import static com.xebialabs.restito.semantics.Condition.post;
import static com.xebialabs.restito.semantics.Condition.withPostBodyContaining;

@Component
@Slf4j
public class DiscoveryFinderSupport {

    @Autowired
    RestitoProvider restitoProvider;

    @Autowired
    ResourceLoader resourceLoader;


    public void discoveryFinderWillReturnEndpointAddress() {
        try {
            String jsonString = resourceLoader.getResource("classpath:stubs/discovery.post.data/discovery_finder_response_200.json").getContentAsString(StandardCharsets.UTF_8);
            String discoveryFinderMock = jsonString.replace("${Placeholder}", "http://localhost:" + restitoProvider.stubServer().getPort() + "/v1.0/administration/connectors/discovery");

            whenHttp(restitoProvider.stubServer()).match(
                    post("/v1.0/administration/connectors/discovery/search")

            ).then(
                    status(HttpStatus.OK_200),
                    restitoProvider.jsonResponseFromString(discoveryFinderMock));

        } catch (IOException e) {
            log.error("Could not retrieve endpointAddress " + e);
        }
    }


    public void discoveryFinderWillReturnConnectorEndpoints() {
        try {
            String jsonString = resourceLoader.getResource("classpath:stubs/discovery.post.data/discovery_finder_connector_response_200.json").getContentAsString(StandardCharsets.UTF_8);
            String discoveryFinderMock = jsonString.replace("${Placeholder}", "http://localhost:" + restitoProvider.stubServer().getPort() + "/");

            whenHttp(restitoProvider.stubServer()).match(
                    post("/v1.0/administration/connectors/discovery")

            ).then(
                    status(HttpStatus.OK_200),
                    restitoProvider.jsonResponseFromString(discoveryFinderMock));

        } catch (IOException e) {
            log.error("Could not retrieve connector endpoints " + e);
        }
    }

    public void discoveryFinderWillReturnConnectorEndpointForAXS3() {
        try {
            String jsonString = resourceLoader.getResource("classpath:stubs/discovery.post.data/discovery_finder_connector_response_200_AXS3.json").getContentAsString(StandardCharsets.UTF_8);
            String discoveryFinderMock = jsonString.replace("${Placeholder}", "http://localhost:" + restitoProvider.stubServer().getPort() + "/");

            whenHttp(restitoProvider.stubServer()).match(
                    post("/v1.0/administration/connectors/discovery")
            ).then(
                    status(HttpStatus.OK_200),
                    restitoProvider.jsonResponseFromString(discoveryFinderMock));

        } catch (IOException e) {
            log.error("Could not retrieve connector endpoints " + e);
        }
    }
}
