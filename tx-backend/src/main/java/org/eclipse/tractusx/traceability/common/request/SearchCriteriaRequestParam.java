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
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.tractusx.traceability.common.model.BaseRequestFieldMapper;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaFilter;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaOperator;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaStrategy;
import org.eclipse.tractusx.traceability.common.model.UnsupportedSearchCriteriaFieldException;
import org.eclipse.tractusx.traceability.common.request.exception.InvalidFilterException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteriaRequestParam {
    @ArraySchema(arraySchema = @Schema(description = "Filter Criteria", additionalProperties = Schema.AdditionalPropertiesValue.FALSE, example = "owner,EQUAL,OWN"), maxItems = Integer.MAX_VALUE)
    List<String> filter;

    public List<@Size(max = 1100, message = "Filter string should not be longer than 1100 characters.") String> getFilter() {
        return filter;
    }

    public SearchCriteria toSearchCriteria(BaseRequestFieldMapper fieldMapper) {
        ArrayList<SearchCriteriaFilter> filters = new ArrayList<>();
        List<String> inputFilters = filter;
        if (isNull(this.filter)) {
            inputFilters = Collections.emptyList();
        }

        for (String inputFilter : inputFilters) {
            try {
                String[] filterParams = inputFilter.split(",");
                filters.add(
                        SearchCriteriaFilter.builder()
                                .key(fieldMapper.mapRequestFieldName(filterParams[0]))
                                .strategy(SearchCriteriaStrategy.valueOf(filterParams[1]))
                                .value(filterParams[2])
                                .operator(SearchCriteriaOperator.valueOf(filterParams[3]))
                                .build());
            } catch (UnsupportedSearchCriteriaFieldException exception) {
                throw exception;
            } catch (Exception exception) {
                throw new InvalidFilterException(
                        "Invalid filter param provided filter={provided} expected format is following sort=parameter,strategy,value,operator"
                                .replace("{provided}", inputFilter)
                );
            }

        }
        return SearchCriteria.builder().searchCriteriaFilterList(filters).build();
    }
}
