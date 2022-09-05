package net.catenax.traceability.assets.infrastructure.adapters.feign.irs.model;

import java.util.List;

record AssemblyPartRelationship(
	String catenaXId,
	List<ChildPart> childParts
) {}

record ChildPart(
	String childCatenaXId
) {}
