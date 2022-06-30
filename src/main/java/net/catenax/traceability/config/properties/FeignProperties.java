package net.catenax.traceability.config.properties;

public class FeignProperties {

	private final Long connectionTimeoutMillis;
	private final Long readTimeoutMillis;
	private final int maxIdleConnections;
	private final Long keepAliveDurationMinutes;

	public FeignProperties(Long connectionTimeoutMillis,
						   Long readTimeoutMillis,
						   int maxIdleConnections,
						   Long keepAliveDurationMinutes) {
		this.connectionTimeoutMillis = connectionTimeoutMillis;
		this.readTimeoutMillis = readTimeoutMillis;
		this.maxIdleConnections = maxIdleConnections;
		this.keepAliveDurationMinutes = keepAliveDurationMinutes;
	}

	public Long getConnectionTimeoutMillis() {
		return connectionTimeoutMillis;
	}

	public Long getReadTimeoutMillis() {
		return readTimeoutMillis;
	}

	public int getMaxIdleConnections() {
		return maxIdleConnections;
	}

	public Long getKeepAliveDurationMinutes() {
		return keepAliveDurationMinutes;
	}
}
