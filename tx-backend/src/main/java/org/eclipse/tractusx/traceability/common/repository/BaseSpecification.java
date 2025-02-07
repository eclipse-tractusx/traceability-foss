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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static org.eclipse.tractusx.traceability.common.model.SearchCriteriaStrategy.AFTER_LOCAL_DATE;
import static org.eclipse.tractusx.traceability.common.model.SearchCriteriaStrategy.BEFORE_LOCAL_DATE;
import static org.eclipse.tractusx.traceability.common.model.SearchCriteriaStrategy.EXCLUDE;
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

        if (SearchCriteriaStrategy.EQUAL.equals(criteria.getStrategy()) || NOTIFICATION_COUNT_EQUAL.equals(criteria.getStrategy())) {
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

        Map<String, List<BaseSpecification<T>>> groupedSpecifications = specifications.stream()
                .collect(groupingBy(spec -> spec.getSearchCriteriaFilter().getKey()));

        Map<FieldOperatorMap, Specification<T>> fieldSpecsByFieldName = groupedSpecifications.values().stream()
                .map(BaseSpecification::combineFieldSpecifications)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return combineSpecifications(fieldSpecsByFieldName);
    }

    // Combines all fields into one specification
    private static <T> Specification<T> combineSpecifications(Map<FieldOperatorMap, Specification<T>> fieldSpecsByFieldName) {
        List<Specification<T>> andSpecifications = extractSpecificationsWithOperator(fieldSpecsByFieldName, SearchCriteriaOperator.AND);
        List<Specification<T>> orSpecifications = extractSpecificationsWithOperator(fieldSpecsByFieldName, SearchCriteriaOperator.OR);

        return Specification.where(combineSpecificationsWith(andSpecifications, SearchCriteriaOperator.AND))
                .and(combineSpecificationsWith(orSpecifications, SearchCriteriaOperator.OR));
    }

    private static <T> List<Specification<T>> extractSpecificationsWithOperator(Map<FieldOperatorMap, Specification<T>> fieldSpecsByFieldName, SearchCriteriaOperator searchCriteriaOperator) {
        return fieldSpecsByFieldName.entrySet().stream()
                .filter(entry -> searchCriteriaOperator.equals(entry.getKey().operator))
                .map(Map.Entry::getValue)
                .toList();
    }


    // Combines specific field specifications
    private static <T> Map.Entry<FieldOperatorMap, Specification<T>> combineFieldSpecifications(List<BaseSpecification<T>> specifications) {
        FieldOperatorMap fieldOperatorMap = FieldOperatorMap.builder()
                .fieldName(specifications.get(0).searchCriteriaFilter.getKey())
                .operator(specifications.get(0).searchCriteriaFilter.getOperator())
                .build();

        Specification<T> result;
        if (hasBeforePredicate(specifications) && hasAfterPredicate(specifications) && dateRangesOverlap(specifications)) {
            result = combineSpecificationsWith(
                    specifications.stream().map(baseSpec -> (Specification<T>) baseSpec).toList(),
                    SearchCriteriaOperator.AND);
        } else if (hasExcludePredicate(specifications)) {
            result = combineSpecificationsWith(
                    specifications.stream().map(baseSpec -> (Specification<T>) baseSpec).toList(),
                    SearchCriteriaOperator.AND);
        } else {
            result = combineSpecificationsWith(
                    specifications.stream().map(baseSpec -> (Specification<T>) baseSpec).toList(),
                    SearchCriteriaOperator.OR);
        }

        return Map.entry(fieldOperatorMap, result);
    }

    private static <T> boolean dateRangesOverlap(List<BaseSpecification<T>> specifications) {
        Optional<BaseSpecification<T>> before = specifications.stream().filter(BaseSpecification::isBeforePredicate).findFirst();
        Optional<BaseSpecification<T>> after = specifications.stream().filter(BaseSpecification::isAfterPredicate).findFirst();

        if (before.isEmpty() || after.isEmpty()) {
            return false;
        }

        LocalDate beforeDate = getParseLocalDate(before.get().searchCriteriaFilter.getValue(), before.get().searchCriteriaFilter.getKey());
        LocalDate afterDate = getParseLocalDate(after.get().searchCriteriaFilter.getValue(), after.get().searchCriteriaFilter.getKey());
        return beforeDate.isAfter(afterDate);

    }

    private static <T> Specification<T> combineSpecificationsWith(List<Specification<T>> specifications, SearchCriteriaOperator searchCriteriaOperator) {
        if (specifications.isEmpty()) {
            return null;
        }
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

    private static <T> boolean hasBeforePredicate(List<BaseSpecification<T>> specifications) {
        return !specifications.stream().filter(spec -> BEFORE_LOCAL_DATE.equals(spec.getSearchCriteriaFilter().getStrategy())).toList().isEmpty();
    }

    private static <T> boolean hasExcludePredicate(List<BaseSpecification<T>> specifications) {
        return !specifications.stream().filter(spec -> EXCLUDE.equals(spec.getSearchCriteriaFilter().getStrategy())).toList().isEmpty();
    }

    private static <T> boolean isBeforePredicate(BaseSpecification<T> specification) {
        return BEFORE_LOCAL_DATE.equals(specification.getSearchCriteriaFilter().getStrategy());
    }

    private static <T> boolean hasAfterPredicate(List<BaseSpecification<T>> specifications) {
        return !specifications.stream().filter(spec -> AFTER_LOCAL_DATE.equals(spec.getSearchCriteriaFilter().getStrategy())).toList().isEmpty();
    }

    private static <T> boolean isAfterPredicate(BaseSpecification<T> specification) {
        return AFTER_LOCAL_DATE.equals(specification.getSearchCriteriaFilter().getStrategy());
    }
}
