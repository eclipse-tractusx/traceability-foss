package net.catenax.traceability.common.support

import net.catenax.traceability.assets.domain.model.ShellDescriptor

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
