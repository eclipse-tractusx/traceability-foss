/********************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
package assets.importpoc;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;


@JsonSerialize(
        using = ToStringSerializer.class
)
public enum OperatorTypeResponse {
    @Schema(description = "eq")
    EQ("eq", "Equals to"),
    @Schema(description = "neq")
    NEQ("neq", "Not equal to"),
    @Schema(description = "lt")
    LT("lt", "Less than"),
    @Schema(description = "gt")
    GT("gt", "Greater than"),
    @Schema(description = "in")
    IN("in", "In"),
    @Schema(description = "lteq")
    LTEQ("lteq", "Less than or equal to"),
    @Schema(description = "gteq")
    GTEQ("gteq", "Greater than or equal to"),
    @Schema(description = "isA")
    ISA("isA", "Is a"),
    @Schema(description = "hasPart")
    HASPART("hasPart", "Has part"),
    @Schema(description = "isPartOf")
    ISPARTOF("isPartOf", "Is part of"),
    @Schema(description = "isOneOf")
    ISONEOF("isOneOf", "Is one of"),
    @Schema(description = "isAllOf")
    ISALLOF("isAllOf", "Is all of"),
    @Schema(description = "isNoneOf")
    ISNONEOF("isNoneOf", "Is none of");

    final String code;

    final String label;

    OperatorTypeResponse(String code, String label) {
        this.code = code;
        this.label = label;
    }

}
