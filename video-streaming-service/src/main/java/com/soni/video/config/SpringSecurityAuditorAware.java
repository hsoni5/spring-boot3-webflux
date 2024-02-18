package com.soni.video.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Configuration
@EnableR2dbcAuditing
public class SpringSecurityAuditorAware implements ReactiveAuditorAware<String> {

    @Override
    public Mono<String> getCurrentAuditor() {
        return ReactiveSecurityContextHolder.getContext() //
                .map(SecurityContext::getAuthentication) //
                .filter(Authentication::isAuthenticated) //
                .map(Authentication::getName) //
                .switchIfEmpty(Mono.just("system"));
    }
}