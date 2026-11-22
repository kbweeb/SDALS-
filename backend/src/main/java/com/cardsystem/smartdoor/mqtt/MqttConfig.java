package com.cardsystem.smartdoor.mqtt;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

/** Wires the backend to the Mosquitto broker: one inbound (telemetry) and one outbound (commands) channel. */
@Configuration
public class MqttConfig {

    public static final String INBOUND_CHANNEL = "mqttInboundChannel";
    public static final String OUTBOUND_CHANNEL = "mqttOutboundChannel";

    @Bean
    public MqttPahoClientFactory mqttClientFactory(MqttProperties props) {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[] { props.brokerUrl() });
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        if (props.username() != null && !props.username().isBlank()) {
            options.setUserName(props.username());
            options.setPassword(props.password() == null ? new char[0] : props.password().toCharArray());
        }
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean(name = INBOUND_CHANNEL)
    public MessageChannel mqttInboundChannel() {
        return new DirectChannel();
    }

    @Bean(name = OUTBOUND_CHANNEL)
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    /** Subscribes to the lock's telemetry topic and pushes each message onto the inbound channel. */
    @Bean
    public MessageProducer mqttInboundAdapter(MqttPahoClientFactory factory, MqttProperties props) {
        var adapter = new MqttPahoMessageDrivenChannelAdapter(props.clientId() + "-in", factory, props.stateTopic());
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInboundChannel());
        return adapter;
    }

    /** Publishes anything sent to the outbound channel to the lock's command topic. */
    @Bean
    @ServiceActivator(inputChannel = OUTBOUND_CHANNEL)
    public MessageHandler mqttOutboundHandler(MqttPahoClientFactory factory, MqttProperties props) {
        var handler = new MqttPahoMessageHandler(props.clientId() + "-out", factory);
        handler.setAsync(true);
        handler.setDefaultTopic(props.commandTopic());
        handler.setDefaultQos(1);
        return handler;
    }
}
