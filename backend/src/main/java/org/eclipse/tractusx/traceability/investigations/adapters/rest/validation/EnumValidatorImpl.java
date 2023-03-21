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

package org.eclipse.tractusx.traceability.investigations.adapters.rest.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Stream;

public class EnumValidatorImpl implements ConstraintValidator<EnumValidator, CharSequence> {
    private List<String> acceptedValues;

    @Override
    public void initialize(final EnumValidator annotation) {
        acceptedValues =
                Stream.of(annotation.enumClass().getEnumConstants()).map(Enum::name).toList();
    }

    @Override
    public boolean isValid(final CharSequence value, final ConstraintValidatorContext context) {
        // This should be validated by NotNull
        if (value == null) {
            return true;
        }
        return acceptedValues.contains(value.toString());
    }
}
