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
package org.eclipse.tractusx.traceability.common.config;

import org.springframework.core.convert.converter.Converter;

import java.util.List;

public class StringToStringListConverter implements Converter<String, List<String>> {

    /**
     * This method was created to override the default behaviour of Spring. By default, Spring splits a String by comma
     * to create a list. To accept a sort query parameter like "sort=name,desc" we need to override the default
     * implementation of Spring.
     * If for e.g. two sort parameters are provided (for cascaded sorting), this does not apply, because in that case
     * we have a List<String> to List<String>, where on comma separation happens.
     *
     * @param from the String that is read from a single query param
     * @return a list of the String with only one entry without comma separation.
     */
    @Override
    public List<String> convert(final String from) {
        return List.of(from);
    }
}
