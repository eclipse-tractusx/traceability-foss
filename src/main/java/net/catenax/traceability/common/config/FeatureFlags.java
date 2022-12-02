package net.catenax.traceability.common.config;

import static net.catenax.traceability.common.config.ApplicationProfiles.DEV;
import static net.catenax.traceability.common.config.ApplicationProfiles.TESTS;

public class FeatureFlags {

	public static final String NOTIFICATIONS_ENABLED_PROFILES = TESTS + " | " + DEV;

	private FeatureFlags() {
	}
}
