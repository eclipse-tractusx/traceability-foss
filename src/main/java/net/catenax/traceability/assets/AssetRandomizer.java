package net.catenax.traceability.assets;

import com.github.javafaker.Faker;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AssetRandomizer {

	private static final List<String> BPNS = List.of(
		"BPNL000000000001",
		"BPNL00000003AXS3",
		"BPNL00000003B3NX",
		"BPNL00000003B5MJ"
	);

	public static Map<String, Asset> generateAssets(int count) {
		Faker faker = new Faker();
		Random rand = new Random();
		List<Asset> randomAssets = new ArrayList<>();

		for (int i = 0; i < count; i++) {
			Asset asset = new Asset(
				faker.code().ean13(),
				faker.code().ean8(),
				faker.commerce().productName(),
				faker.code().ean13(),
				BPNS.get(rand.nextInt(BPNS.size())),
				null,
				faker.commerce().productName(),
				faker.code().ean13(),
				faker.date().past(500, TimeUnit.DAYS).toInstant(),
				faker.country().countryCode3().toUpperCase()
			);

			randomAssets.add(asset);
		}

		Random random = new Random();

		return randomAssets.stream().map(asset -> {
				List<Asset.ChildDescriptions> childDescriptions = new ArrayList<>();

				for (int i = 0; i < random.nextInt(5); i++) {
					Asset randomAsset = randomAssets.get(random.nextInt(randomAssets.size()));

					childDescriptions.add(new Asset.ChildDescriptions(randomAsset.id(), randomAsset.idShort()));
				}

				return asset.withChildDescriptions(childDescriptions);
			}).collect(Collectors.toMap(Asset::id, Function.identity()));
	}

}
