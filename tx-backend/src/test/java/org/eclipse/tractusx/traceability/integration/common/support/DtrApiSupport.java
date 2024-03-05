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

//https://registry.net/semantics/registry/api/v3.0
@Component
public class DtrApiSupport {

    @Autowired
    RestitoProvider restitoProvider;

    public void dtrWillCreateShell() {
        whenHttp(restitoProvider.stubServer()).match(
                post("/semantics/registry/api/v3.0")
        ).then(
                status(HttpStatus.CREATED_201)
        );
    }

    public void verityDtrCreateShellCalledTimes(int times) {
        verifyHttp(restitoProvider.stubServer()).times(
                times,
                post("/semantics/registry/api/v3.0")
        );
    }
}
