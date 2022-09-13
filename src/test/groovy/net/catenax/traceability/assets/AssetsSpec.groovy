package net.catenax.traceability.assets

import net.catenax.traceability.assets.application.AssetFacade
import net.catenax.traceability.assets.application.RegistryFacade
import net.catenax.traceability.assets.domain.ports.AssetRepository
import net.catenax.traceability.assets.domain.ports.IrsRepository
import net.catenax.traceability.assets.domain.ports.ShellDescriptorRepository
import net.catenax.traceability.assets.domain.service.AssetService
import net.catenax.traceability.assets.domain.service.ShellDescriptorsService
import net.catenax.traceability.assets.infrastructure.adapters.openapi.registry.RegistryService
import spock.lang.Specification

abstract class AssetsSpec extends Specification {

	AssetFacade assetFacade
	RegistryFacade registryFacade
	RegistryService registryService
	AssetRepository assetRepository
	IrsRepository irsRepository
	ShellDescriptorRepository shellDescriptorsRepository

	def setup() {
		registryService = Mock(RegistryService)
		assetRepository = Mock(AssetRepository)
		irsRepository = Mock(IrsRepository)
		def assetService = new AssetService(assetRepository, irsRepository)
		assetFacade = new AssetFacade(assetService)

		shellDescriptorsRepository = Mock(ShellDescriptorRepository)
		registryFacade = new RegistryFacade(new ShellDescriptorsService(shellDescriptorsRepository), registryService, assetService)
	}
}
