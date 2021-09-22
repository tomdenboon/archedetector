package com.rug.archedetector.util;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

public class UriBuilder {
	/**
	 * Builds a URI which contains the given set of query parameters.
	 * @param baseUri The base URI, excluding any query parameters.
	 * @param params The set of parameters.
	 * @return A URI containing the base uri path, and query parameters.
	 */
	public static URI buildParams(String baseUri, Map<String, Object> params) {
		final String queryString = params.entrySet().stream()
				.map(entry -> entry.getKey() + "=" + URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8))
				.collect(Collectors.joining("&"));
		return URI.create(baseUri + (queryString.isBlank() ? "" : "?" + queryString));
	}
}
