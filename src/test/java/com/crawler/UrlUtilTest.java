package com.crawler;

import com.crawler.utils.UrlUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

class UrlUtilTest {

    @Test
    void isSameHost() throws MalformedURLException {
        URL url_with_www_prefix = new URL("http://www.google.com");
        URL url_without_www_prefix = new URL("http://google.com");

        Assertions.assertTrue(UrlUtil.isSameHost(url_with_www_prefix, url_without_www_prefix));
        Assertions.assertTrue(UrlUtil.isSameHost(url_with_www_prefix, url_with_www_prefix));
        Assertions.assertTrue(UrlUtil.isSameHost(url_without_www_prefix, url_without_www_prefix));
    }

}