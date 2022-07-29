package net.catenax.traceability.assets.infrastructure.adapters.cache.bpn;

import java.util.Optional;

public interface BpnCache {
	void put(BpnMapping bpnMapping);

	Optional<String> getCompanyName(String bpn);

	void clear();
}
