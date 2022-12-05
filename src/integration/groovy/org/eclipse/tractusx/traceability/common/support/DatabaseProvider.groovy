package org.eclipse.tractusx.traceability.common.support

import org.springframework.jdbc.core.JdbcTemplate

interface DatabaseProvider {
	JdbcTemplate jdbcTemplate()
}
