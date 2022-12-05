package org.eclipse.tractusx.traceability.common.support


import org.eclipse.tractusx.traceability.assets.domain.ports.BpnRepository

interface BpnRepositoryProvider {
	BpnRepository bpnRepository()
}
