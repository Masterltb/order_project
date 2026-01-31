package com.restaurant.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA configuration to enable auditing.
 * This enables @CreatedDate and @LastModifiedDate annotations.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditConfig {
}
