package net.catenax.traceability.assets.application;

import net.catenax.traceability.assets.domain.service.ShellDescriptiorsService;
import net.catenax.traceability.assets.infrastructure.config.async.AssetsAsyncConfig;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class RegistryFacade {

	private final ShellDescriptiorsService shellDescriptiorsService;

	public RegistryFacade(ShellDescriptiorsService shellDescriptiorsService) {
		this.shellDescriptiorsService = shellDescriptiorsService;
	}

	public void loadShellDescriptorsFor(String bpn) {
		shellDescriptiorsService.loadShellDescriptorsFor(bpn);
	}

	@Async(value = AssetsAsyncConfig.LOAD_SHELL_DESCRIPTORS_EXECUTOR)
	public void loadShellDescriptorsAsyncFor(String bpn) {
		shellDescriptiorsService.loadShellDescriptorsFor(bpn);
	}
}
