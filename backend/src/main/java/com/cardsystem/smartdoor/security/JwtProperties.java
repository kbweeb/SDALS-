package com.cardsystem.smartdoor.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @param secret     HMAC-SHA256 signing key, at least 32 characters
 * @param ttlMinutes how long an issued token stays valid
 */
@ConfigurationProperties(prefix = "security.jwt")
public record JwtProperties(String secret, long ttlMinutes) { }
