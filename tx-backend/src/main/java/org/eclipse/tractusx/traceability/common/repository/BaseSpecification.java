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
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Getter;
import org.eclipse.tractusx.traceability.common.domain.ParseLocalDateException;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaFilter;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaOperator;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaStrategy;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.eclipse.tractusx.traceability.common.model.SearchCriteriaStrategy.BEFORE_LOCAL_DATE;
import static org.eclipse.tractusx.traceability.common.model.SearchCriteriaStrategy.GLOBAL;
import static org.eclipse.tractusx.traceability.common.model.SearchCriteriaStrategy.NOTIFICATION_COUNT_EQUAL;

@Getter
public abstract class BaseSpecification<T> implements Specification<T> {

    private final SearchCriteriaFilter searchCriteriaFilter;

    protected BaseSpecification(SearchCriteriaFilter searchCriteriaFilter) {
        this.searchCriteriaFilter = searchCriteriaFilter;
    }

    protected Predicate createPredicate(SearchCriteriaFilter criteria, Root<?> root, CriteriaBuilder builder) {
        String expectedFieldValue = criteria.getValue();
        String fieldName = getJoinTableFieldName(criteria.getKey());
        Path<Object> fieldPath = getFieldPath(root, criteria);

        if (SearchCriteriaStrategy.EXCLUDE.equals(criteria.getStrategy())) {
            return builder.notEqual(
                    fieldPath.as(String.class),
                    expectedFieldValue);
        }
        if (SearchCriteriaStrategy.IS_NOT_NULL.equals(criteria.getStrategy())) {
            return builder.isNotNull(
                    fieldPath.as(String.class));
        }

        if (SearchCriteriaStrategy.EQUAL.equals(criteria.getStrategy())
                || NOTIFICATION_COUNT_EQUAL.equals(criteria.getStrategy())
                || GLOBAL.equals(criteria.getStrategy())) {
            return builder.equal(
                    fieldPath,
                    builder.literal(expectedFieldValue));
        }
        if (SearchCriteriaStrategy.STARTS_WITH.equals(criteria.getStrategy())) {
            return builder.like(
                    fieldPath.as(String.class),
                    expectedFieldValue + "%");
        }
        if (SearchCriteriaStrategy.AT_LOCAL_DATE.equals(criteria.getStrategy())) {
            final LocalDate localDate = getParseLocalDate(expectedFieldValue, fieldName);
            Predicate startingFrom = builder.greaterThanOrEqualTo(fieldPath.as(LocalDateTime.class),
                    LocalDateTime.of(localDate, LocalTime.MIN));
            Predicate endingAt = builder.lessThanOrEqualTo(fieldPath.as(LocalDateTime.class),
                    LocalDateTime.of(localDate, LocalTime.MAX));

            return builder.and(startingFrom, endingAt);
        }
        if (BEFORE_LOCAL_DATE.equals(criteria.getStrategy())) {
            final LocalDate localDate = getParseLocalDate(expectedFieldValue, fieldName);

            return builder.lessThanOrEqualTo(fieldPath.as(LocalDateTime.class),
                    LocalDateTime.of(localDate, LocalTime.MAX));
        }
        if (SearchCriteriaStrategy.AFTER_LOCAL_DATE.equals(criteria.getStrategy())) {
            final LocalDate localDate = getParseLocalDate(expectedFieldValue, fieldName);

            return builder.greaterThanOrEqualTo(fieldPath.as(LocalDateTime.class),
                    LocalDateTime.of(localDate, LocalTime.MIN));
        }

        return null;
    }

    private Path<Object> getFieldPath(Root<?> root, SearchCriteriaFilter criteria) {
        if (isJoinQueryFieldName(criteria.getKey())) {
            Join<?, ?> join = root.join(getJoinTableName(criteria.getKey()));
            return join.get(getJoinTableFieldName(criteria.getKey()));
        }
        return root.get(criteria.getKey());
    }

    private static LocalDate getParseLocalDate(String fieldValue, String fieldName) {
        try {
            return LocalDate.parse(fieldValue);
        } catch (Exception exception) {
            throw new ParseLocalDateException(fieldValue, fieldName, exception);
        }
    }

    private boolean isJoinQueryFieldName(String fieldName) {
        return fieldName.contains("_");
    }

    private String getJoinTableName(String joinQueryFieldName) {
        String[] split = joinQueryFieldName.split("_");
        return split[0];
    }

    private String getJoinTableFieldName(String joinQueryFieldName) {
        String[] split = joinQueryFieldName.split("_");
        return split.length == 2 ? split[1] : split[0];
    }

    public static <T> Specification<T> toSpecification(List<? extends BaseSpecification<T>> specifications) {
        if (specifications.isEmpty()) {
            return null;
        }

        List<BaseSpecification<T>> concreteSpecifications = new ArrayList<>(specifications);

        List<BaseSpecification<T>> globalSpecifications = concreteSpecifications.stream()
                .filter(spec -> SearchCriteriaStrategy.GLOBAL.equals(spec.getSearchCriteriaFilter().getStrategy()))
                .toList();

        List<BaseSpecification<T>> otherSpecifications = concreteSpecifications.stream()
                .filter(spec -> !SearchCriteriaStrategy.GLOBAL.equals(spec.getSearchCriteriaFilter().getStrategy()))
                .toList();

        Specification<T> globalSpec = combineSpecificationsWith(
                globalSpecifications.stream().map(spec -> (Specification<T>) spec).toList(), SearchCriteriaOperator.OR);


        Map<String, List<BaseSpecification<T>>> groupedSpecs = otherSpecifications.stream()
                .collect(Collectors.groupingBy(spec -> spec.getSearchCriteriaFilter().getKey()));

        List<Specification<T>> combinedGroupSpecifications = new ArrayList<>();

        for (Map.Entry<String, List<BaseSpecification<T>>> entry : groupedSpecs.entrySet()) {
            List<BaseSpecification<T>> specs = entry.getValue();
            SearchCriteriaOperator operator = specs.get(0).getSearchCriteriaFilter().getOperator();

            Specification<T> groupedSpec = combineSpecificationsWith(
                    specs.stream().map(spec -> (Specification<T>) spec).toList(), operator);

            combinedGroupSpecifications.add(groupedSpec);
        }

        Specification<T> otherSpec = combineSpecificationsWith(combinedGroupSpecifications, SearchCriteriaOperator.AND);

        return (globalSpec != null) ? Specification.where(globalSpec).and(otherSpec) : otherSpec;
    }

    // Combines specific field specifications based on the operator
    private static <T> Specification<T> combineSpecificationsWith(List<Specification<T>> specifications, SearchCriteriaOperator operator) {
        if (specifications.isEmpty()) {
            return null;
        }

        Specification<T> combinedSpec = specifications.get(0);
        for (int i = 1; i < specifications.size(); i++) {
            if (SearchCriteriaOperator.AND.equals(operator)) {
                combinedSpec = combinedSpec.and(specifications.get(i));
            } else {
                combinedSpec = combinedSpec.or(specifications.get(i));
            }
        }
        return combinedSpec;
    }
}
