package com.juvarya.nivaas.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(
		prePostEnabled = true)
public class WebSecurityConfig {

	@Autowired
	private AuthEntryPointJwt unauthorizedHandler;

	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}

	/*@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}*/

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors().and()
				.csrf().disable()
				.exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
				.antMatchers("/api/access-mgmt/nivaas/auth/**", "/").permitAll()
				.antMatchers("/nivaas/auth/**", "/").permitAll()
				.antMatchers("/api/test/**").permitAll()
				.antMatchers("/jtprofile/admin/**").permitAll()
				.antMatchers("/api/test/user").hasRole("USER")
	            .antMatchers("/api/test/admin").hasRole("USER_ADMIN")
	            .antMatchers("/api/test/apartment_admin").hasRole("APARTMENT_ADMIN")
	            .antMatchers("/api/test/flat_owner").hasRole("FLAT_OWNER")
	            .antMatchers("/api/test/flat_tenant").hasRole("FLAT_TENANT")
	            .antMatchers("/api/test/apartment_helper").hasRole("APARTMENT_HELPER")
				.anyRequest().authenticated();

		http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

}
