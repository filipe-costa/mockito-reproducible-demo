package com.example.mockitoreproducibledemo;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

interface DemoHttpClient {
    HttpResponse<String> sendRequestWithRetry(HttpRequest request) throws DemoException;
}
