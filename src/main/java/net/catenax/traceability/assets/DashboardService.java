package net.catenax.traceability.assets;

import net.catenax.traceability.config.security.KeycloakAuthentication;
import net.catenax.traceability.config.security.KeycloakRole;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class DashboardService {

	private final AssetRepository assetRepository;

	public DashboardService(AssetRepository assetRepository) {
		this.assetRepository = assetRepository;
	}

	public Dashboard getDashboard(KeycloakAuthentication keycloakAuthentication) {
		long assetsCount = assetRepository.countAssets();

		if (keycloakAuthentication.hasRole(KeycloakRole.USER)) {
			return new Dashboard(assetsCount, null);
		} else if (keycloakAuthentication.hasAtLeastOneRole(KeycloakRole.ADMIN, KeycloakRole.SUPERVISOR)) {
			return new Dashboard(assetsCount, assetsCount);
		} else {
			throw new AccessDeniedException("User has invalid role to access the dashboard.");
		}
	}
}
