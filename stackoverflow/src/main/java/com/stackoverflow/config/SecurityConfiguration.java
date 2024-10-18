package com.stackoverflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String BASE_USER = "/api/v1/user";
    private static final String BASE_PROFILE = "/api/v1/profile";
    private static final String BASE_ROLE = "/api/v1/role";

    private static final String USER = "USER";
    private static final String ADMIN = "ADMIN";
    private static final String PROFILE = "PROFILE";
    private static final String ROLE = "ROLE";

    private static final String[] SWAGGER_WHITELIST = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/swagger-resources"
    };

    public SecurityConfiguration(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(SWAGGER_WHITELIST).permitAll()
                        .requestMatchers("/api/v1/auth/signup").permitAll()
                        .requestMatchers("/api/v1/auth/login").permitAll()

                        .requestMatchers("/api/v1/user/passwordChange").permitAll()
                        .requestMatchers("api/v1/user/password-recovery").permitAll()
                        .requestMatchers("/api/v1/user/password-reset").permitAll()

                        .requestMatchers("/api/assets/*").permitAll()

                        .requestMatchers(HttpMethod.GET, BASE_USER).hasAnyRole(ADMIN, USER)
                        .requestMatchers(HttpMethod.GET, BASE_USER + "/*").hasAnyRole(ADMIN, USER)
                        .requestMatchers(HttpMethod.GET, BASE_USER + "/findByUsername/*").hasAnyRole(ADMIN, USER)
                        .requestMatchers(HttpMethod.GET, BASE_USER + "/findByEmail/*").hasAnyRole(ADMIN, USER)
                        .requestMatchers(HttpMethod.PATCH, BASE_USER + "/changeStatus/*").hasAnyRole(ADMIN, USER)
                        .requestMatchers(HttpMethod.PATCH, BASE_USER + "/updateProfile/*").hasAnyRole(ADMIN)
                        .requestMatchers(HttpMethod.PATCH, BASE_USER + "/changePhotoProfile/*").hasAnyRole(ADMIN, USER)
                        .requestMatchers(HttpMethod.PUT, BASE_USER + "/*").hasAnyRole(ADMIN, USER)
                        .requestMatchers(HttpMethod.DELETE, BASE_USER + "/*").hasAnyRole(ADMIN)

                        .requestMatchers(HttpMethod.POST, BASE_PROFILE).hasRole(PROFILE)
                        .requestMatchers(HttpMethod.GET, BASE_PROFILE).hasRole(PROFILE)
                        .requestMatchers(HttpMethod.GET, BASE_PROFILE + "/*").hasRole(PROFILE)
                        .requestMatchers(HttpMethod.PATCH, BASE_PROFILE + "/*").hasRole(PROFILE)
                        .requestMatchers(HttpMethod.PUT, BASE_PROFILE + "/*").hasRole(PROFILE)
                        .requestMatchers(HttpMethod.DELETE, BASE_PROFILE + "/*").hasRole(PROFILE)

                        .requestMatchers(HttpMethod.POST, BASE_ROLE).hasRole(ROLE)
                        .requestMatchers(HttpMethod.GET, BASE_ROLE).hasRole(ROLE)
                        .requestMatchers(HttpMethod.GET, BASE_ROLE + "/*").hasRole(ROLE)
                        .requestMatchers(HttpMethod.PATCH, BASE_ROLE + "/*").hasRole(ROLE)
                        .requestMatchers(HttpMethod.PUT, BASE_ROLE + "/*").hasRole(ROLE)
                        .requestMatchers(HttpMethod.DELETE, BASE_ROLE + "/*").hasRole(ROLE)

                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}