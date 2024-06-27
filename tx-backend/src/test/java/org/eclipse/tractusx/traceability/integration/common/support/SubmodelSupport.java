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

package org.eclipse.tractusx.traceability.integration.common.support;

import org.glassfish.grizzly.http.util.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.builder.verify.VerifyHttp.verifyHttp;
import static com.xebialabs.restito.semantics.Action.status;
import static com.xebialabs.restito.semantics.Condition.post;

@Component
public class SubmodelSupport {

    @Autowired
    RestitoProvider restitoProvider;
    private static final String uuidRegex = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";
    public void willCreateSubmodel(String submodelId) {
        whenHttp(restitoProvider.stubServer()).match(
                post("/api/submodel/data/" + uuidRegex)
        ).then(
                status(HttpStatus.CREATED_201)
        );
    }

    public void dtrWillFailToCreateShell() {
        whenHttp(restitoProvider.stubServer()).match(
                post("/semantics/registry/api/v3.0/shell-descriptors")
        ).then(
                status(HttpStatus.SERVICE_UNAVAILABLE_503)
        );
    }

    public void verifyDtrCreateShellCalledTimes(int times) {
        verifyHttp(restitoProvider.stubServer()).times(
                times,
                post("/semantics/registry/api/v3.0/shell-descriptors")
        );
    }
}
