package com.cardsystem.smartdoor.mqtt;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Connection settings for the Mosquitto broker and the Zigbee2MQTT topics of the lock.
 *
 * @param stateTopic   topic Zigbee2MQTT publishes lock telemetry on (e.g. zigbee2mqtt/smart_lock)
 * @param commandTopic topic Zigbee2MQTT listens on for commands (e.g. zigbee2mqtt/smart_lock/set)
 */
@ConfigurationProperties(prefix = "mqtt")
public record MqttProperties(
        String brokerUrl,
        String clientId,
        String username,
        String password,
        String stateTopic,
        String commandTopic) { }
