package com.example.mockitoreproducibledemo;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.retry.RetryException;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.test.context.ContextConfiguration;

import java.net.URI;
import java.net.http.HttpRequest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@RestClientTest
@WireMockTest(httpPort = 8022)
@ContextConfiguration(classes = { HttpConfiguration.class, DemoHttpClientImpl.class })
class DemoHttpClientImplITest {
    private static final String SAMPLE_API_URL = "http://localhost:8022/demo-api";
    private static final String JSON = "{}";

    @Autowired
    private DemoHttpClientImpl classToTest;

    @SpyBean
    private RetryTemplate retryTemplate;

    @Test
    void shouldThrowDemoExceptionWhenRetryExceptionHappensOnSendRequestWithRetry() throws DemoException {
        var httpRequest = HttpRequest.newBuilder()
                .header("Content-Type", "application/json; charset=UTF-8")
                .uri(URI.create(SAMPLE_API_URL))
                .POST(HttpRequest.BodyPublishers.ofString(JSON))
                .build();

        // Works when using mockito-inline
        doThrow(RetryException.class)
                .when(retryTemplate)
                .execute(any());

        // The following works - we are only using a single argument for this overloaded method:
//        doThrow(RetryException.class)
//                .when(retryTemplate)
//                .execute(any(), any(), any());

        assertThrows(DemoException.class, () -> classToTest.sendRequestWithRetry(httpRequest));
    }
}