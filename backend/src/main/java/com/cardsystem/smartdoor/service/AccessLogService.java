package com.cardsystem.smartdoor.service;

import com.cardsystem.smartdoor.dto.Dtos.AccessLogDto;
import com.cardsystem.smartdoor.model.*;
import com.cardsystem.smartdoor.repository.AccessLogRepository;
import com.cardsystem.smartdoor.repository.CredentialRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;

import java.util.Locale;

/** Writes and reads the audit trail. Also covers security monitoring (tamper / denied events). */
@Service
@Transactional
public class AccessLogService {

    private final AccessLogRepository logs;
    private final CredentialRepository credentials;

    public AccessLogService(AccessLogRepository logs, CredentialRepository credentials) {
        this.logs = logs;
        this.credentials = credentials;
    }

    @Transactional(readOnly = true)
    public Page<AccessLogDto> recent(int page, int size) {
        return logs.findAllByOrderByLoggedAtDesc(PageRequest.of(page, size)).map(AccessLogDto::from);
    }

    public void record(User user, AccessMethod method, AccessStatus status, String rawPayload) {
        logs.save(new AccessLog(user, method, status, rawPayload));
    }

    /**
     * Turns a lock telemetry message into an audit entry, if it describes an access event.
     * PROVISIONAL: the real field names depend on the MOES firmware and must be confirmed
     * in Stage 3 by watching the zigbee2mqtt/smart_lock topic while using the lock.
     */
    public void recordFromTelemetry(JsonNode node, String rawPayload) {
        if (!node.hasNonNull("action")) {
            return; // plain state/battery update, not an access event
        }
        String action = node.get("action").asString().toLowerCase(Locale.ROOT);

        AccessStatus status;
        if (action.contains("tamper") || action.contains("alarm")) {
            status = AccessStatus.TAMPER;
        } else if (action.contains("fail") || action.contains("error") || action.contains("denied")) {
            status = AccessStatus.DENIED;
        } else {
            status = AccessStatus.AUTHORIZED;
        }

        AccessMethod method;
        CredentialType credentialType;
        if (action.contains("finger")) {
            method = AccessMethod.FINGERPRINT;
            credentialType = CredentialType.FINGERPRINT;
        } else if (action.contains("rfid") || action.contains("card")) {
            method = AccessMethod.RFID;
            credentialType = CredentialType.RFID_CARD;
        } else {
            method = AccessMethod.PIN;
            credentialType = CredentialType.PASSWORD;
        }

        User user = null;
        if (node.hasNonNull("action_user")) {
            user = credentials.findByTypeAndIndex(credentialType, node.get("action_user").asInt())
                    .map(Credential::getUser)
                    .orElse(null);
        }
        record(user, method, status, rawPayload);
    }
}
