package com.app.BugBee.security;

import com.app.BugBee.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public ReactiveUserDetailsService userDetailsService(UserRepository users) {
        return (username) -> users.findByUsernameOrEmail(username, username)
                .map(u -> User.withUsername(String.valueOf(u.getUserId()))
                        .password(u.getPassword())
                        .authorities(u.getRoles())
                        .accountExpired(false)
                        .credentialsExpired(false)
                        .disabled(false)
                        .accountLocked(false)
                        .build()
                );

    }

//    @Bean
//    public CorsWebFilter corsWebFilter() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList("http://localhost:80"));
//        configuration.setAllowCredentials(true);
//        configuration.addAllowedHeader("*");
//        configuration.addAllowedMethod("*");
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return new CorsWebFilter(source);
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, JwtTokenProvider tokenProvider) {
        return http
//                .cors(Customizer.withDefaults())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth ->
                                auth.pathMatchers("/api/auth/**").permitAll()
                                        .pathMatchers("/api/users/**", "/api/posts/**", "/api/search").authenticated()
                                        .pathMatchers("/api/admin/**").hasRole("ADMIN")
                )
                .addFilterAt(new JwtTokenAuthenticationFilter(tokenProvider), SecurityWebFiltersOrder.HTTP_BASIC)
//                .formLogin(Customizer.withDefaults()).build();
                .formLogin(formLoginSpec -> formLoginSpec.loginPage("/"))
                .build();
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authenticationManager.setPasswordEncoder(passwordEncoder);
        return authenticationManager;
    }

}
