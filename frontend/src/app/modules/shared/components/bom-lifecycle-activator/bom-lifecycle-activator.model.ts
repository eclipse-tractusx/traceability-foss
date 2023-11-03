/********************************************************************************
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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

export interface BomLifecycleSize {
    asDesignedSize: number,
    asPlannedSize: number,
    asOrderedSize: number,
    asBuiltSize: number,
    asSupportedSize: number,
    asRecycledSize: number,
}

export interface BomLifecycleConfig {
    asDesignedActive: boolean,
    asPlannedActive: boolean,
    asOrderedActive: boolean,
    asBuiltActive: boolean,
    asSupportedActive: boolean,
    asRecycledActive: boolean,
}

export enum BomLifecycleType {
    AS_DESIGNED = 'AsDesigned/AsDeveloped',
    AS_PLANNED = 'AsPlanned',
    AS_ORDERED = 'AsOrdered',
    AS_BUILT = 'AsBuilt',
    AS_SUPPORTED = 'AsSupported/AsFlying/AsMaintainted/AsOperated',
    AS_RECYCLED = 'AsRecycled'
}
