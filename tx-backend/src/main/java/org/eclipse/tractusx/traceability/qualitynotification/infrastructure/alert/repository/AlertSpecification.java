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

package org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.repository;

import jakarta.persistence.criteria.*;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaFilter;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaOperator;
import org.eclipse.tractusx.traceability.common.model.SearchStrategy;
import org.eclipse.tractusx.traceability.common.repository.BaseSpecification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.QualityNotificationSpecificationUtil;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.model.AlertEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.model.AlertNotificationEntity;
import org.glassfish.jersey.internal.guava.Lists;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


public class AlertSpecification extends BaseSpecification<AlertEntity> implements Specification<AlertEntity> {

    private static final List<String> ATTRIBUTES_IN_ALERT_ENTITY = List.of("description", "status", "createdDate");
    public AlertSpecification(SearchCriteriaFilter criteria) {
        super(criteria);
    }

    @Override
    public Predicate toPredicate(@NotNull Root<AlertEntity> root, @NotNull CriteriaQuery<?> query, @NotNull CriteriaBuilder builder) {
        return createPredicateBasedOnJoin(getSearchCriteriaFilter(), root, builder);
    }

    private Predicate createPredicateBasedOnJoin(SearchCriteriaFilter criteria, Root<?> root, CriteriaBuilder builder) {
        Join<AlertEntity, AlertNotificationEntity> alertJoin = root.join("notifications");
        Path predicatePath = ATTRIBUTES_IN_ALERT_ENTITY.contains(criteria.getKey()) ?
                            root.get(criteria.getKey()):alertJoin.get(criteria.getKey());
        if (criteria.getStrategy().equals(SearchStrategy.EQUAL)) {
            return builder.equal(
                    predicatePath.as(String.class),
                    criteria.getValue());
        }
        if (criteria.getStrategy().equals(SearchStrategy.STARTS_WITH)) {
            return builder.like(
                    predicatePath,
                    criteria.getValue() + "%");
        }
        if (criteria.getStrategy().equals(SearchStrategy.AT_LOCAL_DATE)) {
            final LocalDate localDate = LocalDate.parse(criteria.getValue());
            Predicate startingFrom = builder.greaterThanOrEqualTo(predicatePath,
                    LocalDateTime.of(localDate, LocalTime.MIN));
            Predicate endingAt = builder.lessThanOrEqualTo(predicatePath,
                    LocalDateTime.of(localDate, LocalTime.MAX));
            return builder.and(startingFrom, endingAt);
        }
        return null;
    }

    public static Specification<AlertEntity> toSpecification(final List<AlertSpecification> allSpecifications, SearchCriteriaOperator searchCriteriaOperator) {
        var specifications = Lists.newArrayList(allSpecifications);
        if (specifications.isEmpty()) {
            return Specification.allOf();
        }
        return QualityNotificationSpecificationUtil.combineSpecifications(specifications, searchCriteriaOperator);
    }
}
