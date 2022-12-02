/*
 *  Copyright (c) 2022 Microsoft Corporation and others
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - initial API and implementation
 *       ZF Friedrichshafen AG - Refactored
 *
 */

package net.catenax.traceability.infrastructure.edc.blackbox.notification;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;


@JsonDeserialize(builder = TransferId.Builder.class)
public class TransferId {
	private String id;

	private TransferId() {
	}

	public String getId() {
		return id;
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static final class Builder {
		private final TransferId dto;

		private Builder() {
			dto = new TransferId();
		}

		@JsonCreator
		public static Builder newInstance() {
			return new Builder();
		}

		public Builder id(String id) {
			dto.id = id;
			return this;
		}

		public TransferId build() {
			return dto;
		}
	}
}
