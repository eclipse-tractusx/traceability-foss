package net.catenax.traceability.common.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@TestConfiguration
class SecurityTestConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		super.configure(http)

		http.csrf().disable()
	}
}
