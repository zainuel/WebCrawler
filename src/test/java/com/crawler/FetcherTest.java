package com.crawler;

import com.crawler.fetcher.Fetcher;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;


class FetcherTest {


    Document mockedDocument = Mockito.mock(Document.class);
    Connection connection = Mockito.mock(Connection.class);

    @Test
    public void shouldReturnDocumentForValidUrl() throws IOException {
        try (MockedStatic<Jsoup> jsoupMockedStatic = Mockito.mockStatic(Jsoup.class)) {
            final String validUrl = "http://website.com";
            jsoupMockedStatic.when(() -> Jsoup.connect(validUrl)).thenReturn(connection);
            Mockito.when(connection.get()).thenReturn(mockedDocument);

            Document document = Assertions.assertDoesNotThrow(() -> Fetcher.fetch(validUrl));

            Assertions.assertEquals(mockedDocument, document);
        }
    }

    @Test
    public void shouldThrowExceptionWhenFetchingInvalidUrl() throws IOException {
        try (MockedStatic<Jsoup> jsoupMockedStatic = Mockito.mockStatic(Jsoup.class)) {
            final String invalidUrl = "hp.web.cm";
            jsoupMockedStatic.when(() -> Jsoup.connect(invalidUrl)).thenReturn(connection);
            Mockito.when(connection.get()).thenThrow(new IOException());

            Assertions.assertThrows(IOException.class, () -> Fetcher.fetch(invalidUrl));
        }
    }

}