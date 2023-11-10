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

package org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaFilter;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaStrategy;
import org.eclipse.tractusx.traceability.common.repository.BaseSpecification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.model.AlertEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.model.InvestigationEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;


public class AssetAsBuildSpecification extends BaseSpecification<AssetAsBuiltEntity> implements Specification<AssetAsBuiltEntity> {
    public AssetAsBuildSpecification(SearchCriteriaFilter criteria) {
        super(criteria);
    }

    @Override
    public Predicate toPredicate(@NotNull Root<AssetAsBuiltEntity> root, @NotNull CriteriaQuery<?> query, @NotNull CriteriaBuilder builder) {
        if (SearchCriteriaStrategy.NOTIFICATION_COUNT_EQUAL.equals(getSearchCriteriaFilter().getStrategy())) {
            return activeNotificationCountEqualPredicate(getSearchCriteriaFilter(), root, builder, getSearchCriteriaFilter().getValue());
        }

        return createPredicate(getSearchCriteriaFilter(), root, builder);
    }


    private static Predicate activeNotificationCountEqualPredicate(SearchCriteriaFilter criteria, Root<?> root, CriteriaBuilder builder, String expectedFieldValue) {
        String joinTableName;
        Class<?> notificationClass;
        switch (criteria.getKey()) {
            case "activeAlertsCount" -> {
                notificationClass = AlertEntity.class;
                joinTableName = "alerts";
            }
            case "activeInvestigationsCount" -> {
                notificationClass = InvestigationEntity.class;
                joinTableName = "investigations";
            }
            default -> throw new UnsupportedOperationException();
        }

        CriteriaQuery<?> cq = builder.createQuery();

        Subquery<Long> sub = cq.subquery(Long.class);
        Root<?> subRoot = sub.from(notificationClass);
        Join<?,?> assetJoin = subRoot.join("assets");
        sub.select(builder.count(subRoot.get("id")));
        Join<?, ?> join = root.join(joinTableName);
        sub.where(
                builder.and(
                        builder.or(
                                activeNotificationPredicates(builder, subRoot)
                        ),
                         builder.equal(assetJoin.get("id"), root.get("id")))

        );

        return builder.equal(sub, Integer.parseInt(expectedFieldValue));
    }

    private static Predicate[] activeNotificationPredicates(CriteriaBuilder builder, Root<?> root) {
        List<Predicate> predicates = QualityNotificationStatus.getActiveStates().stream()
                .map(state -> builder.equal(root.get("status").as(String.class), state.name()))
                .toList();

        return predicates.toArray(new Predicate[0]);
    }
}
