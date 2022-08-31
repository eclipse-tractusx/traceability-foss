package net.catenax.traceability.assets.infrastructure.adapters;

import net.catenax.traceability.assets.domain.model.ShellDescriptor;
import net.catenax.traceability.assets.domain.ports.ShellDescriptorStore;
import net.catenax.traceability.assets.infrastructure.adapters.jpa.shelldescriptor.ShellDescriptorDbStore;
import net.catenax.traceability.assets.infrastructure.adapters.openapi.registry.RegistryService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ShellDescriptorStoreAdapter implements ShellDescriptorStore {

	private final ShellDescriptorDbStore shellDescriptorDbStore;
	private final RegistryService registryService;

	public ShellDescriptorStoreAdapter(ShellDescriptorDbStore shellDescriptorDbStore, RegistryService registryService) {
		this.shellDescriptorDbStore = shellDescriptorDbStore;
		this.registryService = registryService;
	}

	@Override
	public void store(List<ShellDescriptor> globalAssetIds) {
		shellDescriptorDbStore.store(globalAssetIds);
	}

	@Override
	public void deleteAll() {
		shellDescriptorDbStore.deleteAll();
	}

	@Override
	public List<ShellDescriptor> findByBpn(String bpn) {
		return registryService.findAssetsByBpn(bpn);
	}
}
