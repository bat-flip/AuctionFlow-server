package org.example.auctionflowserver.config;

import org.example.auctionflowserver.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf((csrf) -> csrf.disable());

        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/", "/login**", "/oauth2/**", "/error").permitAll()
                                .requestMatchers("/items/**","/api/**").authenticated()
                                .anyRequest().authenticated()
                )
                .oauth2Login(oauth2Login ->
                        oauth2Login
                                .userInfoEndpoint(userInfoEndpoint ->
                                        userInfoEndpoint.userService(customOAuth2UserService)
                                )
                                .defaultSuccessUrl("/oauth2/loginSuccess", true)
                                .failureUrl("/oauth2/loginFailure")
                )
                .logout(logout ->
                        logout
                                .logoutUrl("/oauth2/logout")
                                .logoutSuccessUrl("/")
                                .invalidateHttpSession(true) // 세션 무효화
                                .deleteCookies("JSESSIONID") // JSESSIONID 쿠키 삭제
                );
        return http.build();
    }
}