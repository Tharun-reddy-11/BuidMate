package com.buildmate.config;

import com.buildmate.security.JwtFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.*;
import java.util.List;

@Configuration @EnableMethodSecurity
public class SecurityConfig {
  @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
  @Bean CorsConfigurationSource corsConfigurationSource(@Value("${app.frontend-urls}") String frontends){var c=new CorsConfiguration();var origins=java.util.Arrays.stream(frontends.split(",")).map(String::trim).filter(value->!value.isBlank()).distinct().toList();c.setAllowedOrigins(origins);c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));c.setAllowedHeaders(List.of("*"));c.setExposedHeaders(List.of("Authorization"));c.setAllowCredentials(false);c.setMaxAge(3600L);var s=new UrlBasedCorsConfigurationSource();s.registerCorsConfiguration("/**",c);return s;}
  @Bean SecurityFilterChain chain(HttpSecurity http,JwtFilter jwt)throws Exception{return http.csrf(c->c.disable()).cors(c->{}).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).authorizeHttpRequests(a->a.requestMatchers("/api/auth/login","/api/templates/**","/api/health").permitAll().requestMatchers(HttpMethod.POST,"/api/requests").permitAll().requestMatchers("/api/admin/**").hasRole("ADMIN").anyRequest().authenticated()).addFilterBefore(jwt,UsernamePasswordAuthenticationFilter.class).build();}
}
