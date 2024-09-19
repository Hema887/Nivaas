package com.juvarya.nivaas.core.client;

import org.springframework.core.NamedThreadLocal;

public class FeignRequestContext {
    private static final ThreadLocal<String> authorizationHeader = new NamedThreadLocal<>("Authorization Header");

    public static String getAuthorizationHeader() {
        return authorizationHeader.get();
    }
}
