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

package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.policy;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AndConstraintTest {

    @Test
    void create() {
        // given
        final AndConstraint toTest = AndConstraint.Builder.newInstance().build();
        final List<Constraint> constraints = List.of(AndConstraint.Builder.newInstance().build());

        // when
        final AndConstraint result = toTest.create(constraints);

        // then
        assertThat(result.getConstraints()).hasSize(1);
    }

    @Test
    void toStringMethod() {
        // given
        final AndConstraint toTest = AndConstraint.Builder.newInstance()
                .build();

        // when
        final String result = toTest.toString();

        // then
        assertThat(result).isEqualTo("And constraint: []");
    }

}
