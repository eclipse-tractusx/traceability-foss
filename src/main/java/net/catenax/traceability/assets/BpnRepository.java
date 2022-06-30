package net.catenax.traceability.assets;

import java.util.Optional;

public interface BpnRepository {
	Optional<String> findManufacturerName(String manufacturerId);
}
