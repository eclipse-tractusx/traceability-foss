/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.core.registry.EntryRemovedEvent;
import io.github.resilience4j.core.registry.EntryReplacedEvent;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import io.github.resilience4j.retry.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.irs.edc.client.policy.AcceptedPoliciesProvider;
import org.eclipse.tractusx.irs.edc.client.policy.AcceptedPolicy;
import org.eclipse.tractusx.irs.edc.client.policy.Constraint;
import org.eclipse.tractusx.irs.edc.client.policy.Constraints;
import org.eclipse.tractusx.irs.edc.client.policy.OperatorType;
import org.eclipse.tractusx.irs.edc.client.policy.Permission;
import org.eclipse.tractusx.irs.edc.client.policy.Policy;
import org.eclipse.tractusx.irs.edc.client.policy.PolicyType;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.time.OffsetDateTime;
import java.util.List;

@Configuration
@ConfigurationPropertiesScan(basePackages = "org.eclipse.tractusx.traceability.*")
@EnableWebMvc
@EnableAsync(proxyTargetClass = true)
@EnableConfigurationProperties
@RequiredArgsConstructor
@Slf4j
@EnableJpaRepositories(basePackages = "org.eclipse.tractusx.traceability.*")
public class ApplicationConfig {
    private final AcceptedPoliciesProvider.DefaultAcceptedPoliciesProvider defaultAcceptedPoliciesProvider;
    private static final String ID_TRACE_CONSTRAINT = "ID 3.0 Trace";


	@Bean
	public InternalResourceViewResolver defaultViewResolver() {
		return new InternalResourceViewResolver();
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
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setCharacterEncoding("UTF-8");
		return templateResolver;
	}

	public ITemplateResolver textTemplateResolver() {
		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setSuffix(".txt");
		templateResolver.setTemplateMode(TemplateMode.TEXT);
		templateResolver.setCharacterEncoding("UTF-8");
		return templateResolver;
	}

    @Bean
    public void registerDecentralRegistryPermissions() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            AcceptedPolicy acceptedPolicy = buildAcceptedPolicy();
            defaultAcceptedPoliciesProvider.addAcceptedPolicies(List.of(acceptedPolicy));
            log.info("Successfully added permission to irs client lib provider: {}", mapper.writeValueAsString(acceptedPolicy));
        } catch (Exception exception) {
            log.error("Failed to create Irs Policies : ", exception);
        }
    }

    @NotNull
    private static AcceptedPolicy buildAcceptedPolicy() {
        OffsetDateTime offsetDateTime = OffsetDateTime.now().plusMonths(1);
        List<Permission> permissions = List.of(
                new Permission(
                        PolicyType.USE,
                        List.of(new Constraints(
                                List.of(new Constraint("PURPOSE", OperatorType.EQ, List.of(ID_TRACE_CONSTRAINT))),
                                List.of(new Constraint("PURPOSE", OperatorType.EQ, List.of(ID_TRACE_CONSTRAINT)))))
                )
        );
        Policy policy = new Policy(ID_TRACE_CONSTRAINT, OffsetDateTime.now(), offsetDateTime, permissions);
        return new AcceptedPolicy(policy, offsetDateTime);
    }

    @Bean
	public RegistryEventConsumer<Retry> myRetryRegistryEventConsumer() {
		final Logger logger = LoggerFactory.getLogger("RetryLogger");

		return new RegistryEventConsumer<>() {
			@Override
			public void onEntryAddedEvent(EntryAddedEvent<Retry> entryAddedEvent) {
				entryAddedEvent.getAddedEntry().getEventPublisher()
					.onEvent(event -> logger.info(event.toString()));
			}

			@Override
			public void onEntryReplacedEvent(EntryReplacedEvent<Retry> entryReplacedEvent) {
			}

			@Override
			public void onEntryRemovedEvent(EntryRemovedEvent<Retry> entryRemoveEvent) {
			}
		};
	}
}
