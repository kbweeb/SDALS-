package com.cardsystem.smartdoor.dto;

import com.cardsystem.smartdoor.model.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDateTime;

/** Request/response shapes exposed by the REST API. Entities never leave the service layer. */
public final class Dtos {

    private Dtos() { }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) { }

    public record LoginResponse(String token, long expiresInSeconds) { }

    public record UserRequest(
            @NotBlank @Size(max = 50) String firstName,
            @NotBlank @Size(max = 50) String lastName,
            @NotNull Role role) { }

    public record UserDto(Integer id, String firstName, String lastName, Role role, boolean active, LocalDateTime createdAt) {
        public static UserDto from(User u) {
            return new UserDto(u.getId(), u.getFirstName(), u.getLastName(), u.getRole(), u.isActive(), u.getCreatedAt());
        }
    }

    /** Records a slot that was enrolled directly on the physical lock. */
    public record CredentialRequest(
            @NotNull Integer userId,
            @NotNull CredentialType type,
            @NotNull @PositiveOrZero Integer index,
            String cardUid) { }

    public record CredentialDto(Integer id, Integer userId, CredentialType type, Integer index, String cardUid) {
        public static CredentialDto from(Credential c) {
            return new CredentialDto(c.getId(), c.getUser().getId(), c.getType(), c.getIndex(), c.getCardUid());
        }
    }

    public record AccessLogDto(Integer id, Integer userId, String userName, AccessMethod method, AccessStatus status, LocalDateTime loggedAt) {
        public static AccessLogDto from(AccessLog log) {
            User u = log.getUser();
            return new AccessLogDto(log.getId(),
                    u == null ? null : u.getId(),
                    u == null ? null : u.getFirstName() + " " + u.getLastName(),
                    log.getMethod(), log.getStatus(), log.getLoggedAt());
        }
    }

    /** Latest lock state reported by Zigbee2MQTT. Fields stay null until the lock reports them. */
    public record DeviceStatusDto(String lockState, Integer battery, Integer linkQuality, Instant lastSeen) { }
}
