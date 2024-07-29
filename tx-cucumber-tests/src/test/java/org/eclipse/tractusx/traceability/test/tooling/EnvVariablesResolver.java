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

package org.eclipse.tractusx.traceability.test.tooling;

public class EnvVariablesResolver {

    public static final String ASSOCIATION_SUPERVISOR_TX_A_CLIENT_ID = "ASSOCIATION_SUPERVISOR_TX_A_CLIENT_ID";
    public static final String ASSOCIATION_SUPERVISOR_TX_A_PASSWORD = "ASSOCIATION_SUPERVISOR_TX_A_PASSWORD";
    public static final String ASSOCIATION_SUPERVISOR_TX_B_CLIENT_ID = "ASSOCIATION_SUPERVISOR_TX_B_CLIENT_ID";
    public static final String ASSOCIATION_SUPERVISOR_TX_B_PASSWORD = "ASSOCIATION_SUPERVISOR_TX_B_PASSWORD";

    public static String getSupervisorClientIdTracexA() {
        return System.getenv(ASSOCIATION_SUPERVISOR_TX_A_CLIENT_ID);
    }

    public static String getSupervisorPasswordTracexA() {
        return System.getenv(ASSOCIATION_SUPERVISOR_TX_A_PASSWORD);
    }

    public static String getSupervisorClientIdTracexB() {
        return System.getenv(ASSOCIATION_SUPERVISOR_TX_B_CLIENT_ID) == null
                ? System.getenv(ASSOCIATION_SUPERVISOR_TX_A_CLIENT_ID) : System.getenv(ASSOCIATION_SUPERVISOR_TX_B_CLIENT_ID);
    }

    public static String getAssociationSupervisorTxBPassword() {
        return System.getenv(ASSOCIATION_SUPERVISOR_TX_B_PASSWORD) == null
                ? System.getenv(ASSOCIATION_SUPERVISOR_TX_A_PASSWORD) : System.getenv(ASSOCIATION_SUPERVISOR_TX_B_PASSWORD);
    }

    public static String getKeycloakHost() {
        return System.getenv("ASSOCIATION_KEYCLOAK_HOST");
    }

    public static String getTX_A_Host() {
        return System.getenv("ASSOCIATION_E2E_TXA_HOST");
    }

    public static String getTX_B_Host() {
        return System.getenv("ASSOCIATION_E2E_TXB_HOST");
    }
}
