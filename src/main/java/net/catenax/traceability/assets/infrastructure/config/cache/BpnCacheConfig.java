package net.catenax.traceability.assets.infrastructure.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class BpnCacheConfig {

	@Bean
	public CacheManager bpnCacheManager(@Autowired BpnCacheProperties bpnCacheProperties) {
		Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
			.maximumSize(bpnCacheProperties.getMaximumSize())
			.expireAfterWrite(bpnCacheProperties.getExpireAfterWrite());

		CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager(bpnCacheProperties.getName());
		caffeineCacheManager.setCaffeine(caffeine);
		caffeineCacheManager.setAllowNullValues(false);

		return caffeineCacheManager;
	}
}
