package com.exadel.tenderflex.config.security;

import com.exadel.tenderflex.controller.filter.JwtFilter;
import com.exadel.tenderflex.repository.entity.enums.ERolePrivilege;
import com.exadel.tenderflex.repository.entity.enums.EUserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserDetailsService jwtUserDetailsService;
    private final JwtFilter jwtFilter;

    @Autowired
    public SecurityConfig(UserDetailsService jwtUserDetailsService, JwtFilter jwtFilter) {
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.jwtFilter = jwtFilter;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // we don't need CSRF because our token is invulnerable
        http.cors(Customizer.withDefaults())
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/v1/users/registration", "/api/v1/users/registration/**",
                        "/api/v1/users/login").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/file/**").hasAuthority(ERolePrivilege.CAN_READ_TENDER.name())
                .antMatchers(HttpMethod.GET, "/api/v1/tender/all").hasAuthority(ERolePrivilege.CAN_CREATE_OFFER.name())
                .antMatchers(HttpMethod.GET, "/api/v1/tender", "/api/v1/tender/**")
                .hasAuthority(ERolePrivilege.CAN_READ_TENDER.name())
                .antMatchers(HttpMethod.POST, "/api/v1/tender").hasAuthority(ERolePrivilege.CAN_CREATE_AND_PUBLISH_TENDER.name())
                .antMatchers(HttpMethod.PUT, "/api/v1/tender/**").hasAuthority(ERolePrivilege.CAN_CREATE_AND_PUBLISH_TENDER.name())
                .antMatchers(HttpMethod.GET, "/api/v1/offer").hasAuthority(ERolePrivilege.CAN_READ_OFFER.name())
                .antMatchers(HttpMethod.POST,"/api/v1/offer").hasAuthority(ERolePrivilege.CAN_CREATE_OFFER.name())
                .antMatchers(HttpMethod.PUT,"/api/v1/offer/**").hasAuthority(ERolePrivilege.CAN_CREATE_OFFER.name())
                .antMatchers("/api/v1/admin", "/api/v1/admin/**").hasRole(EUserRole.ADMIN.name())
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler())
                .authenticationEntryPoint(authenticationEntryPoint())
                .and()
                // don't create session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    RestAuthenticationEntryPoint authenticationEntryPoint() {
        return new RestAuthenticationEntryPoint();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Access-Control-Allow-Origin", "Content-Type",
                "X-Requested-With", "accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        configuration.setAllowCredentials(false); // we're using jwt
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
