package net.catenax.traceability.common.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.PostgreSQLContainer

@TestConfiguration
class PostgreSQLConfig {

	private static final DATABASE_NAME = "traceability-integration-tests"
	private static final DATABASE_USERNAME = "test"
	private static final DATABASE_PASSWORD = "test"

	private static final PostgreSQLContainer POSTGRE_SQL_CONTAINER

	static {
		POSTGRE_SQL_CONTAINER = new PostgreSQLContainer("postgres:14.4")
			.withDatabaseName(DATABASE_NAME)
			.withUsername(DATABASE_USERNAME)
			.withPassword(DATABASE_PASSWORD)

		POSTGRE_SQL_CONTAINER.start()
	}

	static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
		void initialize(ConfigurableApplicationContext configurableApplicationContext) {
			TestPropertyValues.of(
				"spring.datasource.url=${POSTGRE_SQL_CONTAINER.jdbcUrl}",
				"spring.datasource.username=${DATABASE_USERNAME}",
				"spring.datasource.password=${DATABASE_PASSWORD}",
			).applyTo(configurableApplicationContext.getEnvironment())
		}
	}
}
