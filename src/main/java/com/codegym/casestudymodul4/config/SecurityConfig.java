package com.codegym.casestudymodul4.config;

import com.codegym.casestudymodul4.service.StoreService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  UserDetailsService userDetailsService(StoreService store) {
    return username ->
        store
            .account(username)
            .map(
                a ->
                    User.withUsername(a.getUsername())
                        .password(a.getPasswordHash())
                        .roles(a.getRole())
                        .disabled(!a.isEnabled())
                        .build())
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản"));
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        "/",
                        "/products/**",
                        "/register",
                        "/login",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/h2-console/**")
                    .permitAll()
                    .requestMatchers("/admin/**")
                    .hasRole("ADMIN")
                    .requestMatchers("/cart/**", "/checkout/**", "/profile/**", "/orders/**")
                    .authenticated()
                    .anyRequest()
                    .permitAll())
        .formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/", false).permitAll())
        .logout(logout -> logout.logoutSuccessUrl("/").permitAll())
        .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
        .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
        .build();
  }
}
