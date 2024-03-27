/*********************************************************************************
 * Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
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

package org.eclipse.tractusx.traceability.ess.domain;

public record LogisticAddress(
    String id,
    String uuid,
    String created_at,
    String updated_at,
    String bpn,
    String phy_country,
    String phy_city,
    String phy_street_name,
    String phy_street_number,
    String phy_postcode
) {
    public static LogisticAddress buildAddress(String id, String uuid, String created_at, String updated_at, String bpn, String phy_country, String phy_city, String phy_street_name, String phy_street_number, String phy_postcode) {
        return new LogisticAddress(id, uuid, created_at, updated_at, bpn, phy_country, phy_city, phy_street_name, phy_street_number, phy_postcode);
    }
}
