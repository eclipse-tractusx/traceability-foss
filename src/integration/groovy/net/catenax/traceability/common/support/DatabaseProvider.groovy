package net.catenax.traceability.common.support

import org.springframework.jdbc.core.JdbcTemplate

interface DatabaseProvider {
	JdbcTemplate jdbcTemplate()
}
