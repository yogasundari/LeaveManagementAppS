package com.saveetha.LeaveManagement.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http
	        .csrf(csrf -> csrf.disable())  
	        .cors(cors -> cors.disable())  
	        .authorizeHttpRequests(auth -> auth.requestMatchers("/auth/**").permitAll()
					.requestMatchers(HttpMethod.GET, "/api/departments/**").permitAll()
					.requestMatchers(HttpMethod.POST, "/api/departments/**").permitAll()
					.requestMatchers(HttpMethod.POST, "/api/departments/create").hasAuthority("ADMIN") // Only ADMIN can create
					.requestMatchers(HttpMethod.PUT, "/api/departments/update/**").hasAuthority("ADMIN") // Only ADMIN can update
					.requestMatchers(HttpMethod.DELETE, "/api/departments/delete/**").hasAuthority("ADMIN") // Only ADMIN can delete
					.requestMatchers(HttpMethod.POST, "/api/leave-types/**").permitAll() // Allow all GET requests
					.requestMatchers(HttpMethod.GET, "/api/leave-types/**").permitAll() // Allow all GET requests
					.requestMatchers(HttpMethod.POST, "/api/leave-types/create").hasAuthority("ADMIN") // Only ADMIN can create
					.requestMatchers(HttpMethod.PUT, "/api/leave-types/**").hasAuthority("ADMIN") // Only ADMIN can update
					.requestMatchers(HttpMethod.DELETE, "/api/leave-types/**").hasAuthority("ADMIN") // Only ADMIN can delete
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
