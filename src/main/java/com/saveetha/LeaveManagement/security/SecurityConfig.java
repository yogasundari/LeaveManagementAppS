package com.saveetha.LeaveManagement.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/auth/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/departments/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/departments/**").permitAll()
						.requestMatchers(HttpMethod.PUT, "/api/departments/update/**").hasAuthority("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/api/departments/delete/**").hasAuthority("ADMIN")

						.requestMatchers(HttpMethod.POST, "/api/leave-types/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/leave-types/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/leave-types/create").hasAuthority("ADMIN")
						.requestMatchers(HttpMethod.PUT, "/api/leave-types/**").hasAuthority("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/api/leave-types/**").hasAuthority("ADMIN")

						.requestMatchers(HttpMethod.POST, "/api/approval-flows/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/approval-flows/create").hasAuthority("ADMIN")
						.requestMatchers(HttpMethod.PUT, "/api/approval-flows/**").hasAuthority("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/api/approval-flows/**").hasAuthority("ADMIN")

						.requestMatchers(HttpMethod.POST, "/api/approval-flow-levels/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/approval-flow-levels/create").hasAuthority("ADMIN")
						.requestMatchers(HttpMethod.PUT, "/api/approval-flow-levels/**").hasAuthority("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/api/approval-flow-levels/**").hasAuthority("ADMIN")

						.requestMatchers(HttpMethod.PUT, "/api/employees/**").permitAll()
						.requestMatchers(HttpMethod.PUT, "/api/employees/**").hasAnyAuthority("EMPLOYEE", "ADMIN")
						.requestMatchers(HttpMethod.POST, "/api/leave").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/leave/apply").hasAnyAuthority("EMPLOYEE", "ADMIN")
						.requestMatchers(HttpMethod.POST, "/api/leave-request/**").permitAll()
						.requestMatchers(HttpMethod.PATCH, "/api/leave-request/**").permitAll()

						.requestMatchers(HttpMethod.POST, "/api/leave-alteration/**").permitAll()
						.requestMatchers(HttpMethod.PATCH, "/api/leave-alteration/**").permitAll()
						.requestMatchers(HttpMethod.PATCH, "/api/leave-approval/**").permitAll()
						.anyRequest().authenticated()
				)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
