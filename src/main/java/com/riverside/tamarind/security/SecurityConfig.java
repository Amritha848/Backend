
package com.riverside.tamarind.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.riverside.tamarind.corsconfig.CorsConfigurations;
import com.riverside.tamarind.jwtauthenticationfilter.JwtAuthenticationFilter;
import com.riverside.tamarind.service.UserDetailsServiceInfo;

import lombok.ToString;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@ToString
public class SecurityConfig {

	@Autowired
	@Qualifier("handlerExceptionResolver")
	private HandlerExceptionResolver exceptionResolver;

	@Autowired
	private CorsConfigurations corsConfiguration;

	@Autowired
	private CustomLogoutHandler logoutHandler;

	@Bean
	JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter(exceptionResolver);
	}

	@Bean
	SecurityFilterChain chain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("api/v1/signup", "api/v1/login", "/actuator/**", "api/v6/**",
								"api/v1/sendOtp", "api/v1/verifyOtp", "api/v1/update", "api/v2/sendOtp",
								"api/v2/verifyOtp", "api/v1/roles", "api/v1/getSession", "api/v1/roles/manager","api/v7/**","/swagger-ui/**","/v3/api-docs/**","/api/v6/refresh-token","/forgot-password","/api/v7/reset-password")
						.permitAll().anyRequest().authenticated())
				.cors(c -> c.configurationSource(corsConfiguration))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(authenticationProvider())
				.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
				.logout(l -> l.logoutUrl("/api/v1/logout").addLogoutHandler(logoutHandler).logoutSuccessHandler(
						(request, response, authentication) -> SecurityContextHolder.clearContext()));

		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(userDetailsService());
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		return daoAuthenticationProvider;
	}

	@Bean
	UserDetailsService userDetailsService() {
		return new UserDetailsServiceInfo();
	}

	
}
