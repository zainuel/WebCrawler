package com.crawler.utils;

import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

@Slf4j
public class UrlUtil {

    private UrlUtil() {
    }

    public static boolean isSameHost(URL firstUrl, URL secondUrl) {

        String firstUrlHostName = getHostName(firstUrl);
        String secondUrlHostName = getHostName(secondUrl);

        return firstUrlHostName.equals(secondUrlHostName);
    }

    private static String getHostName(URL url) {
        /*
         * Delete the trailing www. if exists and return host name
         */
        return url.getHost().startsWith("www.") ? url.getHost().substring(4) : url.getHost();
    }

    public static boolean isValidURL(String url) {
        try {
            URL url1 = new URL(url);
            return true;
        } catch (MalformedURLException e) {
            log.error("URL {} is invalid", url);
        }
        return false;
    }

    public static Optional<URL> toURL(String url) {
        try {
            return Optional.of(new URL(url));
        } catch (MalformedURLException e) {
            log.error("URL {} is invalid", url);
        }
        return Optional.empty();
    }
}
