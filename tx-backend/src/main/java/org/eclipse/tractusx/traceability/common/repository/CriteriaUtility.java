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

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections.CollectionUtils;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationSide;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

@UtilityClass
public class CriteriaUtility {

    public List<String> getDistinctAssetFieldValues(
            String fieldName,
            String startWith,
            Integer resultLimit,
            Owner owner,
            List<String> inAssetIds,
            Class<?> assetEntityClass,
            EntityManager entityManager) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<String> cq = builder.createQuery(String.class);
        Root<?> root = cq.from(assetEntityClass);

        Path<String> fieldPath = root.get(fieldName);
        // Cast to string
        Expression<String> stringFieldPath = builder.concat(fieldPath, "");

        cq.select(stringFieldPath)
                .distinct(true)
                .orderBy(List.of(builder.asc(stringFieldPath)));

        List<Predicate> predicates = new ArrayList<>();
        if (nonNull(startWith)) {
            predicates.add(
                    builder.like(
                            builder.lower(fieldPath),
                            startWith.toLowerCase() + "%")
            );
        }

        if (nonNull(owner)) {
            predicates.add(builder.equal(root.get("owner").as(String.class), owner.name()));
        }

        if (!CollectionUtils.isEmpty(inAssetIds)) {
            predicates.add(root.get("id").as(String.class).in(inAssetIds));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(cq)
                .setMaxResults(resultLimit)
                .getResultList();
    }

    public List<String> getDistinctNotificationFieldValues(
            String fieldName,
            String startWith,
            Integer resultLimit,
            NotificationSide side,
            Class<?> notificationEntityClass,
            EntityManager entityManager) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<String> cq = builder.createQuery(String.class);
        Root<?> root = cq.from(notificationEntityClass);

        Path<String> fieldPath1 = getFieldPath(root, fieldName);
        // Cast to string
        Expression<String> stringFieldPath1 = builder.concat(fieldPath1, "");

        cq.select(stringFieldPath1)
                .distinct(true)
                .orderBy(List.of(builder.asc(stringFieldPath1)));

        List<Predicate> predicates = new ArrayList<>();
        if (nonNull(startWith)) {
            predicates.add(
                    builder.like(
                            builder.lower(stringFieldPath1),
                            startWith.toLowerCase() + "%")
            );
        }

        if (nonNull(side)) {
            predicates.add(builder.equal(root.get("side").as(String.class), side.name()));
        }
        cq.where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(cq)
                .setMaxResults(resultLimit)
                .getResultList();
    }

    private Path<String> getFieldPath(Root<?> root, String fieldName) {
        if (isJoinQueryFieldName(fieldName)) {
            Join<?, ?> join = root.join(getJoinTableName(fieldName), JoinType.LEFT);
            return join.get(getJoinTableFieldName(fieldName));
        }
        return root.get(fieldName);
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
}
