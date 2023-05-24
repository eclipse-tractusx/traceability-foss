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

import static org.assertj.core.api.Assertions.assertThat;

class DutyTest {

    @Test
    void givenDutyBuilder_whenBuildDuty_thenConstructProperObject() {
        // given
        final Permission permission = Permission.Builder
                .newInstance().build();
        final Duty duty = Duty.Builder.newInstance().build();

        // when
        final Duty result = Duty.Builder.newInstance()
                .parentPermission(permission)
                .consequence(duty)
                .build();

        // then
        assertThat(result.getParentPermission()).isEqualTo(permission);
        assertThat(result.getConsequence()).isEqualTo(duty);
    }

    @Test
    void setParentPermission() {
        // given
        final Permission permission = Permission.Builder
                .newInstance().build();
        final Duty duty = Duty.Builder.newInstance().build();

        final Duty result = Duty.Builder.newInstance()
                .parentPermission(permission)
                .consequence(duty)
                .build();

        // when
        result.setParentPermission(null);

        // then
        assertThat(result.getParentPermission()).isNull();
    }

    @Test
    void toStringMethod() {
        // given
        final Permission permission = Permission.Builder
                .newInstance().build();
        final Duty duty = Duty.Builder.newInstance().build();

        final Duty result = Duty.Builder.newInstance()
                .parentPermission(permission)
                .consequence(duty)
                .build();

        // when
        final String string = result.toString();

        // then
        assertThat(string).isEqualTo("Duty constraint: []");
    }

    @Test
    void withTarget() {
        // given
        final String target = "target";
        final Permission permission = Permission.Builder
                .newInstance().build();
        final Duty duty = Duty.Builder.newInstance().build();

        final Duty duty1 = Duty.Builder.newInstance()
                .parentPermission(permission)
                .consequence(duty)
                .build();

        // when
        final Duty result = duty1.withTarget(target);

        // then
        assertThat(result.getTarget()).isEqualTo(target);
        assertThat(result.getConsequence().getTarget()).isEqualTo(target);
    }

}
