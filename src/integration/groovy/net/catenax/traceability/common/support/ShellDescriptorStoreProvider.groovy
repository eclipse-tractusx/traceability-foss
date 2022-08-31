package net.catenax.traceability.common.support


import net.catenax.traceability.assets.infrastructure.adapters.jpa.shelldescriptor.ShellDescriptorDbStore

interface ShellDescriptorStoreProvider {
	ShellDescriptorDbStore shellDescriptorStore()
}
