package com.aurasage.document.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Hooks;

@Configuration
@ConditionalOnProperty(name = "aurasage.observability.enabled", havingValue = "true", matchIfMissing = false)
public class ObservabilityConfig {

    @PostConstruct
    public void setup() {
        // Ensure Reactor automatically restores MDC from context
        Hooks.enableAutomaticContextPropagation();
    }
    
    /**
     * Common @Observed annotation support
     */
    @Bean
    public ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
        return new ObservedAspect(observationRegistry);
    }


}
