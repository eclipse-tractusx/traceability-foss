package net.catenax.traceability.common.support

trait ShellDescriptorStoreProviderSupport implements ShellDescriptorStoreProvider {

	void assertDescriptorsSizeFor(String shellDescriptorId, int size) {
		assert shellDescriptorStore().count(shellDescriptorId) == (long)size
	}

	void assertNoDescriptorsStoredFor(String shellDescriptorId) {
		assertDescriptorsSizeFor(shellDescriptorId, 0)
	}
}
