/********************************************************************************
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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

import common.FilterAttribute;
import common.FilterValue;
import lombok.experimental.UtilityClass;
import org.eclipse.tractusx.traceability.common.model.BaseRequestFieldMapper;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaFilter;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaOperator;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaStrategy;
import org.eclipse.tractusx.traceability.common.request.exception.InvalidFilterException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@UtilityClass
public class SearchCriteriaMapper {

    public static <T> SearchCriteria toSearchCriteria(BaseRequestFieldMapper fieldMapper, List<T> filters) throws InvalidFilterException {
        List<SearchCriteriaFilter> filterList = new ArrayList<>();

        if (filters != null) {
            filters.forEach(filter -> {
                if (Objects.nonNull(filter)) {
                    extractFilters(filterList, filter, fieldMapper);
                }
            });
        }
        return SearchCriteria.builder()
                .searchCriteriaFilterList(filterList)
                .build();
    }

    private static <T> void extractFilters(List<SearchCriteriaFilter> filterList, T filter, BaseRequestFieldMapper fieldMapper) {
        for (Field field : filter.getClass().getDeclaredFields()) {
            if (field.getType().equals(FilterAttribute.class)) {
                field.setAccessible(true);
                try {
                    FilterAttribute filterAttribute = (FilterAttribute) field.get(filter);
                    if (filterAttribute != null && filterAttribute.getValue() != null && !filterAttribute.getValue().isEmpty()) {
                        addMappedFieldToFilterList(filterList, fieldMapper, field, filterAttribute);
                    }
                } catch (IllegalAccessException e) {
                    throw new InvalidFilterException(String.format("Invalid filter param provided filter={%s}", filter));
                }
            }
        }
    }

    private static void addMappedFieldToFilterList(List<SearchCriteriaFilter> filterList, BaseRequestFieldMapper fieldMapper, Field field, FilterAttribute filterAttribute) {
        String mappedFieldName = fieldMapper.mapRequestFieldName(field.getName());

        for (FilterValue filterValue : filterAttribute.getValue()) {
            if (filterValue.getValue() != null && !filterValue.getValue().isBlank()) {
                filterList.add(createFilter(mappedFieldName, filterValue, filterAttribute.getOperator()));
            }
        }
    }

    private static SearchCriteriaFilter createFilter(String key, FilterValue filterValue, String operator) {
        return SearchCriteriaFilter.builder()
                .key(key)
                .strategy(SearchCriteriaStrategy.fromValue(filterValue.getStrategy()))
                .value(filterValue.getValue())
                .operator(SearchCriteriaOperator.fromValue(operator))
                .build();
    }
}
