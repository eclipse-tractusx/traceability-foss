package org.eclipse.tractusx.traceability.common.support


import org.eclipse.tractusx.traceability.assets.domain.ports.ShellDescriptorRepository

interface ShellDescriptorStoreProvider {
	ShellDescriptorRepository shellDescriptorRepository()
}
