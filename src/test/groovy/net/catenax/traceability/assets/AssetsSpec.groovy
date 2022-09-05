package net.catenax.traceability.assets

import net.catenax.traceability.assets.application.AssetFacade
import net.catenax.traceability.assets.application.RegistryFacade
import net.catenax.traceability.assets.domain.ports.AssetRepository
import net.catenax.traceability.assets.domain.ports.IrsRepository
import net.catenax.traceability.assets.domain.ports.ShellDescriptorStore
import net.catenax.traceability.assets.domain.service.AssetService
import net.catenax.traceability.assets.domain.service.ShellDescriptiorsService
import spock.lang.Specification

abstract class AssetsSpec extends Specification {

	AssetFacade assetFacade
	RegistryFacade registryFacade

	AssetRepository assetRepository
	IrsRepository irsRepository
	ShellDescriptorStore shellDescriptorStore

	def setup() {
		assetRepository = Mock(AssetRepository)
		irsRepository = Mock(IrsRepository)
		def assetService = new AssetService(assetRepository, irsRepository)
		assetFacade = new AssetFacade(assetService)

		shellDescriptorStore = Mock(ShellDescriptorStore)
		registryFacade = new RegistryFacade(new ShellDescriptiorsService(shellDescriptorStore, assetService))
	}
}
