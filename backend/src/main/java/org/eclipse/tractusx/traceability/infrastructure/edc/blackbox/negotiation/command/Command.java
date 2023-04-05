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

package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.negotiation.command;

import java.util.UUID;

/**
 * Base class for all command objects. Contains basic information such as a command ID
 * and an error count, which indicates how many times a particular command has already errored out. This is useful
 * if the command should be discarded after a few retries.
 * <p>
 * Please take note of the following guidelines:
 * <ul>
 * <li>Commands are simple POJOs, that must be (JSON-)serializable and can therefore not have references to other services.</li>
 * <li>Commands must contain all the information that a {@link Command} requires to do its job.</li>
 * <li>Commands do not have results. Any results that an operation may produce are to be handled by the {@link Command}</li>
 * </ul>
 */
public abstract class Command {
	private final String commandId;
	private int errorCount = 0;

	/**
	 * Creates a new Command assigning a random UUID as commandId
	 */
	protected Command() {
		this(UUID.randomUUID().toString());
	}

	/**
	 * Creates a new Command assigning a specified String as command ID
	 */
	protected Command(String commandId) {
		this.commandId = commandId;
	}

	public String getCommandId() {
		return commandId;
	}

	public void increaseErrorCount() {
		errorCount++;
	}


	/**
	 * Indicates whether {@link Command#getMaxRetry()} has been reached.
	 */
	public boolean canRetry() {
		return errorCount < getMaxRetry();
	}

	/**
	 * Indicates the maximum amount of times a Command should be retried. Defaults to 5.
	 */
	protected int getMaxRetry() {
		return 5;
	}
}
