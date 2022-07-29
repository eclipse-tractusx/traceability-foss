package net.catenax.traceability.assets.infrastructure.config.openapi.bpn;

import feign.RequestInterceptor;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

import java.util.concurrent.TimeUnit;

@Configuration
public class BpnApiConfig {

	@Bean
	public OkHttpClient bpnApiOkHttpClient(@Autowired BpnApiProperties bpnApiProperties) {
		return new OkHttpClient.Builder()
			.connectTimeout(bpnApiProperties.getConnectionTimeoutMillis(), TimeUnit.MILLISECONDS)
			.readTimeout(bpnApiProperties.getReadTimeoutMillis(), TimeUnit.MILLISECONDS)
			.connectionPool(
				new ConnectionPool(
					bpnApiProperties.getMaxIdleConnections(),
					bpnApiProperties.getKeepAliveDurationMinutes(),
					TimeUnit.MINUTES
				)
			)
			.build();
	}

	@Bean
	public RequestInterceptor requestInterceptor(OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager) {
		return new KeycloakAuthorizationInterceptor(oAuth2AuthorizedClientManager);
	}
}
