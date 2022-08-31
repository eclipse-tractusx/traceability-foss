package net.catenax.traceability.assets

import net.catenax.traceability.assets.application.AssetFacade
import net.catenax.traceability.assets.application.RegistryFacade
import net.catenax.traceability.assets.domain.ports.AssetMockDataRepository
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
	AssetMockDataRepository assetMockDataRepository
	IrsRepository irsRepository
	ShellDescriptorStore shellDescriptorStore

	def setup() {
		assetRepository = Mock(AssetRepository)
		assetMockDataRepository = Mock(AssetMockDataRepository)
		irsRepository = Mock(IrsRepository)
		def assetService = new AssetService(assetRepository, assetMockDataRepository, irsRepository)
		assetFacade = new AssetFacade(assetService)

		shellDescriptorStore = Mock(ShellDescriptorStore)
		registryFacade = new RegistryFacade(new ShellDescriptiorsService(shellDescriptorStore, assetService))
	}
}
