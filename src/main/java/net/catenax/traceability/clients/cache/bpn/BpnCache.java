package net.catenax.traceability.clients.cache.bpn;

import java.util.Optional;

public interface BpnCache {
	void put(BpnMapping bpnMapping);

	Optional<String> getCompanyName(String bpn);

	void clear();
}
