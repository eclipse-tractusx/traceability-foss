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
package org.eclipse.tractusx.traceability.testdata;

import org.eclipse.edc.catalog.spi.Catalog;
import org.eclipse.edc.catalog.spi.DataService;
import org.eclipse.edc.catalog.spi.Dataset;
import org.eclipse.edc.catalog.spi.Distribution;
import org.eclipse.edc.policy.model.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CatalogTestDataFactory {

    public static Catalog createCatalogTestData() {
        return createCatalogTestData(Collections.emptyMap());
    }

    public static Catalog createCatalogTestData(Map<String, Object> properties) {

        OrConstraint orConstraint = OrConstraint.Builder.newInstance().constraint(AtomicConstraint.Builder.newInstance()
                .operator(Operator.EQ)
                .leftExpression(new LiteralExpression("PURPOSE"))
                .rightExpression(new LiteralExpression("ID 3.0 Trace"))
                .build()).build();

        Permission permission = Permission.Builder.newInstance()
                .action(Action.Builder.newInstance()
                        .type("USE").build())
                .constraints(List.of(orConstraint)).build();
        Policy policy = Policy.Builder.newInstance().permission(permission).build();

        DataService dataService = DataService.Builder.newInstance()
                .build();
        Distribution distribution = Distribution.Builder.newInstance().format("format")
                .dataService(dataService).build();

        Dataset dataset = Dataset.Builder.newInstance()
                .offer("123", policy)
                .distribution(distribution)
                .property("https://w3id.org/edc/v0.0.1/ns/notificationtype", "qualityinvestigation")
                .property("https://w3id.org/edc/v0.0.1/ns/notificationmethod", "receive")
                .property("https://w3id.org/edc/v0.0.1/ns/id", "id")
                .build();

        return Catalog.Builder.newInstance().dataset(dataset).properties(properties).build();
    }

    public static Catalog createCatalogTestData(Dataset.Builder datasetBuilder) {
        Permission permission = Permission.Builder.newInstance()
                .action(Action.Builder.newInstance()
                        .type("USE").build())
                .constraints(List.of(AtomicConstraint.Builder.newInstance()
                        .operator(Operator.EQ)
                        .leftExpression(new LiteralExpression("PURPOSE"))
                        .rightExpression(new LiteralExpression("ID 3.0 Trace"))
                        .build())).build();
        Policy policy = Policy.Builder.newInstance().permission(permission).build();

        DataService dataService = DataService.Builder.newInstance()
                .build();
        Distribution distribution = Distribution.Builder.newInstance().format("format")
                .dataService(dataService).build();
        Dataset dataset = datasetBuilder
                .offer("123", policy)
                .distribution(distribution)
                .build();

        return Catalog.Builder.newInstance().dataset(dataset).properties(Collections.emptyMap()).build();
    }

}
