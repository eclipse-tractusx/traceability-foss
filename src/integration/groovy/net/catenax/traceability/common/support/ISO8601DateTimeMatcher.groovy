/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
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

package net.catenax.traceability.common.support

import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class ISO8601DateTimeMatcher extends TypeSafeMatcher<String> {


	@Override
	protected boolean matchesSafely(String item) {
		try {
			DateTimeFormatter.ISO_INSTANT.parse(item)
		} catch (DateTimeParseException ignored) {
			return false
		}

		return true
	}

	@Override
	void describeTo(Description description) {
		description.appendText("ISO 8601 date")
	}

	static Matcher<String> isIso8601DateTime() {
		return new ISO8601DateTimeMatcher()
	}
}
