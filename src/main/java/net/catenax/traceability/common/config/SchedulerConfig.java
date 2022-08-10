package net.catenax.traceability.common.config;

import net.catenax.traceability.assets.domain.AssetService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.annotation.PostConstruct;

@Profile("!integration")
@Configuration
public class SchedulerConfig {

	private final AssetService assetService;

	public SchedulerConfig(AssetService assetService) {
		this.assetService = assetService;
	}

	@PostConstruct
	public void initializeAssets() {
		assetService.synchronizeAssets();
	}

	@Bean
	public ThreadPoolTaskScheduler threadPoolTaskScheduler(){
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(5);
		threadPoolTaskScheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
		return threadPoolTaskScheduler;
	}

}
