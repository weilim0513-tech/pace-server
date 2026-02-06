package com.pace.server.global.security.jwt;

import java.io.Serializable;

public record JwtAuthentication(Long userId, String email) implements Serializable {
}
