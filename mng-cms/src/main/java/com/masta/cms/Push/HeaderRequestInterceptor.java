package com.masta.cms.Push;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class HeaderRequestInterceptor implements ClientHttpRequestInterceptor {
    private final String headerName;
    private final String headerValue;

    public HeaderRequestInterceptor(String headerName, String headerValue) {
        this.headerName = headerName;
        this.headerValue = headerValue;
    }

    public ClientHttpResponse intercept(HttpRequest request, )
}
