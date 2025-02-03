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
package org.eclipse.tractusx.traceability.common.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UrlUtilsTest {

    @Test
    void shouldReturnNullWhenBothBaseUrlAndPathAreNull() {
        // given
        String baseUrl = null;
        String path = null;

        // when
        String result = UrlUtils.appendSuffix(baseUrl, path);

        // then
        assertNull(result);
    }

    @Test
    void shouldReturnBaseUrlWhenPathIsNullAndBaseUrlIsNonEmpty() {
        // given
        String baseUrl = "http://example.com/api";
        String path = null;

        // when
        String result = UrlUtils.appendSuffix(baseUrl, path);

        // then
        assertEquals(baseUrl, result);
    }

    @Test
    void shouldReturnPathWhenBaseUrlIsNullAndPathIsNonEmpty() {
        // given
        String baseUrl = null;
        String path = "/resource";

        // when
        String result = UrlUtils.appendSuffix(baseUrl, path);

        // then
        assertEquals("/resource", result);
    }

    @Test
    void shouldReturnBaseUrlUnchangedWhenBaseUrlEndsWithPath() {
        // given
        String baseUrl = "http://example.com/api/resource";
        String path = "/resource";

        // when
        String result = UrlUtils.appendSuffix(baseUrl, path);

        // then
        assertEquals(baseUrl, result);
    }

    @Test
    void shouldCorrectlyAppendPathWhenBaseUrlDoesNotEndWithPath() {
        // given
        String baseUrl = "http://example.com/api";
        String path = "/resource";

        // when
        String result = UrlUtils.appendSuffix(baseUrl, path);

        // then
        assertEquals("http://example.com/api/resource", result);
    }

    @Test
    void shouldRemoveOneSlashWhenBaseUrlEndsWithSlashAndPathStartsWithSlash() {
        // given
        String baseUrl = "http://example.com/api/";
        String path = "/resource";

        // when
        String result = UrlUtils.appendSuffix(baseUrl, path);

        // then
        assertEquals("http://example.com/api/resource", result);
    }

    @Test
    void shouldHandleEmptyBaseUrlAndReturnOnlyPath() {
        // given
        String baseUrl = "";
        String path = "/resource";

        // when
        String result = UrlUtils.appendSuffix(baseUrl, path);

        // then
        assertEquals("/resource", result);
    }

    @Test
    void shouldHandleEmptyBaseUrlAndPathAndReturnEmptyString() {
        // given
        String baseUrl = "";
        String path = "";

        // when
        String result = UrlUtils.appendSuffix(baseUrl, path);

        // then
        assertEquals("", result);
    }

    @Test
    void shouldHandleBaseUrlEndingWithSubstringOfPath() {
        // given
        String baseUrl = "http://example.com/api/res";
        String path = "/resource";

        // when
        String result = UrlUtils.appendSuffix(baseUrl, path);

        // then
        assertEquals("http://example.com/api/res/resource", result);
    }

    @Test
    void shouldHandleBaseUrlAndPathBeingSameAndReturnBaseUrl() {
        // given
        String baseUrl = "http://example.com/api/resource";
        String path = "http://example.com/api/resource";

        // when
        String result = UrlUtils.appendSuffix(baseUrl, path);

        // then
        assertEquals(baseUrl, result);
    }

    @Test
    void shouldHandleEmptyPathAndReturnOnlyBaseUrl() {
        // given
        String baseUrl = "http://example.com/api";
        String path = "";

        // when
        String result = UrlUtils.appendSuffix(baseUrl, path);

        // then
        assertEquals(baseUrl, result);
    }

}
