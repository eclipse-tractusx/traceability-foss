package net.catenax.traceability.common.support

import org.springframework.beans.factory.annotation.Value

trait BpnSupport implements BpnRepositoryProvider, AssetRepositoryProvider {

	@Value('${traceability.bpn}')
	private String bpn

	void cachedBpnsForDefaultAssets() {
		List<String> assetIds = assetsConverter().readAndConvertAssets().collect { it.manufacturerId }
		Map<String, String> bpnMappings = new HashMap<>()

		for (int i = 0; i < assetIds.size(); i++) {
			bpnMappings.put(assetIds.get(i), "Manufacturer Name $i".toString())
		}

		bpnRepository().updateManufacturers(bpnMappings)
	}

	String testBpn() {
		return bpn
	}
}
