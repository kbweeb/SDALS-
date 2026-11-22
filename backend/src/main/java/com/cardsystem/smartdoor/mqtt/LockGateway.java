package com.cardsystem.smartdoor.mqtt;

/**
 * Outbound port to the physical lock. Services depend on this interface only,
 * so they never know about MQTT or ZigBee and can be tested without a broker.
 */
public interface LockGateway {

    void unlock();

    void lock();
}
