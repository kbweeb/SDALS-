package com.cardsystem.smartdoor.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Append-only audit record of an access or security event. */
@Entity
@Table(name = "access_logs")
public class AccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_method", nullable = false, length = 20)
    private AccessMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccessStatus status;

    /** Original MQTT payload, kept for debugging. */
    @Column(name = "raw_payload", columnDefinition = "TEXT")
    private String rawPayload;

    @Column(name = "logged_at", insertable = false, updatable = false)
    private LocalDateTime loggedAt;

    protected AccessLog() { }

    public AccessLog(User user, AccessMethod method, AccessStatus status, String rawPayload) {
        this.user = user;
        this.method = method;
        this.status = status;
        this.rawPayload = rawPayload;
    }

    public Integer getId() { return id; }
    public User getUser() { return user; }
    public AccessMethod getMethod() { return method; }
    public AccessStatus getStatus() { return status; }
    public String getRawPayload() { return rawPayload; }
    public LocalDateTime getLoggedAt() { return loggedAt; }
}
