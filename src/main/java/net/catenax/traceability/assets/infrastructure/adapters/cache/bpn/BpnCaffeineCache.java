package net.catenax.traceability.assets.infrastructure.adapters.cache.bpn;

import net.catenax.traceability.assets.infrastructure.config.cache.BpnCacheProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BpnCaffeineCache implements BpnCache {

	private final Cache cache;

	public BpnCaffeineCache(CacheManager cacheManager, BpnCacheProperties bpnCacheProperties) {
		this.cache = cacheManager.getCache(bpnCacheProperties.getName());
	}

	@Override
	public void put(BpnMapping bpnMapping) {
		cache.put(bpnMapping.bpn(), bpnMapping.companyName());
	}

	@Override
	public Optional<String> getCompanyName(String bpn) {
		return Optional.ofNullable(cache.get(bpn, String.class));
	}

	@Override
	public void clear() {
		cache.clear();
	}
}
