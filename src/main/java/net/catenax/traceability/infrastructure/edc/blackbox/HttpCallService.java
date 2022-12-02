package net.catenax.traceability.infrastructure.edc.blackbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.MultivaluedMap;
import net.catenax.traceability.infrastructure.edc.blackbox.catalog.Catalog;
import net.catenax.traceability.infrastructure.edc.blackbox.policy.AtomicConstraint;
import net.catenax.traceability.infrastructure.edc.blackbox.policy.LiteralExpression;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

@Component
public class HttpCallService {

	private static final Logger logger = LoggerFactory.getLogger(HttpCallService.class);

	private final OkHttpClient httpClient;
	private final ObjectMapper objectMapper;
	@Value("${edc.catalog}")
	String catalogPath;

	public HttpCallService(OkHttpClient httpClient, ObjectMapper objectMapper) {
		this.httpClient = withIncreasedTimeout(httpClient);
		this.objectMapper = objectMapper;
		objectMapper.registerSubtypes(AtomicConstraint.class, LiteralExpression.class);
	}

	private static OkHttpClient withIncreasedTimeout(OkHttpClient httpClient) {
		return httpClient.newBuilder()
			.connectTimeout(10, TimeUnit.SECONDS)
			.readTimeout(25, TimeUnit.SECONDS)
			.writeTimeout(50, TimeUnit.SECONDS)
			.build();
	}


	public Catalog getCatalogFromProvider(
		String consumerEdcDataManagementUrl,
		String providerConnectorControlPlaneIDSUrl,
		Map<String, String> headers
	) throws IOException {
		var url = consumerEdcDataManagementUrl + catalogPath + providerConnectorControlPlaneIDSUrl;
		var request = new Request.Builder().url(url);
		headers.forEach(request::addHeader);

		return (Catalog) sendRequest(request.build(), Catalog.class);
	}


	public Object sendRequest(Request request, Class<?> responseObject) throws IOException {
		logger.info("Requesting {} {}...", request.method(), request.url());
		try (var response = httpClient.newCall(request).execute()) {
			var body = response.body();

			if (!response.isSuccessful() || body == null) {
				throw new BadRequestException(format("Control plane responded with: %s %s", response.code(), body != null ? body.string() : ""));
			}

			String res = body.string();
			return objectMapper.readValue(res, responseObject);
		} catch (Exception e) {
			throw e;
		}
	}

	public void sendRequest(Request request) throws IOException {
		try (var response = httpClient.newCall(request).execute()) {
			var body = response.body();
			if (!response.isSuccessful() || body == null) {
				throw new BadRequestException(format("Control plane responded with: %s %s", response.code(), body != null ? body.string() : ""));
			}
		} catch (Exception e) {
			throw e;
		}
	}


	public HttpUrl getUrl(String connectorUrl, String subUrl, MultivaluedMap<String, String> parameters) {
		var url = connectorUrl;

		if (subUrl != null && !subUrl.isEmpty()) {
			url = url + "/" + subUrl;
		}

		HttpUrl.Builder httpBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();

		if (parameters == null) {
			return httpBuilder.build();
		}

		for (Map.Entry<String, List<String>> param : parameters.entrySet()) {
			for (String value : param.getValue()) {
				httpBuilder = httpBuilder.addQueryParameter(param.getKey(), value);
			}
		}

		return httpBuilder.build();
	}
}
