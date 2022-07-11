package net.catenax.traceability.api.dashboard;

import net.catenax.traceability.assets.Dashboard;
import net.catenax.traceability.assets.DashboardService;
import net.catenax.traceability.config.security.KeycloakAuthentication;
import net.catenax.traceability.config.security.InjectedKeycloakAuthentication;
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
