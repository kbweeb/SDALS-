package com.cardsystem.smartdoor.security;

import com.cardsystem.smartdoor.dto.Dtos.LoginResponse;
import com.cardsystem.smartdoor.model.User;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/** Issues signed JWTs; validation is done by Spring Security's resource-server filter. */
@Service
public class JwtService {

    public static final String ROLES_CLAIM = "roles";

    private final JwtEncoder encoder;
    private final JwtProperties props;

    public JwtService(JwtEncoder encoder, JwtProperties props) {
        this.encoder = encoder;
        this.props = props;
    }

    public LoginResponse issue(User user) {
        Instant now = Instant.now();
        Duration ttl = Duration.ofMinutes(props.ttlMinutes());
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("smart-door-backend")
                .subject(user.getUsername())
                .issuedAt(now)
                .expiresAt(now.plus(ttl))
                .claim(ROLES_CLAIM, List.of(user.getRole().name()))
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new LoginResponse(token, ttl.toSeconds());
    }
}
