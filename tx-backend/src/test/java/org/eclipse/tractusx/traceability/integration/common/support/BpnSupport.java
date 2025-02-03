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
package org.eclipse.tractusx.traceability.integration.common.support;

import org.glassfish.grizzly.http.util.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.semantics.Action.status;
import static com.xebialabs.restito.semantics.Condition.matchesUri;

@Component
public class BpnSupport {

    @Autowired
    RestitoProvider restitoProvider;

    @Value("${traceability.bpn}")
    String bpn = null;

    public String testBpn() {
        return bpn;
    }

    public void providesBpdmLookup() {
        whenHttp(restitoProvider.stubServer()).match(
                matchesUri(Pattern.compile("/endpointdatareference/members/legal-entities/search"))
        ).then(
                status(HttpStatus.OK_200),
                restitoProvider.jsonResponseFromFile("stubs/bpdm/response_200.json")
        );
    }

}
