package org.eclipse.tractusx.traceability.common.request;

public class UrlUtils {
    /**
     * Appends a specified path to a base URL, ensuring proper formatting.
     * If the base URL already ends with the path, it is returned unchanged.
     * If both the base URL ends with a slash and the path starts with a slash,
     * one of the slashes is removed to avoid duplication.
     *
     * @param baseUrl the base URL to which the path will be appended.
     *                It should not end with the path unless intended to be returned as is.
     * @param path    the path to append to the base URL.
     *                It may start with a slash, which will be handled appropriately.
     * @return the combined URL with the path appended, formatted correctly.
     */
    public static String appendSuffix(final String baseUrl, final String path) {
        if (baseUrl == null) {
            return path;
        }
        if (path == null) {
            return baseUrl;
        }

        String addressWithSuffix;
        if (baseUrl.endsWith(path)) {
            addressWithSuffix = baseUrl;
        } else if (baseUrl.endsWith("/") && path.startsWith("/")) {
            addressWithSuffix = baseUrl.substring(0, baseUrl.length() - 1) + path;
        } else {
            addressWithSuffix = baseUrl + path;
        }
        return addressWithSuffix;
    }
}
