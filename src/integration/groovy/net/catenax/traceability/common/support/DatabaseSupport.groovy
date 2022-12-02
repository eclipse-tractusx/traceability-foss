package net.catenax.traceability.common.support

import org.springframework.test.jdbc.JdbcTestUtils

trait DatabaseSupport implements DatabaseProvider {

	private static final List<String> TABLES = [
		"asset_child_descriptors",
		"assets_investigations",
		"assets_notifications",
		"asset",
		"shell_descriptor",
		"bpn_storage",
		"notification",
		"investigation",
		"registry_lookup_metrics"
	]

	void clearAllTables() {
		TABLES.each {
			JdbcTestUtils.deleteFromTables(jdbcTemplate(), it)
		}
	}
}
