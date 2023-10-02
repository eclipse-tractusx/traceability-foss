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

package org.eclipse.tractusx.traceability.submodel.infrastructure.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.eclipse.tractusx.traceability.submodel.domain.model.Submodel;

@Entity
@Table(name = "submodel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmodelEntity {
    @Id
    private String id;

    private String submodel;

    public static SubmodelEntity from(Submodel submodel) {
        return SubmodelEntity.builder()
                .id(submodel.getId())
                .submodel(submodel.getPayload())
                .build();
    }

    public Submodel toDomain() {
        return Submodel.builder()
                .id(getId())
                .payload(getSubmodel())
                .build();
    }
}
