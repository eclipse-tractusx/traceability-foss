/*
 *  Copyright (c) 2020, 2021 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - initial API and implementation
 *
 */

package net.catenax.traceability.infrastructure.edc.blackbox.policy;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * An expression that can be evaluated.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "edctype")
public abstract class Expression {

	public abstract <R> R accept(Visitor<R> visitor);

	public interface Visitor<R> {

		R visitLiteralExpression(LiteralExpression expression);

	}

}
