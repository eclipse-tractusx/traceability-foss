package net.catenax.traceability.common.support

import net.catenax.traceability.assets.domain.ports.BpnRepository

interface BpnRepositoryProvider {
	BpnRepository bpnRepository()
}
