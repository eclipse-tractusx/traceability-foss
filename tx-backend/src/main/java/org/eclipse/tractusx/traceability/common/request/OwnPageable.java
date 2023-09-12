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
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.eclipse.tractusx.traceability.common.model.SearchOperation;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@AllArgsConstructor
public class OwnPageable {
    private Integer page;
    private Integer size;
    @ArraySchema(arraySchema = @Schema(description = "Content of Assets PageResults", additionalProperties = Schema.AdditionalPropertiesValue.FALSE, example = "manufacturerPartId,desc"), maxItems = Integer.MAX_VALUE)
    private List<String> sort;
    private List<String> filter;

    public static Pageable toPageable(OwnPageable ownPageable) {
        int usedPage = 0;
        int usedPageSize = 50;

        if (ownPageable.page != null) {
            usedPage = ownPageable.getPage();
        }

        if (ownPageable.size != null) {
            usedPageSize = ownPageable.getSize();
        }

        Sort usedSort = Sort.unsorted();

        if (!CollectionUtils.isEmpty(ownPageable.getSort())) {
            usedSort = toDomainSort(ownPageable.getSort());
        }

        return PageRequest.of(usedPage, usedPageSize, usedSort);
    }

    private static Sort toDomainSort(final List<String> sorts) {
        ArrayList<Sort.Order> orders = new ArrayList<>();
        for (String sort : sorts) {

            try {
                String[] sortParams = sort.split(",");
                orders.add(new Sort.Order(Sort.Direction.valueOf(sortParams[1].toUpperCase()),
                        sortParams[0]));
            } catch (Exception exception) {
                throw new InvalidSortException(
                        "Invalid sort param provided sort={provided} expected format is following sort=parameter,order"
                                .replace("{provided}", sort)
                );
            }
        }
        return Sort.by(orders);
    }

    public List<SearchCriteria> toSearchCriteria() {
        if(isNull(this.filter)) {
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
}
