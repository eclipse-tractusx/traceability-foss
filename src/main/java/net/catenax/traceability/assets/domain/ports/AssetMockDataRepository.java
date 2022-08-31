package net.catenax.traceability.assets.domain.ports;

import net.catenax.traceability.assets.domain.model.Asset;

import java.util.List;

public interface AssetMockDataRepository {
	List<Asset> mockData();
}
