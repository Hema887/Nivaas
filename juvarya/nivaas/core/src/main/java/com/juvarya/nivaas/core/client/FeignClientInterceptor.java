package com.juvarya.nivaas.core.client;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;

@Component
public class FeignClientInterceptor implements RequestInterceptor {
	@Override
	public void apply(RequestTemplate template) {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();

		if (requestAttributes != null) {
			HttpServletRequest request = requestAttributes.getRequest();
			String authorizationHeader = request.getHeader("Authorization");
			if (authorizationHeader != null) {
				template.header("Authorization", authorizationHeader);
			}
		} else {
			// Check for custom header for cron jobs or any async flows
			String systemAuthHeader = FeignRequestContext.getAuthorizationHeader();
			if (systemAuthHeader != null) {
				template.header("Authorization", systemAuthHeader);
			}
		}
	}
}
