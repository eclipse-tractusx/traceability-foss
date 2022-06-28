package net.catenax.traceability.assets;

import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryAssetsRepository implements AssetRepository {

	private Map<String, Asset> assets = new HashMap<>();

	@PostConstruct
	public void initializeRepository() {
		assets = AssetsReader.readAssets();
	}

	@Override
	public Asset getAssetById(String assetId) {
		return assets.get(assetId);
	}

	@Override
	public Asset getAssetByChildId(String assetId, String childId) {
		return assets.get(assetId).childDescriptions().stream()
			.filter(childDescription -> childDescription.id().equals(childId))
			.map(childDescription -> assets.get(childDescription.id()))
			.findFirst()
			.orElse(null);
	}

	@Override
	public List<Asset> getAssets() {
		return new ArrayList<>(assets.values());
	}

	@Override
	public PageResult<Asset> getAssets(Pageable pageable) {
		PagedListHolder<Asset> pageListHolder = new PagedListHolder<>(new ArrayList<>(assets.values()));
		Sort sort = pageable.getSortOr(Sort.unsorted());

		if (sort.isSorted()) {
			sort.stream().findFirst().ifPresent(order -> {
				pageListHolder.setSort(new MutableSortDefinition(order.getProperty(), true, order.isAscending()));
				pageListHolder.resort();
			});
		}

		pageListHolder.setPage(pageable.getPageNumber());
		pageListHolder.setPageSize(pageable.getPageSize());

		return new PageResult<>(pageListHolder);
	}
}
