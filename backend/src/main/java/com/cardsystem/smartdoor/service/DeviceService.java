package com.cardsystem.smartdoor.service;

import com.cardsystem.smartdoor.dto.Dtos.DeviceStatusDto;
import com.cardsystem.smartdoor.model.AccessMethod;
import com.cardsystem.smartdoor.model.AccessStatus;
import com.cardsystem.smartdoor.mqtt.LockGateway;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.time.Instant;

/** Keeps the latest lock state in memory and sends remote commands. */
@Service
public class DeviceService {

    private final LockGateway lockGateway;
    private final AccessLogService accessLogService;

    private volatile DeviceStatusDto status = new DeviceStatusDto(null, null, null, null);

    public DeviceService(LockGateway lockGateway, AccessLogService accessLogService) {
        this.lockGateway = lockGateway;
        this.accessLogService = accessLogService;
    }

    public DeviceStatusDto getStatus() {
        return status;
    }

    public void remoteUnlock() {
        lockGateway.unlock();
        accessLogService.record(null, AccessMethod.REMOTE, AccessStatus.AUTHORIZED, null);
    }

    public void remoteLock() {
        lockGateway.lock();
    }

    /** Field names follow common Zigbee2MQTT lock exposes; adjust after Stage 3 inspection. */
    public void updateFromTelemetry(JsonNode node) {
        DeviceStatusDto old = status;
        status = new DeviceStatusDto(
                node.hasNonNull("state") ? node.get("state").asString() : old.lockState(),
                node.hasNonNull("battery") ? node.get("battery").asInt() : old.battery(),
                node.hasNonNull("linkquality") ? node.get("linkquality").asInt() : old.linkQuality(),
                Instant.now());
    }
}
