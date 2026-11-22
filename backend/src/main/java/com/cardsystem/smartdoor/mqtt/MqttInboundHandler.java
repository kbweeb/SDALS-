package com.cardsystem.smartdoor.mqtt;

import com.cardsystem.smartdoor.service.AccessLogService;
import com.cardsystem.smartdoor.service.DeviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Entry point for telemetry coming from the lock. Only parses the payload and hands it
 * to the service layer; it never touches repositories directly.
 */
@Component
public class MqttInboundHandler {

    private static final Logger log = LoggerFactory.getLogger(MqttInboundHandler.class);

    private final JsonMapper jsonMapper;
    private final DeviceService deviceService;
    private final AccessLogService accessLogService;

    public MqttInboundHandler(JsonMapper jsonMapper, DeviceService deviceService, AccessLogService accessLogService) {
        this.jsonMapper = jsonMapper;
        this.deviceService = deviceService;
        this.accessLogService = accessLogService;
    }

    @ServiceActivator(inputChannel = MqttConfig.INBOUND_CHANNEL)
    public void handle(String payload) {
        JsonNode node;
        try {
            node = jsonMapper.readTree(payload);
        } catch (JacksonException e) {
            log.warn("Ignoring non-JSON lock payload: {}", payload);
            return;
        }
        deviceService.updateFromTelemetry(node);
        accessLogService.recordFromTelemetry(node, payload);
    }
}
