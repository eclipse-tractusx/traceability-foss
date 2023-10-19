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

package org.eclipse.tractusx.traceability.common.repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Getter;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaFilter;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaOperator;
import org.eclipse.tractusx.traceability.common.model.SearchStrategy;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Getter
public abstract class BaseSpecification<T> implements Specification<T> {

    private static final String OWNER_FIELD_NAME = "owner";

    private final SearchCriteriaFilter searchCriteriaFilter;

    protected BaseSpecification(SearchCriteriaFilter searchCriteriaFilter) {
        this.searchCriteriaFilter = searchCriteriaFilter;
    }

    protected Predicate createPredicate(SearchCriteriaFilter criteria, Root<?> root, CriteriaBuilder builder) {
        if (criteria.getStrategy().equals(SearchStrategy.EQUAL)) {
            return builder.equal(
                    root.<String>get(criteria.getKey()).as(String.class),
                    criteria.getValue());
        }
        if (criteria.getStrategy().equals(SearchStrategy.STARTS_WITH)) {
            return builder.like(
                    root.get(criteria.getKey()),
                    criteria.getValue() + "%");
        }
        if (criteria.getStrategy().equals(SearchStrategy.AT_LOCAL_DATE)) {
            final LocalDate localDate = LocalDate.parse(criteria.getValue());
            Predicate startingFrom = builder.greaterThanOrEqualTo(root.get(criteria.getKey()),
                    LocalDateTime.of(localDate, LocalTime.MIN));
            Predicate endingAt = builder.lessThanOrEqualTo(root.get(criteria.getKey()),
                    LocalDateTime.of(localDate, LocalTime.MAX));
            return builder.and(startingFrom, endingAt);
        }
        return null;
    }

    public static <T> Specification<T> toSpecification(List<? extends BaseSpecification<T>> specifications, SearchCriteriaOperator searchCriteriaOperator) {
        if (specifications.isEmpty()) {
            return null;
        }

        Map<String, List<BaseSpecification<T>>> groupedSpecifications = specifications.stream()
                .collect(groupingBy(spec -> spec.getSearchCriteriaFilter().getKey()));

        Map<String, Specification<T>> fieldSpecsByFieldName = groupedSpecifications.values().stream()
                .map(BaseSpecification::combineFieldSpecifications)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return combineSpecifications(fieldSpecsByFieldName, searchCriteriaOperator);

    }

    // Combines all fields into one specification
    private static <T> Specification<T> combineSpecifications(Map<String, Specification<T>> fieldSpecsByFieldName, SearchCriteriaOperator searchCriteriaOperator) {
        Specification<T> result;

        // global filtering specific logic
        if (fieldSpecsByFieldName.containsKey(OWNER_FIELD_NAME) && SearchCriteriaOperator.OR.equals(searchCriteriaOperator)) {
            result = fieldSpecsByFieldName.get(OWNER_FIELD_NAME);
            List<Specification<T>> otherFieldsSpecifications = fieldSpecsByFieldName.entrySet().stream()
                    .filter(entry -> !OWNER_FIELD_NAME.equals(entry.getKey()))
                    .map(Map.Entry::getValue).toList();

            if (otherFieldsSpecifications.isEmpty()) {
                return result;
            }
            return Specification.where(result).and(combineWithSpecificationsWith(otherFieldsSpecifications, SearchCriteriaOperator.OR));
        } else {

            List<Specification<T>> fieldSpecList = fieldSpecsByFieldName.values().stream().toList();

            result = combineWithSpecificationsWith(fieldSpecList, searchCriteriaOperator);
        }
        return result;
    }

    // Combines specific field specifications
    private static <T> Map.Entry<String, Specification<T>> combineFieldSpecifications(List<BaseSpecification<T>> specifications) {
        // TODO: Add here date range handling if list has BEFORE_LOCAL_DATE and AFTER_LOCAL_DATE then combine those with AND
        String fieldName = specifications.get(0).searchCriteriaFilter.getKey();
        Specification<T> result = combineWithSpecificationsWith(
                specifications.stream().map(baseSpec -> (Specification<T>) baseSpec).toList(),
                SearchCriteriaOperator.OR);

        return Map.entry(fieldName, result);
    }

    private static <T> Specification<T> combineWithSpecificationsWith(List<Specification<T>> specifications, SearchCriteriaOperator searchCriteriaOperator) {
        Specification<T> result = specifications.get(0);
        for (int i = 1; i < specifications.size(); i++) {
            if (SearchCriteriaOperator.OR.equals(searchCriteriaOperator)) {
                result = Specification.where(result).or(specifications.get(i));
            } else {
                result = Specification.where(result).and(specifications.get(i));
            }
        }
        return result;
    }
}
