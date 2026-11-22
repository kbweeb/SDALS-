package com.cardsystem.smartdoor.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Maps a credential slot stored on the physical lock to a system user. */
@Entity
@Table(name = "credentials")
public class Credential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "credential_type", nullable = false, length = 20)
    private CredentialType type;

    /** Physical slot index on the lock. */
    @Column(name = "credential_index", nullable = false)
    private Integer index;

    /** RFID hex UID, if the lock exposes it over MQTT. */
    @Column(name = "card_uid", unique = true, length = 64)
    private String cardUid;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public Integer getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public CredentialType getType() { return type; }
    public void setType(CredentialType type) { this.type = type; }
    public Integer getIndex() { return index; }
    public void setIndex(Integer index) { this.index = index; }
    public String getCardUid() { return cardUid; }
    public void setCardUid(String cardUid) { this.cardUid = cardUid; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
