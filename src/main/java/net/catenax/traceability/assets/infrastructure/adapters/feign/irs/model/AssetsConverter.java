/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package net.catenax.traceability.assets.infrastructure.adapters.feign.irs.model;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.catenax.traceability.assets.domain.model.Asset;
import net.catenax.traceability.assets.domain.model.Asset.ChildDescriptions;
import net.catenax.traceability.assets.domain.model.QualityType;
import net.catenax.traceability.assets.domain.model.ShellDescriptor;
import net.catenax.traceability.assets.domain.ports.BpnRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AssetsConverter {

	public static final String EMPTY_TEXT = "--";

	private BpnRepository bpnRepository;

	private final ObjectMapper mapper = new ObjectMapper()
		.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, true)
		.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	public AssetsConverter(BpnRepository bpnRepository) {
		this.bpnRepository = bpnRepository;
	}

	public List<Asset> readAndConvertAssets() {
		try {
			InputStream file = AssetsConverter.class.getResourceAsStream("/data/irs_assets_v2.json");
			JobResponse response = mapper.readValue(file, JobResponse.class);

			return convertAssets(response);
		} catch (IOException e) {
			return Collections.emptyList();
		}
	}

	public List<Asset> convertAssets(List<ShellDescriptor> items) {
		return items.stream()
			.map(this::toAsset)
			.toList();
	}

	public List<Asset> convertAssets(JobResponse response)  {
		List<SerialPartTypization> parts = response.serialPartTypizations();
		Map<String, AssemblyPartRelationship> relationships = response.assemblyPartRelationships();
		Map<String, String> shortIds = response.shells().stream()
			.collect(Collectors.toMap(Shell::identification, Shell::idShort));
		Set<String> supplierParts = relationships.values().stream()
			.flatMap(a -> a.childParts().stream())
			.map(ChildPart::childCatenaXId)
			.collect(Collectors.toSet());


			return parts.stream()
				.map(part -> new Asset(
					part.catenaXId(),
					defaultValue(shortIds.get(part.catenaXId())),
					defaultValue(part.partTypeInformation().nameAtManufacturer()),
					defaultValue(part.partTypeInformation().manufacturerPartID()),
					partInstanceId(part),
					manufacturerId(part),
					batchId(part),
					manufacturerName(part),
					defaultValue(part.partTypeInformation().nameAtCustomer()),
					defaultValue(part.partTypeInformation().customerPartId()),
					manufacturingDate(part),
					manufacturingCountry(part),
					supplierParts.contains(part.catenaXId()),
					getChildParts(relationships, shortIds, part.catenaXId()),
					false,
					QualityType.OK
				)).toList();
	}

	private Asset toAsset(ShellDescriptor shellDescriptor) {
		String manufacturerId = shellDescriptor.manufacturerId();
		String manufacturerName = bpnRepository.findManufacturerName(manufacturerId).orElse(EMPTY_TEXT);
		return new Asset(
			shellDescriptor.globalAssetId(),
			shellDescriptor.idShort(),
			shellDescriptor.idShort(),
			defaultValue(shellDescriptor.manufacturerPartId()),
			defaultValue(shellDescriptor.partInstanceId()),
			defaultValue(manufacturerId),
			defaultValue(shellDescriptor.batchId()),
			manufacturerName,
			shellDescriptor.idShort(),
			shellDescriptor.manufacturerPartId(),
			null,
			EMPTY_TEXT,
			false,
			Collections.emptyList(),
			false,
			QualityType.OK
		);
	}

	private String manufacturerName(SerialPartTypization part) {
		String manufacturerId = manufacturerId(part);

		return bpnRepository.findManufacturerName(manufacturerId).orElse(EMPTY_TEXT);
	}

	private String manufacturerId(SerialPartTypization part) {
		return part.getLocalId(LocalIdType.MANUFACTURER_ID)
			.orElse(EMPTY_TEXT);
	}

	private String batchId(SerialPartTypization part) {
		return part.getLocalId(LocalIdType.BATCH_ID)
			.orElse(EMPTY_TEXT);
	}

	private String partInstanceId(SerialPartTypization part) {
		return part.getLocalId(LocalIdType.PART_INSTANCE_ID)
			.orElse(EMPTY_TEXT);
	}

	private String manufacturingCountry(SerialPartTypization part) {
		if (part.manufacturingInformation() == null) {
			return EMPTY_TEXT;
		}
		return part.manufacturingInformation().country();
	}

	private Instant manufacturingDate(SerialPartTypization part) {
		if (part.manufacturingInformation() == null) {
			return null;
		}

		return Optional.ofNullable(part.manufacturingInformation().date())
			.map(Date::toInstant)
			.orElse(null);
	}

	private String defaultValue(String value) {
		if (!StringUtils.hasText(value)) {
			return EMPTY_TEXT;
		}
		return value;
	}

	private List<ChildDescriptions> getChildParts(Map<String, AssemblyPartRelationship> relationships, Map<String, String> shortIds, String catenaXId) {
		return Optional.ofNullable(relationships.get(catenaXId))
			.map(assemblyPartRelationship -> assemblyPartRelationship.childParts().stream()
				.map(child -> new ChildDescriptions(child.childCatenaXId(), shortIds.get(child.childCatenaXId())))
				.toList()
			).orElse(Collections.emptyList());
	}
}
