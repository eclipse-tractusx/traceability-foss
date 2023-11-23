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
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.experimental.UtilityClass;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;

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
            Class<?> assetEntityClass,
            EntityManager entityManager) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<String> cq = builder.createQuery(String.class);
        Root<?> root = cq.from(assetEntityClass);

        Path<String> fieldPath = root.get(fieldName);

        cq.select(fieldPath.as(String.class))
                .distinct(true)
                .orderBy(List.of(builder.asc(fieldPath.as(String.class))));

        List<Predicate> predicates = new ArrayList<>();
        if (nonNull(startWith)) {
            predicates.add(builder.like(fieldPath, startWith + "%"));
        }

        if (nonNull(owner)) {
            predicates.add(builder.equal(root.get("owner").as(String.class), owner.name()));
        }
        cq.where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(cq)
                .setMaxResults(resultLimit)
                .getResultList();
    }
}
