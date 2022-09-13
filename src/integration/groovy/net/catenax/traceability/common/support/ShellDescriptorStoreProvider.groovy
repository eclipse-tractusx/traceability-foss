package net.catenax.traceability.common.support

import net.catenax.traceability.assets.domain.ports.ShellDescriptorRepository

interface ShellDescriptorStoreProvider {
	ShellDescriptorRepository shellDescriptorRepository()
}
