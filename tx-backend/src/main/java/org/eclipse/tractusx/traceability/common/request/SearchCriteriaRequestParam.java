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

package org.eclipse.tractusx.traceability.common.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.eclipse.tractusx.traceability.common.model.SearchOperation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Data
@AllArgsConstructor
public class SearchCriteriaRequestParam {
    @ArraySchema(arraySchema = @Schema(description = "Filter Criteria", additionalProperties = Schema.AdditionalPropertiesValue.FALSE, example = "owner,EQUAL,OWN"), maxItems = Integer.MAX_VALUE)
    private List<String> filter;

    public List<SearchCriteria> toSearchCriteria() {
        if (isNull(this.filter)) {
            return Collections.emptyList();
        }
        ArrayList<SearchCriteria> filters = new ArrayList<>();
        for (String filter : this.filter) {
            try {
                String[] filterParams = filter.split(",");
                filters.add(
                        SearchCriteria.builder()
                                .key(filterParams[0])
                                .operation(SearchOperation.valueOf(filterParams[1]))
                                .value(filterParams[2])
                                .build());
            } catch (Exception exception) {
                throw new InvalidFilterException(
                        "Invalid filter param provided filter={provided} expected format is following sort=parameter,operation,value"
                                .replace("{provided}", filter)
                );
            }
        }
        return filters;
    }

    public void addOwnerCriteria(Owner owner) {
        if (nonNull(owner) && isNull(filter)) {
            filter = new ArrayList<>();
            filter.add("owner,EQUAL," + owner.name());
        } else if (nonNull(owner) && nonNull(filter)) {
            filter.add("owner,EQUAL," + owner.name());
        }
    }
}
