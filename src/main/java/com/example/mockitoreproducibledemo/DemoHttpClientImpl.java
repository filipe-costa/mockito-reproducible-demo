package com.example.mockitoreproducibledemo;

import lombok.RequiredArgsConstructor;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryException;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
@RequiredArgsConstructor
public class DemoHttpClientImpl implements DemoHttpClient{
    private static final int MIN_HTTP_STATUS_CODE_RETRYABLE = 500;
    private static final int MAX_HTTP_STATUS_CODE_RETRYABLE = 599;

    private final HttpClient httpClient;
    private final RetryTemplate retryTemplate;

    @Override
    public HttpResponse<String> sendRequestWithRetry(HttpRequest request) throws DemoException {
        try {
            return retryTemplate.execute(sendWithRetryCallback(request));
        } catch (RetryException e) {
            throw new DemoException(e);
        }
    }

    private HttpResponse<String> send(HttpRequest request) throws DemoException {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new DemoException(e);
        }
    }

    private RetryCallback<HttpResponse<String>, DemoException> sendWithRetryCallback(HttpRequest request) {
        return context -> {
            var response = this.send(request);
            if (isRetryable(response)) {
                throw new DemoException(context.getLastThrowable());
            }
            return response;
        };
    }

    private boolean isRetryable(HttpResponse<String> response) {
        return response.statusCode() >= MIN_HTTP_STATUS_CODE_RETRYABLE && response.statusCode() < MAX_HTTP_STATUS_CODE_RETRYABLE;
    }
}
