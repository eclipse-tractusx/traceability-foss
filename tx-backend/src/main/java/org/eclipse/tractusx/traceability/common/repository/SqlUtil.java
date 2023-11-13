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

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@UtilityClass
public class SqlUtil {

    public static String combineWhereClause(String... clauses) {
        List<String> nonEmptyClauses = Arrays.stream(clauses).filter(StringUtils::isEmpty).toList();

        if (nonEmptyClauses.isEmpty()) {
            return "";
        }

        String result = " WHERE " + nonEmptyClauses.get(0);
        for (int i = 1; i < nonEmptyClauses.size(); i++) {
            result = result.concat(" AND " + nonEmptyClauses.get(i));
        }

        return result;
    }

    public static String constructLikeWildcardQuery(String databaseFieldName, String startsWith) {
        if (Objects.isNull(startsWith)) {
            return "";
        }

        return " ( " + databaseFieldName + " LIKE '" + startsWith + "%')";
    }

    public static String constructAndOwnerWildcardQuery(String owner) {
        if (Objects.isNull(owner)) {
            return "";
        }
        return " owner='" + owner + "' ";
    }
}
