package net.catenax.traceability.config.properties;

import java.time.Duration;

public class CacheProperties {

	private final String name;
	private final int maximumSize;
	private final Duration expireAfterWrite;

	public CacheProperties(String name, int maximumSize, Duration expireAfterWrite) {
		this.name = name;
		this.maximumSize = maximumSize;
		this.expireAfterWrite = expireAfterWrite;
	}

	public String getName() {
		return name;
	}

	public int getMaximumSize() {
		return maximumSize;
	}

	public Duration getExpireAfterWrite() {
		return expireAfterWrite;
	}
}
