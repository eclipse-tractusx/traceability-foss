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

package org.eclipse.tractusx.traceability.common.support


import org.eclipse.tractusx.traceability.assets.domain.model.ShellDescriptor

trait ShellDescriptorSupport implements ShellDescriptorStoreProvider {

	ShellDescriptorsAssertion assertShellDescriptors() {
		return new ShellDescriptorsAssertion(shellDescriptorRepository().findAll())
	}

	static class ShellDescriptorsAssertion {
		private List<ShellDescriptor> descriptors

		private ShellDescriptorsAssertion(List<ShellDescriptor> descriptors) {
			this.descriptors = descriptors
		}

		ShellDescriptorsAssertion hasSize(int count) {
			assert descriptors.size() == count
			return this;
		}

		ShellDescriptorsAssertion containsShellDescriptorWithId(String ... shellDescriptorId) {
			assert descriptors
				.collect {it.shellDescriptorId()}
				.containsAll(shellDescriptorId)
			return this;
		}

		ShellDescriptorsAssertion doesNotContainShellDescriptorWithId(String shellDescriptorId) {
			assert descriptors.find {it.shellDescriptorId() == shellDescriptorId} == null
			return this;
		}
	}

}
