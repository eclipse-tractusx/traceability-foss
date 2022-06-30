package net.catenax.traceability.config;

import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticationProcessingFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@KeycloakConfiguration
@ConditionalOnProperty(name = "keycloak.enabled", havingValue = "true", matchIfMissing = true)
public class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

	private static final String[] WHITELIST_URLS = {
		"/v3/api-docs/**",
		"/swagger-ui/**",
		"/swagger-ui.html",
		"/actuator/**",
	};

	@Override
	public void configure(AuthenticationManagerBuilder auth) {
		KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
		keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
		auth.authenticationProvider(keycloakAuthenticationProvider);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		super.configure(http);

		http
			.cors()
			.and()
			.anonymous().disable()
			.authorizeRequests()
			.antMatchers(WHITELIST_URLS).permitAll()
			.antMatchers("/api/**").authenticated();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		super.configure(web);

		web.ignoring().antMatchers(WHITELIST_URLS);
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource(@Value("${cors.origins}") List<String> origins) {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(origins);
		configuration.setAllowedMethods(List.of("*"));
		configuration.setAllowedHeaders(List.of("*"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}

	@Bean
	public KeycloakAuthenticationProcessingFilter keycloakAuthenticationProcessingFilter() throws Exception {
		KeycloakAuthenticationProcessingFilter filter = new KeycloakAuthenticationProcessingFilter(authenticationManagerBean());
		filter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy());
		filter.setAuthenticationFailureHandler(new ErrorHandlingConfig());

		return filter;
	}

	@Bean
	@Override
	protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
		return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
	}
}
