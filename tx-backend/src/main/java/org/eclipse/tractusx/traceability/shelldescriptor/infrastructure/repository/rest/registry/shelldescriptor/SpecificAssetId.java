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

package org.eclipse.tractusx.traceability.shelldescriptor.infrastructure.repository.rest.registry.shelldescriptor;

import org.eclipse.tractusx.irs.component.assetadministrationshell.IdentifierKeyValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SpecificAssetId {
    private final String key;
    private final String value;

    public SpecificAssetId(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static List<SpecificAssetId> fromList(List<IdentifierKeyValuePair> keyValuePair) {
        List<SpecificAssetId> specificAssetIds = new ArrayList<>();
        keyValuePair.forEach(keyValuePair1 -> specificAssetIds.add(new SpecificAssetId(keyValuePair1.getName(), keyValuePair1.getValue())));
        return specificAssetIds;
    }

    public String key() {
        return key;
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SpecificAssetId) obj;
        return Objects.equals(this.key, that.key) &&
                Objects.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public String toString() {
        return "SpecificAssetId[" +
                "key=" + key + ", " +
                "value=" + value + ']';
    }

}
