package com.juvarya.nivaas.access.mgmt.services;

import javax.servlet.http.HttpServletRequest;

import com.juvarya.nivaas.auth.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.juvarya.nivaas.commonservice.dto.LoggedInUser;

@Component("customerState")
public class CustomerState {
	private static final String BEARER = "Bearer ";
	private static final String AUTHORIZATION = "Authorization";

	public static ThreadLocal<String> currentUser = new ThreadLocal<>();
	public static ThreadLocal<Long> currentUserId = new ThreadLocal<>();
	
	@Autowired
	private JwtUtils jwtUtils;

	public LoggedInUser getLoggedInUser(HttpServletRequest request) {
		try {
			String jwt = parseJwt(request);
			if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
				return jwtUtils.getUserFromToken(jwt);
			}
		} catch (Exception ex) {
		}
		return null;
	}

	private String parseJwt(HttpServletRequest request) {
		String headerAuth = request.getHeader(AUTHORIZATION);
		if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(BEARER)) {
			return headerAuth.substring(7, headerAuth.length());
		}
		return null;
	}
}
