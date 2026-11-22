package com.cardsystem.smartdoor.mqtt;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * Sends lock commands to Zigbee2MQTT. Payload format is the Zigbee2MQTT "set" convention;
 * confirm the exact field the MOES lock exposes during Stage 3 (lock pairing and inspection).
 */
@Component
public class MqttLockGateway implements LockGateway {

    private final MessageChannel outbound;

    public MqttLockGateway(@Qualifier(MqttConfig.OUTBOUND_CHANNEL) MessageChannel outbound) {
        this.outbound = outbound;
    }

    @Override
    public void unlock() {
        publish("{\"state\":\"UNLOCK\"}");
    }

    @Override
    public void lock() {
        publish("{\"state\":\"LOCK\"}");
    }

    private void publish(String json) {
        outbound.send(MessageBuilder.withPayload(json).build());
    }
}
