/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
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
package org.eclipse.tractusx.traceability.test;



import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

    public class TestStepDefinition {
        private Integer int1;
        private Integer int2;
        private Integer result;

        @Given("I have entered {int} into the calculator")
        public void iHaveEnteredIntoTheCalculator(Integer int1) {
            this.int2 = this.int1;
            this.int1 = int1;
        }

        @When("I press add")
        public void iPressAdd() {
            this.result = this.int1 + this.int2;
        }

        @When("I press multiply")
        public void iPressMultiply() {
            this.result = this.int1 * this.int2;
        }

        @Then("the result should be {int} on the screen")
        public void theResultShouldBeOnTheScreen(Integer value) {
            assertThat(this.result).isEqualTo(value);
        }
    }

