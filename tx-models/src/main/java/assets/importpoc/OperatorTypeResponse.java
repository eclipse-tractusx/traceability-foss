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
import io.swagger.annotations.ApiModelProperty;


@JsonSerialize(
        using = ToStringSerializer.class
)
public enum OperatorTypeResponse {
    @ApiModelProperty("eq")
    EQ("eq", "Equals to"),
    @ApiModelProperty("neq")
    NEQ("neq", "Not equal to"),
    @ApiModelProperty("lt")
    LT("lt", "Less than"),
    @ApiModelProperty("gt")
    GT("gt", "Greater than"),
    @ApiModelProperty("in")
    IN("in", "In"),
    @ApiModelProperty("lteq")
    LTEQ("lteq", "Less than or equal to"),
    @ApiModelProperty("gteq")
    GTEQ("gteq", "Greater than or equal to"),
    @ApiModelProperty("isA")
    ISA("isA", "Is a"),
    @ApiModelProperty("hasPart")
    HASPART("hasPart", "Has part"),
    @ApiModelProperty("isPartOf")
    ISPARTOF("isPartOf", "Is part of"),
    @ApiModelProperty("isOneOf")
    ISONEOF("isOneOf", "Is one of"),
    @ApiModelProperty("isAllOf")
    ISALLOF("isAllOf", "Is all of"),
    @ApiModelProperty("isNoneOf")
    ISNONEOF("isNoneOf", "Is none of");

   final String code;

   final String label;

     OperatorTypeResponse(String code, String label) {
        this.code = code;
        this.label = label;
    }

}
