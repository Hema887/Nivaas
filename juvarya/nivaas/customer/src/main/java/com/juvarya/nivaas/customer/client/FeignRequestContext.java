package com.juvarya.nivaas.customer.client;

import org.springframework.core.NamedThreadLocal;

public class FeignRequestContext {
    private static final ThreadLocal<String> authorizationHeader = new NamedThreadLocal<>("Authorization Header");

    public static void setAuthorizationHeader(String header) {
        authorizationHeader.set(header);
    }

    public static String getAuthorizationHeader() {
        return authorizationHeader.get();
    }

    public static void clearAuthorizationHeader() {
        authorizationHeader.remove();
    }
}
