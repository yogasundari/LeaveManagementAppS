package com.saveetha.LeaveManagement.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable())
				.cors(cors -> cors.disable())
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/auth/**").permitAll() // Allow authentication routes

						// Department API
						.requestMatchers(HttpMethod.GET, "/api/departments/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/departments/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/departments/create").hasAuthority("ADMIN")
						.requestMatchers(HttpMethod.PUT, "/api/departments/update/**").hasAuthority("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/api/departments/delete/**").hasAuthority("ADMIN")

						// Leave Type API
						.requestMatchers(HttpMethod.GET, "/api/leave-types/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/leave-types/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/leave-types/create").hasAuthority("ADMIN")
						.requestMatchers(HttpMethod.PUT, "/api/leave-types/**").hasAuthority("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/api/leave-types/**").hasAuthority("ADMIN")

						// Approval Flow API
						.requestMatchers(HttpMethod.GET, "/api/approval-flows/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/approval-flows/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/approval-flows/create").hasAuthority("ADMIN")
						.requestMatchers(HttpMethod.PUT, "/api/approval-flows/**").hasAuthority("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/api/approval-flows/**").hasAuthority("ADMIN")

						// Approval Flow Level API
						.requestMatchers(HttpMethod.GET, "/api/approval-flow-levels/**").permitAll() // Allow viewing
						.requestMatchers(HttpMethod.POST, "/api/approval-flow-levels/**").permitAll() // Allow viewing
						.requestMatchers(HttpMethod.POST, "/api/approval-flow-levels/create").hasAuthority("ADMIN") // Only ADMIN can create
						.requestMatchers(HttpMethod.PUT, "/api/approval-flow-levels/**").hasAuthority("ADMIN") // Only ADMIN can update
						.requestMatchers(HttpMethod.DELETE, "/api/approval-flow-levels/**").hasAuthority("ADMIN") // Only ADMIN can delete

						.anyRequest().authenticated()
				)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		return http.build();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}


}
