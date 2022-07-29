package net.catenax.traceability.common.config;

import feign.Contract;
import feign.Logger;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = {"net.catenax.traceability.assets.infrastructure.adapters.openapi.*"})
public class FeignGlobalConfig {

	@Bean
	public Logger.Level feignLoggerLevel() {
		return Logger.Level.FULL;
	}

	@Bean
	public Contract useFeignAnnotations() {
		return new Contract.Default();
	}
}
