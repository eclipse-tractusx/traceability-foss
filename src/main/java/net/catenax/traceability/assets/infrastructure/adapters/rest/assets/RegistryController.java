package net.catenax.traceability.assets.infrastructure.adapters.rest.assets;

import net.catenax.traceability.assets.application.RegistryFacade;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class RegistryController {

	private final RegistryFacade registryFacade;

	public RegistryController(RegistryFacade registryFacade) {
		this.registryFacade = registryFacade;
	}

	@GetMapping("/registry/fetch/{bpn}")
	public void assets(@PathVariable String bpn) {
		registryFacade.loadShellDescriptorsAsyncFor(bpn);
	}
}
