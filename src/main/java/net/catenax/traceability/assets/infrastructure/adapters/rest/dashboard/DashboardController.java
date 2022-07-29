package net.catenax.traceability.assets.infrastructure.adapters.rest.dashboard;

import net.catenax.traceability.assets.domain.Dashboard;
import net.catenax.traceability.assets.domain.DashboardService;
import net.catenax.traceability.common.security.KeycloakAuthentication;
import net.catenax.traceability.common.security.InjectedKeycloakAuthentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class DashboardController {

	private final DashboardService dashboardService;

	public DashboardController(DashboardService dashboardService) {
		this.dashboardService = dashboardService;
	}

	@GetMapping("/dashboard")
	public Dashboard dashboard(@InjectedKeycloakAuthentication KeycloakAuthentication keycloakAuthentication) {
		return dashboardService.getDashboard(keycloakAuthentication);
	}
}
