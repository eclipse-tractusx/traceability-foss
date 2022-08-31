package net.catenax.traceability.assets.infrastructure.config.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AssetsAsyncConfig {

	public static final String SYNCHRONIZE_ASSETS_EXECUTOR = "synchronize-assets-executor";
	public static final String LOAD_SHELL_DESCRIPTORS_EXECUTOR = "load-shell-descriptors-executor";

	@Bean(name = SYNCHRONIZE_ASSETS_EXECUTOR)
	public ThreadPoolTaskExecutor synchronizeAssetsExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setMaxPoolSize(100);
		executor.setQueueCapacity(50);
		executor.setThreadNamePrefix("%s-".formatted(SYNCHRONIZE_ASSETS_EXECUTOR));

		return executor;
	}

	@Bean(name = LOAD_SHELL_DESCRIPTORS_EXECUTOR)
	public ThreadPoolTaskExecutor loadShellDescriptorsExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setMaxPoolSize(100);
		executor.setQueueCapacity(50);
		executor.setThreadNamePrefix("%s-".formatted(LOAD_SHELL_DESCRIPTORS_EXECUTOR));

		return executor;
	}
}
