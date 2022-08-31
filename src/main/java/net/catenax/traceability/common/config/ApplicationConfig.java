/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package net.catenax.traceability.common.config;

import net.catenax.traceability.common.docs.SwaggerPageable;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import springfox.documentation.builders.OAuth2SchemeBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.HttpAuthenticationScheme;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;

import java.util.Arrays;
import java.util.List;

@Configuration
@ConfigurationPropertiesScan(basePackages = "net.catenax.traceability.*")
@EnableOpenApi
@EnableWebMvc
@EnableAsync
@EnableConfigurationProperties
@EnableJpaRepositories(basePackages = "net.catenax.traceability.*")
public class ApplicationConfig {

	public static final AuthorizationScope[] DEFAULT_SCOPES = {
		new AuthorizationScope("uma_authorization", "UMA authorization")
	};

	@Value("${keycloak.realm:}")
	private String realm;

	@Value("${keycloak.resource:}")
	private String clientId;

	@Value("${keycloak.auth-server-url:}")
	private String authServerUrl;

	@Value("${spring.mail.templates.path}")
	private String mailTemplatesPath;

	@Bean
	public InternalResourceViewResolver defaultViewResolver() {
		return new InternalResourceViewResolver();
	}

	@Bean
	public KeycloakSpringBootConfigResolver keycloakConfigResolver() {
		return new KeycloakSpringBootConfigResolver();
	}

	@Bean
	public SpringTemplateEngine thymeleafTemplateEngine() {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.addTemplateResolver(htmlTemplateResolver());
		templateEngine.addTemplateResolver(textTemplateResolver());
		return templateEngine;
	}

	@Bean(name = "security-context-async")
	public ThreadPoolTaskExecutor securityContextAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setMaxPoolSize(100);
		executor.setQueueCapacity(50);
		executor.setThreadNamePrefix("security-context-async-");
		return executor;
	}

	@Bean
	public DelegatingSecurityContextAsyncTaskExecutor taskExecutor(@Qualifier("security-context-async") ThreadPoolTaskExecutor threadPoolTaskExecutor) {
		return new DelegatingSecurityContextAsyncTaskExecutor(threadPoolTaskExecutor);
	}

	public ITemplateResolver htmlTemplateResolver() {
		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setPrefix(mailTemplatesPath + "/");
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setCharacterEncoding("UTF-8");
		return templateResolver;
	}

	public ITemplateResolver textTemplateResolver() {
		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setPrefix(mailTemplatesPath + "/");
		templateResolver.setSuffix(".txt");
		templateResolver.setTemplateMode(TemplateMode.TEXT);
		templateResolver.setCharacterEncoding("UTF-8");
		return templateResolver;
	}

	@Bean
	public Docket swaggerSpringMvcPlugin() {
		return new Docket(DocumentationType.OAS_30)
			.securitySchemes(List.of(keycloakAuthenticationScheme(), bearerTokenAuthenticationScheme()))
			.securityContexts(List.of(securityContext()))
			.select()
			.apis(RequestHandlerSelectors.basePackage("net.catenax.traceability"))
			.build()
			.directModelSubstitute(Pageable.class, SwaggerPageable.class);
	}

	@Bean
	public SecurityConfiguration security() {
		return SecurityConfigurationBuilder.builder()
			.clientId(clientId)
			.realm(realm)
			.appName(clientId)
			.scopeSeparator(",")
			.additionalQueryStringParams(null)
			.build();
	}

	private static SecurityScheme bearerTokenAuthenticationScheme() {
		return HttpAuthenticationScheme
			.JWT_BEARER_BUILDER
			.name("Bearer")
			.build();
	}

	private SecurityScheme keycloakAuthenticationScheme() {
		return new OAuth2SchemeBuilder("authorizationCode")
			.name("Keycloak")
			.authorizationUrl(authServerUrl + "/realms/" + realm + "/protocol/openid-connect/auth")
			.tokenUrl(authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token")
			.scopes(Arrays.asList(DEFAULT_SCOPES))
			.build();
	}

	private SecurityContext securityContext() {
		return SecurityContext
			.builder()
			.securityReferences(List.of(
				new SecurityReference("Keycloak", DEFAULT_SCOPES),
				new SecurityReference("Bearer", DEFAULT_SCOPES)
			))
			.operationSelector(operationContext -> HttpMethod.GET.equals(operationContext.httpMethod()))
			.build();
	}
}
