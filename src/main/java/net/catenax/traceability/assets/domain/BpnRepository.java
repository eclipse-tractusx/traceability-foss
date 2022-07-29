package net.catenax.traceability.assets.domain;

import java.util.Optional;

public interface BpnRepository {
	Optional<String> findManufacturerName(String manufacturerId);
}
